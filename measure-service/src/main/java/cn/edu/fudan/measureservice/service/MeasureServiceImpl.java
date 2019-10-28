package cn.edu.fudan.measureservice.service;

import cn.edu.fudan.measureservice.analyzer.MeasureAnalyzer;
import cn.edu.fudan.measureservice.component.RestInterfaceManager;
import cn.edu.fudan.measureservice.domain.*;
import cn.edu.fudan.measureservice.domain.Package;
import cn.edu.fudan.measureservice.handler.ResultHandler;
import cn.edu.fudan.measureservice.mapper.PackageMeasureMapper;
import cn.edu.fudan.measureservice.mapper.RepoMeasureMapper;
import cn.edu.fudan.measureservice.util.DateTimeUtil;
import cn.edu.fudan.measureservice.util.GitUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class MeasureServiceImpl implements MeasureService {

    @Value("${repoHome}")
    private String repoHome;
    @Value("${inactive}")
    private int inactive;
    @Value("${lessActive}")
    private int lessActive;
    @Value("${relativelyActive}")
    private int relativelyActive;


    private MeasureAnalyzer measureAnalyzer;
    private ResultHandler resultHandler;
    private RestInterfaceManager restInterfaceManager;
    private RepoMeasureMapper repoMeasureMapper;
    private PackageMeasureMapper packageMeasureMapper;
    private GitUtil gitUtil;

    public MeasureServiceImpl(MeasureAnalyzer measureAnalyzer,
                             ResultHandler resultHandler,
                              RestInterfaceManager restInterfaceManager,
                              RepoMeasureMapper repoMeasureMapper,
                              PackageMeasureMapper packageMeasureMapper,
                              GitUtil gitUtil) {
        this.measureAnalyzer = measureAnalyzer;
        this.resultHandler = resultHandler;
        this.restInterfaceManager=restInterfaceManager;
        this.repoMeasureMapper=repoMeasureMapper;
        this.packageMeasureMapper=packageMeasureMapper;
        this.gitUtil=gitUtil;
    }

    @Override
    public Object getMeasureDataChange(String userToken, Duration duration) {
        List<Object> projectsMeasureChanges=new ArrayList<>();
        String accountId=restInterfaceManager.getAccountId(userToken);
        JSONArray projects=restInterfaceManager.getProjectList(accountId);
        Set<String> repoIds=new HashSet<>();//去除重复的repoId
        for(int i=0;i<projects.size();i++){
            String repoName=projects.getJSONObject(i).getString("name");
            String repoId=projects.getJSONObject(i).getString("repo_id");
            if(!repoIds.contains(repoId)){
                Object change=getMeasureChangeOfOneProject(repoId,repoName,duration);
                if(change!=null)
                    projectsMeasureChanges.add(change);
                repoIds.add(repoId);
            }
        }
        return projectsMeasureChanges;
    }

    private Map<String,Object> getMeasureChangeOfOneProject(String repoId,String repoName,Duration duration){
        LocalDateTime now=LocalDateTime.now();
        LocalDateTime time_line;
        switch (duration){
            case week:
                time_line=now.minusWeeks(1);
                break;
            case month:
                time_line=now.minusMonths(1);
                break;
            default:
                time_line=now.minusWeeks(1);
        }
        //当前时间该项目的度量值
        log.info("开始获取第一个度量........");
        RepoMeasure measure1=repoMeasureMapper.getLatestMeasureData(repoId);
        //某个时间跨度之前项目的度量值
        log.info("开始获取第二个度量........");
        RepoMeasure measure2=repoMeasureMapper.getFirstMeasureDataAfterDuration(repoId,DateTimeUtil.transfer(time_line));
        if(measure1!=null&&measure2!=null){
            Map<String,Object> measureChanges=new HashMap<>();
            measureChanges.put("repoName",repoName);
            int change1;double change2;
            change1=measure1.getFiles()-measure2.getFiles();
            measureChanges.put("files",change1);
            change1=measure1.getNcss()-measure2.getNcss();
            measureChanges.put("ncss",change1);
            change1=measure1.getClasses()-measure2.getClasses();
            measureChanges.put("classes",change1);
            change1=measure1.getFunctions()-measure2.getFunctions();
            measureChanges.put("functions",change1);
            change1=measure1.getJava_docs()-measure2.getJava_docs();
            measureChanges.put("java_docs",change1);
            change1=measure1.getJava_doc_lines()-measure2.getJava_doc_lines();
            measureChanges.put("java_docs_lines",change1);
            change1=measure1.getSingle_comment_lines()-measure2.getSingle_comment_lines();
            measureChanges.put("single_comment_lines",change1);
            change1=measure1.getMulti_comment_lines()-measure2.getMulti_comment_lines();
            measureChanges.put("multi_comment_lines",change1);
            change2=measure1.getCcn()-measure2.getCcn();
            measureChanges.put("ccn",Double.valueOf(String.format("%.2f",change2)));
            return measureChanges;
        }
        return null;
    }

    //监听项目的commit列表的消息,保存度量信息
    @KafkaListener(id = "measure", topics = {"Measure"}, groupId = "measure")
    public void commitInfoListener(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack) {
        List<CommitWithTime> commits=JSONArray.parseArray(consumerRecord.value(),CommitWithTime.class);
        log.info("received message from topic -> " + consumerRecord.topic() + " : " + commits.size()+" commits need to scan!");
        ack.acknowledge();
        for(CommitWithTime commit:commits){
            saveMeasureData(commit.getRepoId(),commit.getCommitId(),commit.getCommitTime(),commit.getDeveloperName(),commit.getDeveloperEmail());
        }
        log.info("all complete!!!");
    }

    //保存某个项目某个commit的度量信息
    private void saveMeasureData(String repoId, String commitId,String commitTime,String developerName,String developerEmail) {
        try{
            Measure measure=getMeasureDataOfOneCommit(repoId,commitId);
            saveRepoLevelMeasureData(measure,repoId,commitId,commitTime,developerName,developerEmail);
            savePackageMeasureData(measure,repoId,commitId,commitTime);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //获取单个项目某个commit的度量值
    private Measure getMeasureDataOfOneCommit(String repoId,String commitId){
        String repoPath=null;
        Measure measure=null;
        try{
            repoPath=restInterfaceManager.getRepoPath(repoId,commitId);
            if(repoPath!=null)
                measure=measureAnalyzer.analyze(repoPath,"",resultHandler);
        }catch (Exception e){
            e.printStackTrace();
            if(repoPath!=null)
                restInterfaceManager.freeRepoPath(repoId,repoPath);
        }finally {
            if(repoPath!=null)
                restInterfaceManager.freeRepoPath(repoId,repoPath);
        }
        return measure;
    }

    //保存某个项目某个commit包级别的度量
    private void savePackageMeasureData(Measure measure,String repoId,String commitId,String commitTime){
        List<Package> packages =new ArrayList<>();
        DecimalFormat df=new DecimalFormat("#.00");
        for(Package p:measure.getPackages().getPackages()){
            p.setUuid(UUID.randomUUID().toString());
            p.setCommit_id(commitId);
            p.setCommit_time(commitTime);
            p.setRepo_id(repoId);
            if(packageMeasureMapper.samePackageMeasureExist(repoId,commitId,p.getName())>0)
                continue;
            String packageName=p.getName();
            int count=0;
            int ccn=0;
            for(Function function:measure.getFunctions().getFunctions()){
                if(function.getName().startsWith(packageName)){
                    count++;
                    ccn+=function.getCcn();
                }
            }
           // log.info("count -> {} , ccn -> {}",count,ccn);
            if(count==0)
                p.setCcn(0.00);
            else{
                double result=(double)ccn/count;
                p.setCcn(Double.valueOf(df.format(result)));
            }
            packages.add(p);
        }
        if(!packages.isEmpty()){
            packageMeasureMapper.insertPackageMeasureDataList(packages);
        }
    }

    //保存某个项目某个commit项目级别的度量
    private void saveRepoLevelMeasureData(Measure measure,String repoId,String commitId,String commitTime,String developerName,String developerEmail){
        RepoMeasure repoMeasure=new RepoMeasure();
        repoMeasure.setUuid(UUID.randomUUID().toString());
        repoMeasure.setFiles(measure.getTotal().getFiles());
        repoMeasure.setNcss(measure.getTotal().getNcss());
        repoMeasure.setClasses(measure.getTotal().getClasses());
        repoMeasure.setFunctions(measure.getTotal().getFunctions());
        repoMeasure.setCcn(measure.getFunctions().getFunctionAverage().getNcss());
        repoMeasure.setJava_docs(measure.getTotal().getJavaDocs());
        repoMeasure.setJava_doc_lines(measure.getTotal().getJavaDocsLines());
        repoMeasure.setSingle_comment_lines(measure.getTotal().getSingleCommentLines());
        repoMeasure.setMulti_comment_lines(measure.getTotal().getMultiCommentLines());
        repoMeasure.setCommit_id(commitId);
        repoMeasure.setCommit_time(commitTime);
        repoMeasure.setRepo_id(repoId);
        repoMeasure.setDeveloper_name(developerName);
        repoMeasure.setDeveloper_email(developerEmail);
        CommitBase commitBase = getCommitBaseInformation(repoId,commitId);
        repoMeasure.setAdd_lines(commitBase.getAddLines());
        repoMeasure.setDel_lines(commitBase.getDelLines());
        if(repoMeasureMapper.sameMeasureOfOneCommit(repoId,commitId)==0)
            repoMeasureMapper.insertOneRepoMeasure(repoMeasure);
    }

    @Override
    public List<RepoMeasure> getRepoMeasureByRepoId(String repoId,String since,String until,Granularity granularity) {
        List<RepoMeasure> result=new ArrayList<>();
        //先统一把这个时间段的所有度量值对象都取出来，然后按照时间单位要求来过滤
        List<RepoMeasure> repoMeasures=repoMeasureMapper.getRepoMeasureBetween(repoId,since,until);
        if(repoMeasures==null||repoMeasures.isEmpty())
            return Collections.emptyList();
        repoMeasures= repoMeasures.stream().map(repoMeasure -> {
            String date=repoMeasure.getCommit_time();
            repoMeasure.setCommit_time(date.split(" ")[0]);
            return repoMeasure;
        }).collect(Collectors.toList());
        //LocalDate end=DateTimeUtil.parse(until);
        LocalDate nextTimeLimit= DateTimeUtil.parse(since);
        switch (granularity){
            case week:
                for(RepoMeasure measure:repoMeasures){
                    LocalDate current=DateTimeUtil.parse(measure.getCommit_time());
                    if(current.isAfter(nextTimeLimit)){
                        result.add(measure);
                        nextTimeLimit=nextTimeLimit.plusWeeks(1);
                    }
                }
                break;
            case month:
                for(RepoMeasure measure:repoMeasures){
                    LocalDate current=DateTimeUtil.parse(measure.getCommit_time());
                    if(current.isAfter(nextTimeLimit)){
                        result.add(measure);
                        nextTimeLimit=nextTimeLimit.plusMonths(1);
                    }
                }
                break;
        }
        return result;
    }

    @Override
    public RepoMeasure getRepoMeasureByRepoIdAndCommitId(String repoId, String commitId) {
        RepoMeasure repoMeasure = repoMeasureMapper.getRepoMeasureByCommit(repoId,commitId);
        return repoMeasure;
    }


    @Override
    public List<Package> getPackageMeasureUnderSpecificCommit(String repoId, String commit) {

        List<Package> packageMeasures=packageMeasureMapper.getPackageMeasureByRepoIdAndCommit(repoId,commit);
        if(packageMeasures==null||packageMeasures.isEmpty())
            return Collections.emptyList();
//        Map<String,List<Package>> result=packageMeasures.stream().collect(Collectors.groupingBy(Package::getName));
//        result.forEach((name,measures)->{
//            measures.sort(Comparator.comparing(Package::getCommit_time));
//        });
        return packageMeasures;
    }

    @Override
    public List<ActiveMeasure> getActiveMeasureChange(String repoId, String since, String until,Granularity granularity) {
        List<ActiveMeasure> result=new ArrayList<>();
        String repoPath=getRepoPath(repoId);
        LocalDate date=LocalDate.parse(since,DateTimeUtil.Y_M_D_formatter);
        LocalDate limit=LocalDate.parse(until,DateTimeUtil.Y_M_D_formatter);
        LocalDate next;
        switch (granularity){
            case day:
                do{
                    if(date.plusDays(1).isAfter(limit))
                        result.add(getOneActiveMeasure(repoPath,DateTimeUtil.y_m_d_format(date),DateTimeUtil.y_m_d_format(limit)));
                    else{
                        next=date.plusDays(1);
                        result.add(getOneActiveMeasure(repoPath,DateTimeUtil.y_m_d_format(date),DateTimeUtil.y_m_d_format(next)));
                        date=next;
                    }
                }while(date.isBefore(limit));
                break;
            case week:
                do{
                    if(date.plusWeeks(1).isAfter(limit))
                        result.add(getOneActiveMeasure(repoPath,DateTimeUtil.y_m_d_format(date),DateTimeUtil.y_m_d_format(limit)));
                    else{
                        next=date.plusWeeks(1);
                        result.add(getOneActiveMeasure(repoPath,DateTimeUtil.y_m_d_format(date),DateTimeUtil.y_m_d_format(next)));
                        date=next;
                    }
                }while(date.isBefore(limit));
                break;
            case month:
                do{
                    if(date.plusMonths(1).isAfter(limit))
                        result.add(getOneActiveMeasure(repoPath,DateTimeUtil.y_m_d_format(date),DateTimeUtil.y_m_d_format(limit)));
                    else{
                        next=date.plusMonths(1);
                        result.add(getOneActiveMeasure(repoPath,DateTimeUtil.y_m_d_format(date),DateTimeUtil.y_m_d_format(next)));
                        date=next;
                    }
                }while(date.isBefore(limit));
                break;
        }
        return result;
    }

    private String getRepoPath(String repoId){
        JSONObject currentRepo = restInterfaceManager.getRepoById(repoId);
        String repoPath = currentRepo.getJSONObject("data").getString("local_addr");
        return repoHome+repoPath;
    }

    @Override
    public ActiveMeasure getOneActiveMeasure(String repoId,String since,String until){
            ActiveMeasure activeMeasure=new ActiveMeasure();
            String repoPath=null;
            try{
                repoPath=restInterfaceManager.getRepoPath(repoId,"");
                activeMeasure.setCommitInfos(gitUtil.getCommitInfoByAuthor(repoPath,since,until));
                Map<String,Integer> map=new HashMap<>();
                for(String file:gitUtil.getCommitFiles(repoPath,since,until)){
                    if(map.containsKey(file)){
                        int count=map.get(file);
                        map.put(file,count+1);
                    }else
                        map.put(file,0);
                }
                List<String> distinctFiles=new ArrayList<>(map.keySet());
                distinctFiles.sort((file1,file2)->map.get(file2)-map.get(file1));
                if(distinctFiles.size()>=10)
                    activeMeasure.setMostCommitFiles(distinctFiles.subList(0,10));
                else
                    activeMeasure.setMostCommitFiles(distinctFiles);
            }finally {
                if(repoPath!=null)
                    restInterfaceManager.freeRepoPath(repoId,repoPath);
            }
            return activeMeasure;
    }

    @Override
    public List<RepoRank> getRepoRankByCommit(String token, String since, String until) {
        List<RepoRank> result=new ArrayList<>();
        String accountId=restInterfaceManager.getAccountId(token);
        JSONArray projects=restInterfaceManager.getProjectList(accountId);
        for(int i=0;i<projects.size();i++){
            JSONObject project=projects.getJSONObject(i);
            String name=project.getString("name");
            String repoId=project.getString("repo_id");
            result.add(getRepoRankInfo(repoId,name,since,until));
        }
        result.sort((o1,o2)->o2.getCommitCount()-o1.getCommitCount());
        return result.stream().distinct().collect(Collectors.toList());
    }

    private RepoRank getRepoRankInfo(String repoId,String repoName,String since,String until){
        RepoRank repoRank=new RepoRank();
        repoRank.setReoName(repoName);
        String repoPath=null;
        try{
            repoPath=restInterfaceManager.getRepoPath(repoId,"");
            repoRank.setCommitCount(gitUtil.getCommitCount(repoPath,since,until,null));
        }finally {
            if(repoPath!=null)
                restInterfaceManager.freeRepoPath(repoId,repoPath);
        }
        return repoRank;
    }



    @Override
    public CommitBase getCommitBaseInformation(String repo_id, String commit_id) {
        String repoPath=null;
        CommitBase commitBase = null;
        try{
            repoPath=restInterfaceManager.getRepoPath(repo_id,"");
            if(repoPath!=null){
                commitBase = gitUtil.getOneCommitChanges(repoPath,commit_id);
            }
        }finally {
            if(repoPath!=null)
                restInterfaceManager.freeRepoPath(repo_id,repoPath);
        }
        return commitBase;
    }

    @Override
    public CommitBase getCommitBaseInformationByDuration(String repo_id, String since, String until) {
        String repoPath=null;
        CommitBase commitBase = new CommitBase();
        try{
            repoPath=restInterfaceManager.getRepoPath(repo_id,"");
            if(repoPath!=null){

                //获取repo一段时间内行数变化值
                int[] lineChanges = gitUtil.getRepoLineChanges(repoPath,since,until,null);
                commitBase.setAddLines(lineChanges[0]);
                commitBase.setDelLines(lineChanges[1]);

                //获取repo一段时间内开发者列表信息
                List<Developer> developers = gitUtil.getRepoDevelopers(repoPath,since,until);
                commitBase.setAuthors(developers);
            }
        }finally {
            if(repoPath!=null)
                restInterfaceManager.freeRepoPath(repo_id,repoPath);
        }

        return commitBase;
    }

    @Override
    public int getCommitCountsByDuration(String repo_id, String since, String until) {
        String repoPath=null;
        int commitCounts = -1;
        try{
            repoPath=restInterfaceManager.getRepoPath(repo_id,"");
            if(repoPath!=null){

            //获取repo一段时间内的commit次数
            commitCounts = gitUtil.getCommitCount(repoPath,since,until,null);

            }
        }finally {
            if(repoPath!=null)
                restInterfaceManager.freeRepoPath(repo_id,repoPath);
        }
        return commitCounts;
    }

    @Override
    public double getQuantityByCommitAndCategory(String repo_id, String commit_id, String category,String token ) {
        double ratio = -1;

        //获取代码行数
        RepoMeasure repoMeasure = getRepoMeasureByRepoIdAndCommitId(repo_id,commit_id);
        if(repoMeasure != null){
            int lineCounts = repoMeasure.getNcss();
            //获取缺陷数量
            int remainIssues = restInterfaceManager.getNumberOfRemainingIssue(repo_id,commit_id,"project",null,token);

            if(remainIssues != 0){
                ratio = lineCounts/ remainIssues;
            }

        }
        return ratio;
    }

    @Override
    public Object getQuantityChangesByCommitAndCategory(String repo_id, String commit_id,String category,String token) {
        String spaceType = "project";
        category = "bug";
        Map<String,Object> changes = new HashMap<>();
        CommitBase commitBase = getCommitBaseInformation(repo_id,commit_id);
        if(commitBase != null){
            double changeLines = commitBase.getAddLines() + commitBase.getDelLines();

            int addedIssues = restInterfaceManager.getNumberOfNewIssueByCommit(repo_id,commit_id,category,spaceType,token);
            if(addedIssues!=-1){
                if(addedIssues != 0 ){
                    changes.put("addedQuantity",changeLines/addedIssues);
                }else{
                    changes.put("addedQuantity",-1);
                }
            }else{
                log.error("ScanResult 未记录该commit");
            }

            int eliminatedIssues = restInterfaceManager.getNumberOfEliminateIssueByCommit(repo_id,commit_id,category,spaceType,token);

            if(eliminatedIssues != 0){
                changes.put("eliminatedQuantity",changeLines/eliminatedIssues);
            }else{
                changes.put("eliminatedQuantity",-1);
            }
        }else {
            log.error("not get repo path!");
        }



        return changes;
    }

    @Override
    public String getActivityByRepoId(String repo_id) {
        String active = null;
        LocalDate nowDate = LocalDate.now();
        int nowYear = nowDate.getYear();
        int nowMonth = nowDate.getMonthValue();
        int nowDay = nowDate.getDayOfMonth();
        LocalDate startDate = nowDate.minusDays(90);
        int startYear = startDate.getYear();
        int startMonth = startDate.getMonthValue();
        int startDay = startDate.getDayOfMonth();

        String start = startYear+"."+startMonth+"."+startDay;
        String now = nowYear+"."+nowMonth+"."+nowDay;

        int commitCount = getCommitCountsByDuration(repo_id,start,now);
        if(commitCount != -1){
            if(commitCount <= inactive){
                active = "inactive";
            }else if(commitCount <= lessActive){
                active = "lessActive";
            }else if(commitCount <= relativelyActive){
                active = "relativelyActive";
            }else{
                active = "active";
            }
        }
        return active;
    }

    @Override
    public CommitBase getCodeChangesByDurationAndDeveloperName(String developer_name, String since, String until, String token, String category) {
        String repoPath=null;
        int[] lineChanges ;
        CommitBase commitBase = new CommitBase();
        int lineAdds = 0;
        int lineDels = 0;
        List<JSONObject> projectList = restInterfaceManager.getProjectListByCategory(token,category);
        for(Object project:projectList){
            JSONObject protectJson = (JSONObject)project;
            if(protectJson.get("download_status").toString().equals("Downloading")){
                continue;
            }
            String repo_id = protectJson.get("repo_id").toString();
            try{
                repoPath=restInterfaceManager.getRepoPath(repo_id,"");
                if(repoPath!=null){

                    //获取repo一段时间内行数变化值
                    lineChanges = gitUtil.getRepoLineChanges(repoPath,since,until,developer_name);
                    lineAdds += lineChanges[0];
                    lineDels += lineChanges[1];

                }
            }finally {
                if(repoPath!=null)
                    restInterfaceManager.freeRepoPath(repo_id,repoPath);
            }

        }
        commitBase.setAddLines(lineAdds);
        commitBase.setDelLines(lineDels);

        return commitBase;
    }

    @Override
    public int getCommitCountByDurationAndDeveloperName(String developer_name, String since, String until, String token, String category) {
        String repoPath=null;
        int commitTotalCount = 0;
        List<JSONObject> projectList = restInterfaceManager.getProjectListByCategory(token,category);
        for(Object project:projectList){
            JSONObject protectJson = (JSONObject)project;
            if(protectJson.get("download_status").toString().equals("Downloading")){
                continue;
            }
            String repo_id = protectJson.get("repo_id").toString();
            try{
                repoPath=restInterfaceManager.getRepoPath(repo_id,"");
                if(repoPath!=null){
                    int commitCount = gitUtil.getCommitCount(repoPath,since,until,developer_name);
                    commitTotalCount += commitCount;

                }
            }finally {
                if(repoPath!=null)
                    restInterfaceManager.freeRepoPath(repo_id,repoPath);
            }

        }
        return commitTotalCount;
    }

    @Override
    public Object getRepoListByDeveloperName(String developer_name, String token, String category) {
        String repoPath=null;
        // 这里配合脚本的判断分支，将since以及until设置为值为null的字符串；
        String since = "null";
        String until = "null";
        int commitCount ;
        Map<String,String> result = new HashMap<>();
        List<JSONObject> projectList = restInterfaceManager.getProjectListByCategory(token,category);
        if(projectList != null){
            for(Object project:projectList){
                JSONObject protectJson = (JSONObject)project;
                if(protectJson.get("download_status").toString().equals("Downloading")){
                    continue;
                }
                String repo_id = protectJson.get("repo_id").toString();
                String repo_name = protectJson.get("name").toString();
                try{
                    repoPath=restInterfaceManager.getRepoPath(repo_id,"");
                    if(repoPath!=null){
                        commitCount = gitUtil.getCommitCount(repoPath,since,until,developer_name);
                        if(commitCount != 0){
                            String active = getActivityByRepoId(repo_id);
                            result.put(repo_name,active);
                        }
                    }
                }finally {
                    if(repoPath!=null)
                        restInterfaceManager.freeRepoPath(repo_id,repoPath);
                }
            }
        }

        return result;
    }


}
