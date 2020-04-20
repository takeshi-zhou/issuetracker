package cn.edu.fudan.scanservice.service.impl;


import cn.edu.fudan.scanservice.component.strategy.CommitFilterStrategy;
import cn.edu.fudan.scanservice.component.rest.RestInterfaceManager;
import cn.edu.fudan.scanservice.dao.ScanDao;
import cn.edu.fudan.scanservice.domain.Scan;
import cn.edu.fudan.scanservice.domain.ScanMessage;
import cn.edu.fudan.scanservice.domain.ScanMessageWithTime;
import cn.edu.fudan.scanservice.domain.ScanResult;
import cn.edu.fudan.scanservice.service.KafkaService;
import cn.edu.fudan.scanservice.task.CloneScanTask;
import cn.edu.fudan.scanservice.task.FindBugScanTask;
import cn.edu.fudan.scanservice.task.SonarScanTask;
import cn.edu.fudan.scanservice.util.DateTimeUtil;
import cn.edu.fudan.scanservice.util.ExecuteShellUtil;
import cn.edu.fudan.scanservice.util.JGitHelper;
import cn.edu.fudan.scanservice.util.SearchUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private SonarScanTask sonarScanTask;
    private RestInterfaceManager restInterfaceManager;
    private KafkaTemplate kafkaTemplate;
    private ScanDao scanDao;


    private ExecuteShellUtil executeShellUtil;

    @Autowired
    public KafkaServiceImpl(FindBugScanTask findBugScanTask,
                            CloneScanTask cloneScanTask,
                            RestInterfaceManager restInterfaceManager,
                            KafkaTemplate kafkaTemplate,
                            SonarScanTask sonarScanTask,
                            ScanDao scanDao,
                            ExecuteShellUtil executeShellUtil) {
        this.findBugScanTask = findBugScanTask;
        this.cloneScanTask = cloneScanTask;
        this.restInterfaceManager=restInterfaceManager;
        this.kafkaTemplate=kafkaTemplate;
        this.sonarScanTask=sonarScanTask;
        this.scanDao = scanDao;
        this.executeShellUtil = executeShellUtil;
    }

    private CommitFilterStrategy<ScanMessageWithTime> commitFilter;

    @Autowired
    @Qualifier("AACS")
    public void setCommitFilter(CommitFilterStrategy<ScanMessageWithTime> commitFilter) {
        this.commitFilter = commitFilter;
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

//                    String type=project.getString("type");
//                    if(type.equals(expected_type)){
                        projectParam.put("uuid", project_id);
                        restInterfaceManager.updateProject(projectParam);
                    //}
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
        if(projectId==null||projectId.equals("")) {
            throw new IllegalArgumentException("please provide projectId");
        }
        if(commitId==null||commitId.equals("")) {
            throw new IllegalArgumentException("please provide commitId");
        }
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
    @KafkaListener(id = "projectScan", topics = {"Scan"}, groupId = "scan")
    public void scanByMQ(ConsumerRecord<String, String> consumerRecord) {
        String msg = consumerRecord.value();
        logger.info("received message from topic -> " + consumerRecord.topic() + " : " + msg);
        ScanMessage scanMessage = JSONObject.parseObject(msg, ScanMessage.class);
        String repoId = scanMessage.getRepoId();
        String commitId = scanMessage.getCommitId();
        List<ScanMessageWithTime> list=new ArrayList<>();
        ScanMessageWithTime scanMessageWithTime=new ScanMessageWithTime(repoId,commitId);
        JSONObject commitTimeJson = restInterfaceManager.getCommitTime(commitId,repoId);
        if(commitTimeJson == null){
            sendToScanResult(repoId,commitId,"bug","failed","request base server failed");
            return;
        }
        scanMessageWithTime.setCommitTime(commitTimeJson.getJSONObject("data").getString("commit_time"));
        list.add(scanMessageWithTime);
        //串行扫
        try{
            if(existProject(repoId,"bug",false)){
                findBugScanTask.runSynchronously(repoId, commitId,"bug");

                updateCodeTracker(repoId);
                checkOutCodeTrackerStatus(repoId);

                sendMessageToMeasure(repoId,list);

                cloneScanTask.runSynchronously(repoId,commitId,"clone");
                sendMessageToClone(repoId,list);

            }
        }catch (RuntimeException e){
            if("request base server failed".equals(e.getMessage())){
                sendToScanResult(repoId,commitId,"bug","failed","request base server failed");
            }
        }

//        if(existProject(repoId,"clone",false)){
//
//        }
    }

    @SuppressWarnings("unchecked")
    private void sendMessageToMeasure(String repoId,List<ScanMessageWithTime> list){
        //发送消息给度量服务，将度量信息保存
        kafkaTemplate.send("Measure",JSONArray.toJSONString(list));
        logger.info("message has been send to topic Measure -> {}",repoId);
    }

    @SuppressWarnings("unchecked")
    private void sendMessageToClone(String repoId,List<ScanMessageWithTime> list){
        JSONObject cloneInfo =new JSONObject();
        cloneInfo.put("repoId",repoId);
        cloneInfo.put("commitList",list.stream().map(ScanMessageWithTime::getCommitId).collect(Collectors.toList()));
        //发送消息给clone服务，将度量信息保存
        kafkaTemplate.send("CloneZNJ",JSONObject.toJSONString(cloneInfo));
        logger.info("message has been send to topic Clone -> {}",repoId);
    }

    private void sendToScanResult(String repoId, String commitId,String category,String status, String description) {
        ScanResult scanResult = new ScanResult(repoId, commitId, category,status, description);
        kafkaTemplate.send("ScanResult", JSONObject.toJSONString(scanResult));
    }

    @Override
    @KafkaListener(id = "updateCommit", topics = {"UpdateCommit"}, groupId = "updateCommit")
    public void firstScanByMQ(ConsumerRecord<String, String> consumerRecord) {
        String msg = consumerRecord.value();
        List<ScanMessageWithTime> commits=JSONArray.parseArray(msg,ScanMessageWithTime.class);
        commits = commits.stream().distinct().collect(Collectors.toList());
        int size=commits.size();
        logger.info("received message from topic -> " + consumerRecord.topic() + " : " + size+" commits need to scan!");
        if(!commits.isEmpty()){
            Map<LocalDate,List<ScanMessageWithTime>> map=commits.stream().collect(Collectors.groupingBy((ScanMessageWithTime scanMessageWithTime)->{
                String dateStr=scanMessageWithTime.getCommitTime().split(" ")[0];
                return LocalDate.parse(dateStr,DateTimeUtil.Y_M_D_formatter);
            }));
            List<LocalDate> dates=new ArrayList<>(map.keySet());
            dates.sort(((o1, o2) -> {
                if(o1.equals(o2)) {
                    return 0;
                }
                return o1.isBefore(o2)?-1:1;
            }));
            firstAutoScan(map,dates);
        }
    }


    @Override
    public String startAutoScan(String repoId,String commitId) {
        String branch = null;
        JSONArray projects = restInterfaceManager.getProjectsOfRepo(repoId);
        if(!projects.isEmpty()){
            branch = projects.getJSONObject(0).getString("branch");
        }else{
            return "can't get branch";
        }

        String firstScanCommitId = null;
        if(commitId == null){
            firstScanCommitId = getBaseCommitId(repoId,branch);
        }else{
            firstScanCommitId = commitId;
        }

        if(firstScanCommitId == null){
            return "can't find commit.";
        }



        String repoPath = null;
        List<String>  scanCommits = new ArrayList<>();

        try {

            repoPath = restInterfaceManager.getRepoPath(repoId, firstScanCommitId);
            if (repoPath == null) {
                throw new RuntimeException("request base server failed");
            }
            JGitHelper jGitHelper = new JGitHelper(repoPath);
            jGitHelper.checkoutToLatestCommit(branch);
            scanCommits = jGitHelper.getCommitListByBranchAndBeginCommit(branch,firstScanCommitId);


        }finally {
            if (repoPath != null) {
                restInterfaceManager.freeRepoPath(repoId, repoPath);
            }
        }


        //sonar扫描
        boolean existedForSonar = (existProject(repoId,"sonar",false)||existProject(repoId,"sonar",true));
        if(existedForSonar){

            if(scanCommits.isEmpty()){
                return "scan commit list is empty. ";
            }
            Map<String,String> sonarResultMap = analyzeProjectIsScanned(repoId,"sonar");

            if(Boolean.parseBoolean(sonarResultMap.get("isFirst"))){

                logger.info("start auto scan sonar -> {}",repoId);
                for(String commit:scanCommits){

                    sonarScanTask.runSynchronously(repoId,commit,"sonar");
                }
                restInterfaceManager.updateFirstAutoScannedToTrue(repoId,"sonar");
            }
        }else{
            logger.info("repo {} not exist or has been auto scanned!",repoId);
        }

        return "scan success";

    }

    private void firstAutoScan(Map<LocalDate,List<ScanMessageWithTime>> map,List<LocalDate> dates){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<ScanMessageWithTime> filteredCommits;

        String repoId=map.get(dates.get(0)).get(0).getRepoId();

        try{
            //bug扫描
            boolean existedForBug =(existProject(repoId,"bug",false)||existProject(repoId,"bug",true));
            if(existedForBug){
                boolean isSendMsgToCodeTracker = false;
                String firstScanCommit = null;
                if(existProject(repoId,"bug",true)){
                    filteredCommits=commitFilter.filter(map,dates);
                    if(filteredCommits == null || filteredCommits.isEmpty()){
                        return ;
                    }
                    isSendMsgToCodeTracker = true;
                    firstScanCommit = filteredCommits.get(0).getCommitId();

                }else{
                    filteredCommits = addAllCommits(map,dates);
                }
                logger.info("{} need to auto scan!",repoId);
                logger.info(filteredCommits.size()+" commits need to scan after filtered!");


                Map<String,String> bugResultMap = analyzeProjectIsScanned(repoId,"bug",filteredCommits);

                if(Boolean.parseBoolean(bugResultMap.get("isFirst"))){
                    List<ScanMessageWithTime> bugFilterCommits = filteredCommits;
                    if(bugResultMap.get("location") != null){
                        bugFilterCommits = updateFilterCommits(filteredCommits,Integer.parseInt(bugResultMap.get("location")));
                    }
                    logger.info("start auto scan bug -> {}",repoId);
                    for(ScanMessageWithTime message:bugFilterCommits){
                        String commitId = message.getCommitId();
                        findBugScanTask.runSynchronously(repoId,commitId,"bug");
                    }

                    logger.info("findbugs scan finished... start code tracker...");
                    sendMsgToCodeTracker(isSendMsgToCodeTracker,firstScanCommit,repoId);

                    checkOutCodeTrackerStatus(repoId);

                    for(ScanMessageWithTime message:bugFilterCommits){
                        String commitId = message.getCommitId();
                        cloneScanTask.runSynchronously(repoId,commitId,"clone");
                    }
                    //restInterfaceManager.updateFirstAutoScannedToTrue(repoId,"clone");

                    sendMessageToMeasure(repoId,bugFilterCommits);

                    restInterfaceManager.updateFirstAutoScannedToTrue(repoId,"bug");
                }

            }else{
                logger.info("repo {} not exist or has been auto scanned!",repoId);
            }
            //clone扫描
            boolean existedForClone = (existProject(repoId,"clone",false)||existProject(repoId,"clone",true));
            if(existedForClone){
                if(existProject(repoId,"clone",true)){
                    filteredCommits=commitFilter.filter(map,dates);
                }else{
                    filteredCommits = addAllCommits(map,dates);
                }
                logger.info("{} need to auto scan!",repoId);
                logger.info(filteredCommits.size()+" commits need to scan after filtered!");

                if(filteredCommits.isEmpty()){
                    return ;
                }
                Map<String,String> cloneResultMap = analyzeProjectIsScanned(repoId,"clone",filteredCommits);

                if(Boolean.parseBoolean(cloneResultMap.get("isFirst"))){
                    List<ScanMessageWithTime> cloneFilterCommits = filteredCommits;
                    if(cloneResultMap.get("location") != null){
                        cloneFilterCommits = updateFilterCommits(filteredCommits,Integer.parseInt(cloneResultMap.get("location")));
                    }
                    logger.info("start auto scan clone -> {}",repoId);
                    for(ScanMessageWithTime message:cloneFilterCommits){
                        String commitId = message.getCommitId();
                        cloneScanTask.runSynchronously(repoId,commitId,"clone");
                    }
                    restInterfaceManager.updateFirstAutoScannedToTrue(repoId,"clone");
                    sendMessageToClone(repoId,cloneFilterCommits);
                }


            }else{
                logger.info("repo {} not exist or has been auto scanned!",repoId);
            }
            //sonar扫描
            boolean existedForSonar = (existProject(repoId,"sonar",false)||existProject(repoId,"sonar",true));
            if(existedForSonar){
                if(existProject(repoId,"sonar",true)){
                    filteredCommits=commitFilter.filter(map,dates);
                }else{
                    filteredCommits = addAllCommits(map,dates);
                }
                logger.info("{} need to auto scan!",repoId);
                logger.info(filteredCommits.size()+" commits need to scan after filtered!");

                if(filteredCommits.isEmpty()){
                    return ;
                }
                Map<String,String> sonarResultMap = analyzeProjectIsScanned(repoId,"sonar",filteredCommits);

                if(Boolean.parseBoolean(sonarResultMap.get("isFirst"))){
                    List<ScanMessageWithTime> sonarFilterCommits = filteredCommits;
                    if(sonarResultMap.get("location") != null){
                        sonarFilterCommits = updateFilterCommits(filteredCommits,Integer.parseInt(sonarResultMap.get("location")));
                    }
                    logger.info("start auto scan sonar -> {}",repoId);
                    for(ScanMessageWithTime message:sonarFilterCommits){
                        String commitId = message.getCommitId();
                        sonarScanTask.runSynchronously(repoId,commitId,"sonar");
                    }
                    restInterfaceManager.updateFirstAutoScannedToTrue(repoId,"sonar");
                }
            }else{
                logger.info("repo {} not exist or has been auto scanned!",repoId);
            }
        }catch (RuntimeException e){
            e.printStackTrace();
            logger.error("something wrong...");
            scanDao.deleteScanByRepoIdAndCategory(repoId,"test");
            if("request base server failed".equals(e.getMessage())){
                sendToScanResult(repoId,"","bug","failed","request base server failed");
            }
        }


    }

    private Map<String,String> analyzeProjectIsScanned(String repoId,String category,List<ScanMessageWithTime> filteredCommits){
        Map<String,String> result = new HashMap<>();
        List<Scan> oldScans = scanDao.getScans(repoId,category);
        if(oldScans == null || oldScans.isEmpty()){
            result.put("isFirst",String.valueOf(true));
            return result;
        }
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
        Date lastCommitTime = oldScans.get(oldScans.size()-1).getCommit_time();
        String lastCommitTimeString = sdf.format(lastCommitTime);
        if(lastCommitTimeString.compareTo(filteredCommits.get(0).getCommitTime()) <0){
            result.put("isFirst",String.valueOf(true));
            return result;
        }
        //*
        if(lastCommitTimeString.compareTo(filteredCommits.get(filteredCommits.size()-1).getCommitTime()) >=0){
            result.put("isFirst",String.valueOf(false));
            return result;
        }

        String[] filterCommitTimes = new String[filteredCommits.size()];
        int iterator = 0;
        for(ScanMessageWithTime scanMessageWithTime : filteredCommits){
            filterCommitTimes[iterator] = scanMessageWithTime.getCommitTime();
            iterator++;
        }
        Map<String,String> searchResult = SearchUtil.dichotomy(filterCommitTimes,lastCommitTimeString);
        int filterIndex=0;
        if(Boolean.parseBoolean(searchResult.get("matching"))){
            //因为在上面*出判断大于等于0，故此处不会超过下标界限
            filterIndex = Integer.parseInt(searchResult.get("location"))+1;
        }else{
            filterIndex = Integer.parseInt(searchResult.get("location"));
        }
        result.put("isFirst",String.valueOf(true));
        result.put("location",String.valueOf(filterIndex));
        return result;
    }


    private Map<String,String> analyzeProjectIsScanned(String repoId,String category) {
        Map<String, String> result = new HashMap<>();
        List<Scan> oldScans = scanDao.getScans(repoId, category);
        if (oldScans == null || oldScans.isEmpty()) {
            result.put("isFirst", String.valueOf(true));
            return result;
        }else{
            result.put("isFirst", String.valueOf(false));
            return result;
        }
    }




    private List<ScanMessageWithTime> updateFilterCommits(List<ScanMessageWithTime> filteredCommits,int index){
        List<ScanMessageWithTime> list = new ArrayList<>();
        int i=0;
        for(ScanMessageWithTime scanMessageWithTime : filteredCommits){
            if(i>=index){
                list.add(scanMessageWithTime);
            }
            i++;
        }
        return list;
    }


    private boolean sendMsgToCodeTracker(boolean isSendMsgToCodeTracker , String firstScanCommit,String repoId){
        boolean  result = false;
        if(isSendMsgToCodeTracker){
            JSONArray projectList = restInterfaceManager.getProjectsOfRepo(repoId);
            if(!projectList.isEmpty()){
                JSONObject project = projectList.getJSONObject(0);
                String branch = project.getString("branch");
                result = restInterfaceManager.startCodeTracker(repoId,branch,firstScanCommit);
            }
        }
        return result;
    }

    private boolean checkOutCodeTrackerStatus(String repoId){
        logger.info("start check code tracker status .....");
        boolean  result = true;
        String status = null;
        JSONArray projectList = restInterfaceManager.getProjectsOfRepo(repoId);
        if(!projectList.isEmpty()){
            JSONObject project = projectList.getJSONObject(0);
            String branch = project.getString("branch");
            status = restInterfaceManager.getCodeTrackerStatus(repoId,branch);
            if(status == null  || "null".equals(status)){
                logger.warn("repo id ---> {} , had not yet begun starting code tracker scanning!",repoId);
            }else if("scanning".equals(status)){
                logger.info("repo id ---> {} ,  code tracker is  scanning! ",repoId);
                while("scanning".equals(status)){
                    try {
                        TimeUnit.MINUTES.sleep(1);
                        status = restInterfaceManager.getCodeTrackerStatus(repoId,branch);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                logger.info("repo id ---> {} ,  code tracker finished scan! ",repoId);
            }else if("scanned".equals(status)){
                logger.info("repo id ---> {} ,  code tracker finished scan! ",repoId);
            }else{
                logger.info("repo id ---> {} ,  code tracker scan status -->{} ",repoId,status);
            }
        }

        return result;
    }


    private boolean updateCodeTracker(String repoId){
        boolean  result = false;

        JSONArray projectList = restInterfaceManager.getProjectsOfRepo(repoId);
        if(!projectList.isEmpty()){
            JSONObject project = projectList.getJSONObject(0);
            String branch = project.getString("branch");
            result = restInterfaceManager.updateCodeTracker(repoId,branch);
        }

        return result;
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

    private LinkedList<ScanMessageWithTime> addAllCommits(Map<LocalDate,List<ScanMessageWithTime>> map,List<LocalDate> dates){
        int i = 0;
        int sourceSize = dates.size();
        LinkedList<ScanMessageWithTime> result=new LinkedList<>();
        while(i<sourceSize){
            LocalDate date=dates.get(sourceSize-1-i);
            List<ScanMessageWithTime> list=map.get(date);
            List<ScanMessageWithTime> sortedList = list.stream().sorted(Comparator.comparing(ScanMessageWithTime::getCommitTime).reversed()).collect(Collectors.toList());
            for(ScanMessageWithTime scanMessageWithTime : sortedList){
                result.addFirst(scanMessageWithTime);
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
        //JSONObject commitResponse = restInterfaceManager.getCommitTime(commitId);
//        if (commitResponse != null) {
//            String commit_time = commitResponse.getJSONObject("data").getString("commit_time");
//            projectParam.put("till_commit_time", commit_time);
//        }
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



    private String getBaseCommitId(String  repoId,String branchName) throws RuntimeException{

        JSONObject jsonObject = restInterfaceManager.getCommitsOfRepoByConditions(repoId,1,1,false);

        JSONArray scanJsonArray = jsonObject.getJSONArray("data");
        if(scanJsonArray.isEmpty()){
            logger.warn("repoId is not exist! -->  {}" , repoId);
            return null ;
        }
        JSONObject latestScanMessageWithTime = scanJsonArray.getJSONObject(0);
        String completeCommitTime = null;
        String checkOutCommitId = null;



        String firstRequestrepoPath = null;
        try {
            firstRequestrepoPath = restInterfaceManager.getRepoPath(repoId, checkOutCommitId);
            if (firstRequestrepoPath == null) {
                throw new RuntimeException("request base server failed");
            }

            JGitHelper jGitHelper = new JGitHelper(firstRequestrepoPath);
            jGitHelper.checkoutToLatestCommit(branchName);
            checkOutCommitId = jGitHelper.getLatestCommitId(branchName);
            completeCommitTime = jGitHelper.getCommitTime(checkOutCommitId);


        } finally {
            if (firstRequestrepoPath != null) {
                restInterfaceManager.freeRepoPath(repoId, firstRequestrepoPath);
            }
        }


        int findCounts = 0;
        String baseCommitId = null;
        LocalDateTime latestCommitTime = null;
        LocalDateTime firstScanCommitTime = null;


        latestCommitTime = DateTimeUtil.stringToLocalDate(completeCommitTime);
        firstScanCommitTime = latestCommitTime.minusMonths(6).minusDays(5);

        int allAggregationCommitCount = getAllAggregationCommitCount(repoId,checkOutCommitId);
        int lastListCommitsSize = 0;
        String lastScannedCommit = null;

        while(baseCommitId == null){
            List<RevCommit> qualified = null;
            RevCommit  baseCommit = null;
            String repoPath = null;

            try {

                repoPath = restInterfaceManager.getRepoPath(repoId, checkOutCommitId);
                if (repoPath == null) {
                    throw new RuntimeException("request base server failed");
                }


                JGitHelper jGitHelper = new JGitHelper(repoPath);
                String formatTime = DateTimeUtil.format(firstScanCommitTime);
                List<RevCommit> listCommits = jGitHelper.getAggregationCommit(formatTime);
                while (listCommits.isEmpty()) {
                    firstScanCommitTime = firstScanCommitTime.minusMonths(6);
                    formatTime = DateTimeUtil.format(firstScanCommitTime);
                    listCommits = jGitHelper.getAggregationCommit(formatTime);
                }

                if(listCommits.size() > lastListCommitsSize){
                    lastListCommitsSize = listCommits.size();
                }else{
                    continue;
                }

                final LocalDateTime finalFirstScanCommitTime = firstScanCommitTime;
                qualified = listCommits.stream().filter(revCommit -> String.valueOf(revCommit.getCommitTime()).compareTo(DateTimeUtil.timeTotimeStamp(DateTimeUtil.format(finalFirstScanCommitTime))) > 0).sorted(Comparator.comparing(RevCommit::getCommitTime)).collect(Collectors.toList());


            } finally {
                if (repoPath != null) {
                    restInterfaceManager.freeRepoPath(repoId, repoPath);
                }
            }


            if (qualified == null || qualified.isEmpty()) {
                return null;
            }



            for (RevCommit revCommit :
                    qualified) {
                String repoPathRevCommit = null;

                if(lastScannedCommit != null && revCommit.getName().equals(lastScannedCommit)){
                    break;
                }

                try {
                    repoPathRevCommit = restInterfaceManager.getRepoPath(repoId, revCommit.getName());
                    boolean isCompiled = executeShellUtil.executeMvn(repoPathRevCommit);
                    findCounts++;
                    if (isCompiled) {
                        baseCommit = revCommit;
                        break;
                    }
                } finally {
                    if (repoPath != null) {
                        restInterfaceManager.freeRepoPath(repoId, repoPath);
                    }
                }
            }

            lastScannedCommit = qualified.get(0).getName();


            if(baseCommit != null){
                baseCommitId = baseCommit.getName();

            }
            if(qualified.size() == allAggregationCommitCount){
                break;
            }

            if(findCounts > 10 && baseCommit == null){
                return null;
            }

            firstScanCommitTime = firstScanCommitTime.minusMonths(3);

        }

        return baseCommitId;

    }

    private int getAllAggregationCommitCount(String repoId, String checkCommitId) throws RuntimeException{
        int result = 0;
        String repoPath = null;
        try {
            repoPath = restInterfaceManager.getRepoPath(repoId, checkCommitId);
            if (repoPath == null) {
                throw new RuntimeException("request base server failed");
            }


            JGitHelper jGitHelper = new JGitHelper(repoPath);
            List<RevCommit> revCommits = jGitHelper.getAllAggregationCommit();
            result = revCommits.size();

        } finally {
            if (repoPath != null) {
                restInterfaceManager.freeRepoPath(repoId, repoPath);
            }
        }

        return result;
    }
}
