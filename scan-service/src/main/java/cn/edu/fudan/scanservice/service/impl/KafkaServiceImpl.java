package cn.edu.fudan.scanservice.service.impl;


import cn.edu.fudan.scanservice.component.RestInterfaceManager;
import cn.edu.fudan.scanservice.domain.ScanMessage;
import cn.edu.fudan.scanservice.domain.ScanMessageWithTime;
import cn.edu.fudan.scanservice.domain.ScanResult;
import cn.edu.fudan.scanservice.service.KafkaService;
import cn.edu.fudan.scanservice.task.CloneScanTask;
import cn.edu.fudan.scanservice.task.FindBugScanTask;
import cn.edu.fudan.scanservice.util.DateTimeUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * @author WZY
 * @version 1.0
 **/
@Service
public class KafkaServiceImpl implements KafkaService {

    private Logger logger = LoggerFactory.getLogger(ScanServiceImpl.class);

    private FindBugScanTask findBugScanTask;
    private CloneScanTask cloneScanTask;
    private RestInterfaceManager restInterfaceManager;
    private KafkaTemplate kafkaTemplate;

    @Autowired
    public KafkaServiceImpl(FindBugScanTask findBugScanTask,
                            CloneScanTask cloneScanTask,
                            RestInterfaceManager restInterfaceManager,
                            KafkaTemplate kafkaTemplate) {
        this.findBugScanTask = findBugScanTask;
        this.cloneScanTask = cloneScanTask;
        this.restInterfaceManager=restInterfaceManager;
        this.kafkaTemplate=kafkaTemplate;
    }

    //初始化project的一些状态,表示目前正在scan
    private void initialProject(String projectId) {
        JSONObject postData = new JSONObject();
        postData.put("uuid", projectId);
        postData.put("scan_status", "Scanning");
        restInterfaceManager.updateProject(postData);
    }



    private void updateProjects(String repo_id, JSONObject projectParam,String expected_type) {
        try {
            JSONArray projects = restInterfaceManager.getProjectsOfRepo(repo_id);
            if (projects != null && !projects.isEmpty()) {
                for (int i = 0; i < projects.size(); i++) {
                    JSONObject project=projects.getJSONObject(i);
                    String project_id = project.getString("uuid");
                    String type=project.getString("type");
                    if(type.equals(expected_type)){
                        projectParam.put("uuid", project_id);
                        restInterfaceManager.updateProject(projectParam);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("project update failed!");
        }
    }

    private void setTimeOut(Future<?> future, String repoId) {
        new Thread(() -> {
            try {
                future.get(15, TimeUnit.MINUTES);//设置15分钟的超时时间
            } catch (TimeoutException e) {
                //因scan超时而抛出异常
                logger.error("超时了");
                future.cancel(false);
                JSONObject projectParam = new JSONObject();
                projectParam.put("scan_status", "Scan Time Out");
                updateProjects(repoId, projectParam,"findbug");
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 通过前台请求触发scan操作
     *
     * @author WZY
     */
    @Override
    public void scanByRequest(JSONObject requestParam) {
        String category=requestParam.getString("category");
        if(category==null){
            category="bug";
        }
        String projectId = requestParam.getString("projectId");
        String commitId = requestParam.getString("commitId");
        if(projectId==null||projectId.equals(""))
            throw new IllegalArgumentException("please provide projectId");
        if(commitId==null||commitId.equals(""))
            throw new IllegalArgumentException("please provide commitId");
        initialProject(projectId);
        String repoId = restInterfaceManager.getRepoIdOfProject(projectId);
        if(category.equals("clone")){
            Future<String> future =cloneScanTask.runAsync(repoId,commitId,category);
            setTimeOut(future, repoId);
        }else{
            Future<String> future = findBugScanTask.runAsync(repoId, commitId,category);
            //开一个工作者线程来管理异步任务的超时
            setTimeOut(future, repoId);
        }
    }

    /**
     * 监听消息队列中project的scan消息触发scan操作
     *
     * @author WZY
     */
    @Override
    @SuppressWarnings("unchecked")
    @KafkaListener(id = "projectScan", topics = {"Scan"}, groupId = "scan")
    public void scanByMQ(ConsumerRecord<String, String> consumerRecord) {
        String msg = consumerRecord.value();
        logger.info("received message from topic -> " + consumerRecord.topic() + " : " + msg);
        ScanMessage scanMessage = JSONObject.parseObject(msg, ScanMessage.class);
        String repoId = scanMessage.getRepoId();
        String commitId = scanMessage.getCommitId();
        //串行扫
        if(existProject(repoId,"bug",false))
            findBugScanTask.runSynchronously(repoId, commitId,"bug");
        if(existProject(repoId,"clone",false))
            cloneScanTask.runSynchronously(repoId,commitId,"clone");
        List<ScanMessageWithTime> list=new ArrayList<>();
        ScanMessageWithTime scanMessageWithTime=new ScanMessageWithTime(repoId,commitId);
        scanMessageWithTime.setCommitTime(restInterfaceManager.getCommitTime(commitId).getJSONObject("data").getString("commit_time"));
        list.add(scanMessageWithTime);
        //发送消息给度量服务，将度量信息保存
        kafkaTemplate.send("Measure",JSONArray.toJSONString(list));
    }

    @KafkaListener(id = "updateCommit", topics = {"UpdateCommit"}, groupId = "updateCommit")
    public void firstScanByMQ(ConsumerRecord<String, String> consumerRecord) {
        String msg = consumerRecord.value();
        List<ScanMessageWithTime> commits=JSONArray.parseArray(msg,ScanMessageWithTime.class);
        int size=commits.size();
        logger.info("received message from topic -> " + consumerRecord.topic() + " : " + size+" commits need to scan!");
        if(!commits.isEmpty()){
            Map<LocalDate,List<ScanMessageWithTime>> map=commits.stream().collect(Collectors.groupingBy((ScanMessageWithTime scanMessageWithTime)->{
                String dateStr=scanMessageWithTime.getCommitTime().split(" ")[0];
                return LocalDate.parse(dateStr,DateTimeUtil.Y_M_D_formatter);
            }));
            List<LocalDate> dates=new ArrayList<>(map.keySet());
            dates.sort(((o1, o2) -> {
                if(o1.equals(o2))
                    return 0;
                return o1.isBefore(o2)?-1:1;
            }));
            firstAutoScan(map,dates);
        }
    }

    @SuppressWarnings("unchecked")
    private void firstAutoScan(Map<LocalDate,List<ScanMessageWithTime>> map,List<LocalDate> dates){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<ScanMessageWithTime> filteredCommits=getFilteredList(map,dates);
        String repoId=filteredCommits.get(0).getRepoId();
        logger.info("{} need to auto scan!",repoId);
        logger.info(filteredCommits.size()+" commits need to scan after filtered!");
        //当前repo_id和type的project存在，并且没被自动扫描过
        boolean existBugProject=existProject(repoId,"bug",true);
        logger.info("existBugProject -> {}",existBugProject);
        boolean existCloneProject=existProject(repoId,"clone",true);
        logger.info("existCloneProject -> {}",existCloneProject);
        if(existBugProject){
            logger.info("start auto scan bug -> {}",repoId);
            for(ScanMessageWithTime message:filteredCommits){
                String commitId = message.getCommitId();
                findBugScanTask.runSynchronously(repoId,commitId,"bug");
            }
            restInterfaceManager.updateFirstAutoScannedToTrue(repoId,"bug");
        }else{
            logger.info("repo {} not exist or has been auto scanned!",repoId);
        }
        if(existCloneProject){
            logger.info("start auto scan clone -> {}",repoId);
            for(ScanMessageWithTime message:filteredCommits){
                String commitId = message.getCommitId();
                cloneScanTask.runSynchronously(repoId,commitId,"clone");
            }
            restInterfaceManager.updateFirstAutoScannedToTrue(repoId,"clone");
        }else{
            logger.info("repo {} not exist or has been auto scanned!",repoId);
        }
        //发送消息给度量服务，将度量信息保存
        kafkaTemplate.send("Measure",JSONArray.toJSONString(filteredCommits));
    }

    private boolean existProject(String repoId,String category,boolean isFirst){
        JSONObject response=restInterfaceManager.existThisProject(repoId, category,isFirst);
        if(response!=null){
            logger.info(response.toJSONString());
            return response.getBooleanValue("exist");
        }else{
            logger.info("response is null");
            return false;
        }
    }

    private List<ScanMessageWithTime> getFilteredList(Map<LocalDate,List<ScanMessageWithTime>> map,List<LocalDate> dates){
        int sourceSize=dates.size();
        LocalDate nextTimeLimit=dates.get(sourceSize-1).minusMonths(1);
        LinkedList<ScanMessageWithTime> result=new LinkedList<>();
        int i=0;
        boolean isRecent=true;
        while(i<sourceSize){
            LocalDate date=dates.get(sourceSize-1-i);
            if(isRecent&&date.isAfter(nextTimeLimit)){
                List<ScanMessageWithTime> list=map.get(date);
                result.addFirst(list.get(list.size()-1));
            }else{
                isRecent=false;
            }
            if(!isRecent){
                if(date.isBefore(nextTimeLimit)||i==sourceSize-1){
                    List<ScanMessageWithTime> list=map.get(date);
                    result.addFirst(list.get(list.size()-1));
                    nextTimeLimit=nextTimeLimit.minusMonths(3);
                }
            }
            i++;
        }
        return result;
    }

    /**
     * 根据扫描的结果更新project的状态
     *
     * @author WZY
     */
    @Override
    @KafkaListener(id = "scanResult", topics = {"ScanResult"}, groupId = "scanResult")
    public void updateCommitScanStatus(ConsumerRecord<String, String> consumerRecord) {
        String msg = consumerRecord.value();
        logger.info("received message from topic -> " + consumerRecord.topic() + " : " + msg);
        JSONObject projectParam = new JSONObject();
        ScanResult scanResult = JSONObject.parseObject(msg, ScanResult.class);
        String repoId = scanResult.getRepoId();
        String commitId = scanResult.getCommitId();
        String type=scanResult.getType();
        JSONObject commitResponse = restInterfaceManager.getCommitTime(commitId);
        if (commitResponse != null) {
            String commit_time = commitResponse.getJSONObject("data").getString("commit_time");
            projectParam.put("till_commit_time", commit_time);
        }
        projectParam.put("last_scan_time", DateTimeUtil.format(new Date()));
        if (scanResult.getStatus().equals("success")) {
            projectParam.put("scan_status", "Scanned");
        } else {
            if (scanResult.getDescription().equals("Mapping failed")) {
                //mapping 失败，删除当前repo所扫commit得到的rawIssue和location
                restInterfaceManager.deleteRawIssueOfRepo(repoId,type);
            }
            projectParam.put("scan_status", scanResult.getDescription());
        }
        updateProjects(repoId, projectParam,type);
    }
}
