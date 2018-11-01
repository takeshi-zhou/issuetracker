package cn.edu.fudan.scanservice.service.impl;


import cn.edu.fudan.scanservice.domain.ScanMessage;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author WZY
 * @version 1.0
 **/
@Service
public class KafkaServiceImpl implements KafkaService {

    private Logger logger = LoggerFactory.getLogger(ScanServiceImpl.class);
    @Value("${commit.service.path}")
    private String commitServicePath;
    @Value("${inner.service.path}")
    private String innerServicePath;

    private HttpHeaders httpHeaders;
    private FindBugScanTask findBugScanTask;
    private CloneScanTask cloneScanTask;
    private RestTemplate restTemplate;

    @Autowired
    public KafkaServiceImpl(HttpHeaders httpHeaders,
                            FindBugScanTask findBugScanTask,
                            CloneScanTask cloneScanTask,
                            RestTemplate restTemplate) {
        this.httpHeaders = httpHeaders;
        this.findBugScanTask = findBugScanTask;
        this.cloneScanTask = cloneScanTask;
        this.restTemplate = restTemplate;
    }

    //初始化project的一些状态,表示目前正在scan
    private void initialProject(String projectId) {
        JSONObject postData = new JSONObject();
        postData.put("uuid", projectId);
        postData.put("scan_status", "Scanning");
        updateProject(postData);
    }

    private void updateProject(JSONObject projectParam) {
        try {
            HttpEntity<Object> entity = new HttpEntity<>(projectParam, httpHeaders);
            restTemplate.exchange(innerServicePath + "/inner/project", HttpMethod.PUT, entity, JSONObject.class);
        } catch (Exception e) {
            throw new RuntimeException("project update failed!");
        }
    }

    private void updateProjects(String repo_id, JSONObject projectParam,String expected_type) {
        try {
            HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
            JSONArray projects = restTemplate.exchange(innerServicePath + "/inner/project?repo_id=" + repo_id, HttpMethod.GET, entity, JSONArray.class).getBody();
            if (projects != null && !projects.isEmpty()) {
                for (int i = 0; i < projects.size(); i++) {
                    JSONObject project=projects.getJSONObject(i);
                    String project_id = project.getString("uuid");
                    String type=project.getString("type");
                    if(type.equals(expected_type)){
                        projectParam.put("uuid", project_id);
                        updateProject(projectParam);
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
    @SuppressWarnings("unchecked")
    @Override
    public void scanByRequest(JSONObject requestParam) {
        String category=requestParam.getString("category");
        if(category==null){
            category="bug";
        }
        String projectId = requestParam.getString("projectId");
        String commitId = requestParam.getString("commitId");
        initialProject(projectId);
        HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
        String repoId = restTemplate.exchange(innerServicePath + "/inner/project/repo-id?project-id=" + projectId, HttpMethod.GET, entity, String.class).getBody();
        if(category.equals("clone")){
            cloneScanTask.run(repoId,commitId,category);
        }else{
            Future<String> future = findBugScanTask.run(repoId, commitId,category);
            //开一个工作者线程来管理异步任务的超时
            setTimeOut(future, repoId);
        }
    }

    /**
     * 监听消息队列中project的scan消息触发scan操作,暂时没有用到
     *
     * @author WZY
     */
    @Override
    @KafkaListener(id = "projectScan", topics = {"Scan"}, groupId = "scan")
    public void scanByMQ(ConsumerRecord<String, String> consumerRecord) {
        String msg = consumerRecord.value();
        logger.info("received message from topic -> " + consumerRecord.topic() + " : " + msg);
        ScanMessage scanMessage = JSONObject.parseObject(msg, ScanMessage.class);
        String repoId = scanMessage.getRepoId();
        String commitId = scanMessage.getCommitId();
        String category=scanMessage.getCategory();
        Future<String> future = findBugScanTask.run(repoId, commitId,category);
        setTimeOut(future, repoId);
        cloneScanTask.run(repoId,commitId,"clone");
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
        JSONObject commitResponse = restTemplate.getForObject(commitServicePath + "/commit-time?commit_id=" + commitId, JSONObject.class);
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
                HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
                restTemplate.exchange(innerServicePath + "/inner/raw-issue/" + repoId, HttpMethod.DELETE, entity, JSONObject.class);
            }
            projectParam.put("scan_status", scanResult.getDescription());
        }
        updateProjects(repoId, projectParam,type);
    }
}
