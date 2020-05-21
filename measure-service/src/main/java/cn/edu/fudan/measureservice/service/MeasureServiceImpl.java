package cn.edu.fudan.measureservice.service;

import cn.edu.fudan.measureservice.analyzer.MeasureAnalyzer;
import cn.edu.fudan.measureservice.component.RestInterfaceManager;
import cn.edu.fudan.measureservice.domain.*;
import cn.edu.fudan.measureservice.domain.Package;
import cn.edu.fudan.measureservice.domain.test.Commit;
import cn.edu.fudan.measureservice.handler.ResultHandler;
import cn.edu.fudan.measureservice.mapper.PackageMeasureMapper;
import cn.edu.fudan.measureservice.mapper.RepoMeasureMapper;
import cn.edu.fudan.measureservice.portrait.DeveloperMetrics;
import cn.edu.fudan.measureservice.portrait.Efficiency;
import cn.edu.fudan.measureservice.util.DateTimeUtil;
import cn.edu.fudan.measureservice.util.GitUtil;
import cn.edu.fudan.measureservice.util.JGitHelper;
import cn.edu.fudan.measureservice.util.JGitUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.protocol.types.Field;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class MeasureServiceImpl implements MeasureService {

    private Logger logger = LoggerFactory.getLogger(MeasureServiceImpl.class);

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
                if(change!=null) {
                    projectsMeasureChanges.add(change);
                }
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
//        logger.info("开始获取第一个度量........");
        RepoMeasure measure1=repoMeasureMapper.getLatestMeasureData(repoId);
        //某个时间跨度之前项目的度量值
//        logger.info("开始获取第二个度量........");
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
        logger.info("received message from topic -> " + consumerRecord.topic() + " : " + commits.size()+" commits need to scan!");
        ack.acknowledge();
        for(CommitWithTime commit:commits){
            String commitId = commit.getCommitId();
            String repoId = commit.getRepoId();
            if (commitId == null || commitId.length() == 0){
                logger.error("Save measure info failed : commitId is null!");
                continue;
            }
            if (repoId == null || repoId.length() == 0){
                logger.error("Save measure info failed : repoId is null!");
                continue;
            }
            String repoPath = null;
            try {
                repoPath = restInterfaceManager.getRepoPath(repoId,commitId);
                if (repoPath!=null){
                    logger.info("Start to save measure info: repoId is " + repoId + " commitId is " + commitId);
                    saveMeasureData(commit.getRepoId(),commitId,commit.getCommitTime(),repoPath);
                }
            }finally {
                if(repoPath!=null) {
                    restInterfaceManager.freeRepoPath(repoId,repoPath);
                }
            }
        }
        logger.info("all complete!!!");
    }

    //保存某个项目某个commit的度量信息
    public void saveMeasureData(String repoId, String commitId,String commitTime,String repoPath) {
        try{
            Measure measure = getMeasureDataOfOneCommit(repoPath);
            saveRepoLevelMeasureData(measure,repoId,commitId,commitTime,repoPath);
            savePackageMeasureData(measure,repoId,commitId,commitTime);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //获取单个项目某个commit的度量值
    private Measure getMeasureDataOfOneCommit(String repoPath){
        Measure measure=null;
        try{
            if(repoPath!=null) {
                measure=measureAnalyzer.analyze(repoPath,"",resultHandler);
            }
        }catch (Exception e){
            logger.error("获取某commit的Measure时出错：");
            e.printStackTrace();
        }
        return measure;
    }

    //保存某个项目某个commit包级别的度量
    private void savePackageMeasureData(Measure measure,String repoId,String commitId,String commitTime){
        try{
            List<Package> packages =new ArrayList<>();
            DecimalFormat df=new DecimalFormat("#.00");
            for(Package p:measure.getPackages().getPackages()){
                p.setUuid(UUID.randomUUID().toString());
                p.setCommit_id(commitId);
                p.setCommit_time(commitTime);
                p.setRepo_id(repoId);
                if(packageMeasureMapper.samePackageMeasureExist(repoId,commitId,p.getName())>0) {
                    continue;
                }
                String packageName=p.getName();
                int count=0;
                int ccn=0;
                for(Function function:measure.getFunctions().getFunctions()){
                    if(function.getName().startsWith(packageName)){
                        count++;
                        ccn+=function.getCcn();
                    }
                }
                if(count==0) {
                    p.setCcn(0.00);
                } else{
                    double result=(double)ccn/count;
                    p.setCcn(Double.valueOf(df.format(result)));
                }
                packages.add(p);
            }
            if(!packages.isEmpty()){
                packageMeasureMapper.insertPackageMeasureDataList(packages);
            }
        } catch (NumberFormatException e) {
            logger.error("Saving package measure data failed：");
            e.printStackTrace();
        }

    }

    //保存某个项目某个commit项目级别的度量
    private void saveRepoLevelMeasureData(Measure measure,String repoId,String commitId,String commitTime,String repoPath){
        try{
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

            //调用JGit，先实例化JGit对象
            JGitHelper jGitHelper = new JGitHelper(repoPath);
            RevCommit revCommit = jGitHelper.getCurrentRevCommit(repoPath,commitId);

            String developerName = revCommit.getAuthorIdent().getName();
            String developerEmail = revCommit.getAuthorIdent().getEmailAddress();
            repoMeasure.setDeveloper_name(developerName);
            repoMeasure.setDeveloper_email(developerEmail);

            //如果是最初始的那个commit，那么工作量记为0，否则  则进行git diff 对比获取工作量
            boolean isInitCommitByJGit = jGitHelper.isInitCommit(revCommit);
            if (isInitCommitByJGit){
                repoMeasure.setAdd_lines(0);
                repoMeasure.setDel_lines(0);
                repoMeasure.setAdd_comment_lines(0);
                repoMeasure.setDel_comment_lines(0);
                repoMeasure.setChanged_files(0);
            }else{
                Map<String, Integer> map = getLinesDataByJGit(jGitHelper, repoPath, commitId);
                repoMeasure.setAdd_lines(map.get("addLines"));
                repoMeasure.setDel_lines(map.get("delLines"));
                repoMeasure.setAdd_comment_lines(map.get("addCommentLines"));
                repoMeasure.setDel_comment_lines(map.get("delCommentLines"));
                repoMeasure.setChanged_files(getChangedFilesCount(jGitHelper, repoPath, commitId));
            }

            //获取该commit是否是merge
            repoMeasure.setIs_merge(jGitHelper.isMerge(revCommit));

            try{
                if(repoMeasureMapper.sameMeasureOfOneCommit(repoId,commitId)==0) {
                    repoMeasureMapper.insertOneRepoMeasure(repoMeasure);
                    logger.info("Successfully insert one record to repo_measure table ：repoId is " + repoId + " commitId is " + commitId);
                }
            } catch (Exception e) {
                logger.error("Inserting data to DB table failed：");
                e.printStackTrace();
            }

        } catch (Exception e) {
            logger.error("Saving commit measure data failed：repoId is " + repoId + " commitId is " + commitId);
            e.printStackTrace();
        }

    }

    @Override
    public List<RepoMeasure> getRepoMeasureByRepoId(String repoId,String since,String until,Granularity granularity) {
        List<RepoMeasure> result=new ArrayList<>();
        LocalDate preTimeLimit= DateTimeUtil.parse(until).plusDays(1);
        //until查询往后推一天，例如输入的是2020-03-31，不往后推一天，接口只能返回03-30这一天及以前的数据。推一天后，就能返回03-31这一天的数据
        until = preTimeLimit.toString();
        //先统一把这个时间段的所有度量值对象都取出来，然后按照时间单位要求来过滤
        int count = 0;
        if(since == null || since.isEmpty()){
            count = 10;
        }
        List<RepoMeasure> repoMeasures=repoMeasureMapper.getRepoMeasureByDeveloperAndRepoId(repoId,null,count,since,until);
        if(repoMeasures==null||repoMeasures.isEmpty()) {
            return Collections.emptyList();
        }

        //过滤符合要求的repoMeasure，并且按照日期归类
        Map<LocalDate,List<RepoMeasure>> map=repoMeasures.stream()
        .collect(Collectors.groupingBy((RepoMeasure repoMeasure)->{
            String dateStr=repoMeasure.getCommit_time().split(" ")[0];
            return LocalDate.parse(dateStr,DateTimeUtil.Y_M_D_formatter);
        }));


        //对日期进行排序
        List<LocalDate> dates=new ArrayList<>(map.keySet());
        dates.sort(((o1, o2) -> {
            if(o1.equals(o2)) {
                return 0;
            }
            return o1.isBefore(o2)?1:-1;
        }));


        //将每个日期中的repo measure ，根据commit time进行排序为降序。
        for(Map.Entry<LocalDate,List<RepoMeasure>> entry : map.entrySet()){
            List<RepoMeasure> repoMeasuresOfDate = entry.getValue();
            repoMeasuresOfDate.sort( (o1, o2) -> {
                if(o1.getCommit_time().compareTo(o2.getCommit_time()) < 0){
                    return 1;
                }else{
                    return -1;
                }

            });
        }


        switch (granularity){
            case day:
                for(LocalDate localDate : dates){
                    RepoMeasure dayOfLatest = map.get(localDate).get(0);
                    result.add(dayOfLatest);
                }
                break;
            case week:
                for(LocalDate localDate : dates){
                    if(localDate.isAfter(preTimeLimit) || DateTimeUtil.isTheSameDay(localDate,preTimeLimit)){
                        continue;
                    }
                    while(!(localDate.isBefore(preTimeLimit) && localDate.isAfter(preTimeLimit.minusWeeks(1))) && !DateTimeUtil.isTheSameDay(localDate,preTimeLimit)){

                        preTimeLimit = preTimeLimit.minusWeeks(1);
                    }
                    result.add(map.get(localDate).get(0));
                    preTimeLimit = preTimeLimit.minusWeeks(1);

                }

                break;
            case month:
                for(LocalDate localDate : dates){
                    if(localDate.isAfter(preTimeLimit) || DateTimeUtil.isTheSameDay(localDate,preTimeLimit) && !DateTimeUtil.isTheSameDay(localDate,preTimeLimit)){
                        continue;
                    }
                    while(!(localDate.isBefore(preTimeLimit) && localDate.isAfter(preTimeLimit.minusMonths(1))) && !DateTimeUtil.isTheSameDay(localDate,preTimeLimit) ){

                        preTimeLimit = preTimeLimit.minusMonths(1);
                    }
                    result.add(map.get(localDate).get(0));
                    preTimeLimit = preTimeLimit.minusMonths(1);

                }

                break;
            default:
                throw new RuntimeException("please input correct granularity !");
        }

        result= result.stream().map(rm -> {
            String date=rm.getCommit_time();
            rm.setCommit_time(date.split(" ")[0]);
            return rm;
        }).collect(Collectors.toList());


        return result;
    }

    @Override
    public RepoMeasure getRepoMeasureByRepoIdAndCommitId(String repoId, String commitId) {
        RepoMeasure repoMeasure = repoMeasureMapper.getRepoMeasureByCommit(repoId,commitId);
        return repoMeasure;
    }

    @Override
    public void deleteRepoMeasureByRepoId(String repoId) {
        logger.info("measurement info start to delete");
        repoMeasureMapper.delRepoMeasureByRepoId(repoId);
        logger.info("measurement delete completed");
    }


    @Override
    public List<Package> getPackageMeasureUnderSpecificCommit(String repoId, String commit) {

        List<Package> packageMeasures=packageMeasureMapper.getPackageMeasureByRepoIdAndCommit(repoId,commit);
        if(packageMeasures==null||packageMeasures.isEmpty()) {
            return Collections.emptyList();
        }
        return packageMeasures;
    }

    @Override
    @Deprecated
    public List<ActiveMeasure> getActiveMeasureChange(String repoId, String since, String until,Granularity granularity) {
        List<ActiveMeasure> result=new ArrayList<>();
        String repoPath=getRepoPath(repoId);
        LocalDate date=LocalDate.parse(since,DateTimeUtil.Y_M_D_formatter);
        LocalDate limit=LocalDate.parse(until,DateTimeUtil.Y_M_D_formatter);
        LocalDate next;
        switch (granularity){
            case day:
                do{
                    if(date.plusDays(1).isAfter(limit)) {
                        result.add(getOneActiveMeasure(repoPath,DateTimeUtil.y_m_d_format(date),DateTimeUtil.y_m_d_format(limit)));
                    } else{
                        next=date.plusDays(1);
                        result.add(getOneActiveMeasure(repoPath,DateTimeUtil.y_m_d_format(date),DateTimeUtil.y_m_d_format(next)));
                        date=next;
                    }
                }while(date.isBefore(limit));
                break;
            case week:
                do{
                    if(date.plusWeeks(1).isAfter(limit)) {
                        result.add(getOneActiveMeasure(repoPath,DateTimeUtil.y_m_d_format(date),DateTimeUtil.y_m_d_format(limit)));
                    } else{
                        next=date.plusWeeks(1);
                        result.add(getOneActiveMeasure(repoPath,DateTimeUtil.y_m_d_format(date),DateTimeUtil.y_m_d_format(next)));
                        date=next;
                    }
                }while(date.isBefore(limit));
                break;
            case month:
                do{
                    if(date.plusMonths(1).isAfter(limit)) {
                        result.add(getOneActiveMeasure(repoPath,DateTimeUtil.y_m_d_format(date),DateTimeUtil.y_m_d_format(limit)));
                    } else{
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
        return repoPath;
    }

    @Override
    @Deprecated
    public ActiveMeasure getOneActiveMeasure(String repoId,String since,String until){
            ActiveMeasure activeMeasure=new ActiveMeasure();
            String repoPath=null;
            String[] sinceDates = since.split("\\D");
            String[] untilDates = until.split("\\D");
            if(sinceDates.length != 3 || untilDates.length != 3){
                logger.error("since --> {} or until --> {} ,pattern is not available" ,since,until);
                return activeMeasure;
            }
            LocalDate sinceLocalDate = LocalDate.of(Integer.parseInt(sinceDates[0]),Integer.parseInt(sinceDates[1]),Integer.parseInt(sinceDates[2]));
            LocalDate untilLocalDate = LocalDate.of(Integer.parseInt(untilDates[0]),Integer.parseInt(untilDates[1]),Integer.parseInt(untilDates[2]));
            if(sinceLocalDate.plusMonths(1).isBefore(untilLocalDate)){
                sinceLocalDate = untilLocalDate.minusMonths(1);
            }
            since = sinceLocalDate.getYear()+"-"+sinceLocalDate.getMonthValue()+"-"+sinceLocalDate.getDayOfMonth();
            try{
                repoPath=restInterfaceManager.getRepoPath(repoId,"");
                activeMeasure.setCommitInfos(gitUtil.getCommitInfoByAuthor(repoPath,since,until));
                Map<String,Integer> map=new HashMap<>();
                for(String file:gitUtil.getCommitFiles(repoPath,since,until)){
                    if(map.containsKey(file)){
                        int count=map.get(file);
                        map.put(file,count+1);
                    }else {
                        map.put(file,0);
                    }
                }
                List<String> distinctFiles=new ArrayList<>(map.keySet());
                distinctFiles.sort((file1,file2)->map.get(file2)-map.get(file1));
                if(distinctFiles.size()>=10) {
                    activeMeasure.setMostCommitFiles(distinctFiles.subList(0,10));
                } else {
                    activeMeasure.setMostCommitFiles(distinctFiles);
                }
            }finally {
                if(repoPath!=null) {
                    restInterfaceManager.freeRepoPath(repoId,repoPath);
                }
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
            if(repoPath!=null) {
                restInterfaceManager.freeRepoPath(repoId,repoPath);
            }
        }
        return repoRank;
    }

    @Override
    public CommitBase getCommitBaseInformation(String repo_id, String commit_id) {

        CommitBase commitBase = repoMeasureMapper.getCommitBaseInformation(repo_id,commit_id);

        return commitBase;
    }

    /**
     * 通过命令行获取指定commit增加和删除的行数
     * @param repo_id
     * @param commit_id
     * @return
     */
    private CommitBase getCommitBaseInformationByCLI(String repo_id, String commit_id) {

        String repoPath=null;
        CommitBase commitBase = null;
        try{
            repoPath=restInterfaceManager.getRepoPath(repo_id,"");
            if(repoPath!=null){
                commitBase = gitUtil.getOneCommitChanges(repoPath,commit_id);
            }
        }finally {
            if(repoPath!=null){
                restInterfaceManager.freeRepoPath(repo_id,repoPath);
            }

        }
        return commitBase;
    }


    //该方法返回一段时间内某个开发者的工作量指标，如果不指定开发者这个参数，则返回所有开发者在该项目中的工作量指标
    @Override
    public CommitBaseInfoDuration getCommitBaseInformationByDuration(String repo_id, String since, String until, String developer_name) {
        CommitBaseInfoDuration commitBaseInfoDuration = new CommitBaseInfoDuration();
        String sinceDay = dateFormatChange(since);
        String untilDay = dateFormatChange(until);

        List<CommitInfoDeveloper> CommitInfoDeveloper = repoMeasureMapper.getCommitInfoDeveloperListByDuration(repo_id, sinceDay, untilDay, developer_name);
        int addLines = repoMeasureMapper.getAddLinesByDuration(repo_id, sinceDay, untilDay);
        int delLines = repoMeasureMapper.getDelLinesByDuration(repo_id, sinceDay, untilDay);
        int sumCommitCounts = repoMeasureMapper.getCommitCountsByDuration(repo_id, sinceDay, untilDay,null);
        int sumChangedFiles = repoMeasureMapper.getChangedFilesByDuration(repo_id, sinceDay, untilDay,null);
        commitBaseInfoDuration.setCommitInfoList(CommitInfoDeveloper);
        commitBaseInfoDuration.setSumAddLines(addLines);
        commitBaseInfoDuration.setSumDelLines(delLines);
        commitBaseInfoDuration.setSumCommitCounts(sumCommitCounts);
        commitBaseInfoDuration.setSumChangedFiles(sumChangedFiles);
        return commitBaseInfoDuration;
    }

    @Override
    public List<CommitBaseInfoGranularity> getCommitBaseInfoGranularity(String repo_id, String granularity, String since, String until, String developer_name){
        //用于从数据库获取数据
        CommitBaseInfoDuration commitBaseInfoDuration = new CommitBaseInfoDuration();
        List<CommitBaseInfoGranularity> result=new ArrayList<>();

        //获取查询时的日期 也就是今天的日期,也作为查询时间段中的截止日期（如果until合法，就把untilDay的值赋值给today）
        LocalDate today = LocalDate.now();

        //获取最早一次提交的commit信息
        String startDateStr = repoMeasureMapper.getStartDateOfRepo(repo_id).substring(0,10);

        //最早一次commit日期
        LocalDate startDay=LocalDate.parse(startDateStr,DateTimeUtil.Y_M_D_formatter);

        //定义用于循环中不断更新的indexDay,也作为查询时间段中的开始日期（如果since合法，就把sinceDay的值赋值给indexDay）
        LocalDate indexDay = startDay;
        //以下是请求参数中的两个日期点
        if (since != null){
            LocalDate sinceDay = LocalDate.parse(dateFormatChange(since),DateTimeUtil.Y_M_D_formatter);
            if (startDay.isBefore(sinceDay)){
                indexDay = sinceDay;
            }
        }
        if (until != null){
            LocalDate untilDay = LocalDate.parse(dateFormatChange(until),DateTimeUtil.Y_M_D_formatter);
            if (today.isAfter(untilDay)){
                today = untilDay;
            }
        }
        if (indexDay.isAfter(today)){
            throw new RuntimeException("please input correct date!");
        }
        if (indexDay.equals(today)){
            commitBaseInfoDuration = getCommitBaseInformationByDuration(repo_id, today.toString(), today.toString(), developer_name);
            result.add(getCommitBaseInfoGranularityData(today.toString().substring(0,10), commitBaseInfoDuration));
            return result;
        }else{
            switch (granularity){
                case "day":
                    while(today.isAfter(indexDay)){
                        //after 为 参数indexDay这天的后一天
                        LocalDate after = indexDay.plusDays(1);
//                        System.out.println(after);
                        commitBaseInfoDuration = getCommitBaseInformationByDuration(repo_id, indexDay.toString(), indexDay.toString(), developer_name);
                        result.add(getCommitBaseInfoGranularityData(indexDay.toString().substring(0,10), commitBaseInfoDuration));
                        //indexDay 变成后一天
                        indexDay = after;
                    }
                    break;
                case "week":
                    while(today.isAfter(indexDay)){
                        //after 为 参数indexDay这天的后一周（后七天的那一天）
                        LocalDate after = indexDay.plusWeeks(1);
                        if(after.isAfter(today)){
                            commitBaseInfoDuration = getCommitBaseInformationByDuration(repo_id, indexDay.toString(), today.toString(), developer_name);
                            result.add(getCommitBaseInfoGranularityData(indexDay.toString().substring(0,10), commitBaseInfoDuration));
                            break;
                        }
                        commitBaseInfoDuration = getCommitBaseInformationByDuration(repo_id, indexDay.toString(), after.toString(), developer_name);
                        result.add(getCommitBaseInfoGranularityData(indexDay.toString().substring(0,10), commitBaseInfoDuration));
                        //indexDay 变成后七天
                        indexDay = after;
                    }
                    break;
                case "month":
                    while(today.isAfter(indexDay)){
                        //after 为 参数indexDay这天的下个月的这一天
                        LocalDate after = indexDay.plusMonths(1);
                        if(after.isAfter(today)){
                            commitBaseInfoDuration = getCommitBaseInformationByDuration(repo_id, indexDay.toString(), today.toString(), developer_name);
                            result.add(getCommitBaseInfoGranularityData(indexDay.toString().substring(0,10), commitBaseInfoDuration));
                            break;
                        }
                        commitBaseInfoDuration = getCommitBaseInformationByDuration(repo_id, indexDay.toString(), after.toString(), developer_name);
                        result.add(getCommitBaseInfoGranularityData(indexDay.toString().substring(0,10), commitBaseInfoDuration));
                        //indexDay 变成indexDay这天的下个月的这一天
                        indexDay = after;
                    }
                    break;
                default:
                    throw new RuntimeException("please input correct granularity type: day or week or month");
            }
        }
        return result;
    }

    private CommitBaseInfoGranularity getCommitBaseInfoGranularityData(String time, CommitBaseInfoDuration commitBaseInfoDuration){
        CommitBaseInfoGranularity commitBaseInfoGranularity = new CommitBaseInfoGranularity();
        commitBaseInfoGranularity.setDate(time);
        commitBaseInfoGranularity.setCommitBaseInfoDuration(commitBaseInfoDuration);
        return commitBaseInfoGranularity;
    }


    //jeff 获取一个repo的每月commit次数
    @Override
    public List<CommitCountsMonthly> getCommitCountsMonthly(String repo_id) {
        List<CommitCountsMonthly> result=new ArrayList<>();
        String startDateStr = repoMeasureMapper.getStartDateOfRepo(repo_id).substring(0,10);
        //最早一次commit日期
        LocalDate startDay=LocalDate.parse(startDateStr,DateTimeUtil.Y_M_D_formatter);
        LocalDate indexDay = LocalDate.now();
//        System.out.println(indexDay);
        int counts = 0;
        while(indexDay.isAfter(startDay)){
            //当月第一天
            LocalDate first = indexDay.with(TemporalAdjusters.firstDayOfMonth());
            //当月最后一天
            LocalDate last = indexDay.with(TemporalAdjusters.lastDayOfMonth());
            counts = getCommitCountsByDuration(repo_id, first.toString(), last.toString());
            result.add(getCommitMonth(indexDay.toString().substring(0,7), counts));
            //indexDay 变成上个月最后一天
            indexDay = first.minusDays(1);
        }
        return result;
    }

    private CommitCountsMonthly getCommitMonth(String time, int counts){
        CommitCountsMonthly commitCountsMonthly=new CommitCountsMonthly();
        commitCountsMonthly.setMonth(time);
        commitCountsMonthly.setCommit_counts(counts);
        return commitCountsMonthly;
    }


    @Override
    public int getCommitCountsByDuration(String repo_id, String since, String until) {
        if (since.compareTo(until)>0 || since.length()>10 || until.length()>10){
            throw new RuntimeException("please input correct date");
        }
        String sinceday = dateFormatChange(since);
        String untilday = dateFormatChange(until);

        int commitCountsByDuration = repoMeasureMapper.getCommitCountsByDuration(repo_id, sinceday, untilday,null);
        return commitCountsByDuration;
    }

    //把日期格式从“2010.10.10转化为2010-10-10”
    private String dateFormatChange(String dateStr){
        String newdateStr = dateStr.replace('.','-');
        return newdateStr;
    }




    @Override
    public double getQuantityByCommitAndCategory(String repo_id, String commit_id, String category,String token ) {
        //代码质量指数：代码行数/问题数 若问题数为0，则返回-1
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
//            System.out.println("addedIssues:" + addedIssues);
            if (addedIssues != -1){
                if (addedIssues != 0 ){
                    //新增问题质量指数：每改变100行代码，新增的问题数量
                    changes.put("addedQuantity", addedIssues*100*1.0/changeLines);
                }else{
                    changes.put("addedQuantity", -1);
                }
            }else{
                changes.put("未取得结果","ScanResult 未记录该commit");
                logger.error("ScanResult 未记录该commit");
            }

            int eliminatedIssues = restInterfaceManager.getNumberOfEliminateIssueByCommit(repo_id,commit_id,category,spaceType,token);
//            System.out.println("eliminatedIssues:" + eliminatedIssues);
            if(eliminatedIssues != -1){
                if(eliminatedIssues != 0){
                    //消除问题质量指数：每改变100行代码，消除的问题数量
                    changes.put("eliminatedQuantity", eliminatedIssues*100*1.0/changeLines);
                }else{
                    changes.put("eliminatedQuantity", -1);
                }
            }else{
                changes.put("未取得结果","ScanResult 未记录该commit");
                logger.error("ScanResult 未记录该commit");
            }

        }else {
            changes.put("未取得结果","未取得repo_measure记录");
            logger.error("not get repo path!");
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
        String startMonthStr = String.valueOf(startMonth);
        String startDayStr = String.valueOf(startDay);
        String nowMonthStr = String.valueOf(nowMonth);
        String nowDayStr = String.valueOf(nowDay);
        if (startMonth<10){
            startMonthStr = "0" + String.valueOf(startMonth);
        }
        if (startDay<10){
            startDayStr = "0" + String.valueOf(startDay);
        }
        if (nowMonth<10){
            nowMonthStr = "0" + String.valueOf(nowMonth);
        }
        if (nowDay<10){
            nowDayStr = "0" + String.valueOf(nowDay);
        }

        String start = startYear+"."+startMonthStr+"."+startDayStr;
        String now = nowYear+"."+nowMonthStr+"."+nowDayStr;
//        System.out.println(start);
//        System.out.println(now);
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
    public Object getCodeChangesByDurationAndDeveloperName(String developer_name, String since, String until, String token, String category,String repoId) {
        String repoPath=null;
        int[] lineChanges ;
        CommitBase commitBase = new CommitBase();
        int lineAdds = 0;
        int lineDels = 0;
        if(repoId == null || repoId.isEmpty()){
            if(category == null || category.isEmpty()){
                return " The category should not be null when  repo id is null";
            }
            List<JSONObject> projectList = restInterfaceManager.getProjectListByCategory(token,category);
            for(Object project:projectList){
                JSONObject protectJson = (JSONObject)project;
                if(protectJson.get("download_status").toString().equals("Downloading")){
                    continue;
                }
                String eachRepoId = protectJson.get("repo_id").toString();
                List<RepoMeasure> repoMeasures = repoMeasureMapper.getRepoMeasureByDeveloperAndRepoId(eachRepoId,developer_name,0,since,until);
                for(RepoMeasure repoMeasure : repoMeasures){
                    lineAdds += repoMeasure.getAdd_lines();
                    lineDels += repoMeasure.getDel_lines();
                }

//                try{
//                    repoPath=restInterfaceManager.getRepoPath(repo_id,"");
//                    if(repoPath!=null){
//
//                        //获取repo一段时间内行数变化值
//                        lineChanges = gitUtil.getRepoLineChanges(repoPath,since,until,developer_name);
//                        lineAdds += lineChanges[0];
//                        lineDels += lineChanges[1];
//
//                    }
//                }finally {
//                    if(repoPath!=null) {
//                        restInterfaceManager.freeRepoPath(repo_id,repoPath);
//                    }
//                }

            }
        }else{
            List<RepoMeasure> repoMeasures = repoMeasureMapper.getRepoMeasureByDeveloperAndRepoId(repoId,developer_name,0,since,until);
            for(RepoMeasure repoMeasure : repoMeasures){
                lineAdds += repoMeasure.getAdd_lines();
                lineDels += repoMeasure.getDel_lines();
            }
        }

        commitBase.setAddLines(lineAdds);
        commitBase.setDelLines(lineDels);

        return commitBase;
    }

    @Override
    public int getCommitCountByDurationAndDeveloperName(String developerName, String since, String until, String token, String category,String repoId) {
        String repoPath=null;
        int commitTotalCount = 0;
        if(repoId != null && !repoId.isEmpty()){
            return repoMeasureMapper.getCommitCountsByDuration(repoId,since,until,developerName);
        }else{
            List<JSONObject> projectList = restInterfaceManager.getProjectListByCategory(token,category);
            for(Object project:projectList){
                JSONObject protectJson = (JSONObject)project;
                if(protectJson.get("download_status").toString().equals("Downloading")){
                    continue;
                }
                String repo_id = protectJson.get("repo_id").toString();

                int commitCount = repoMeasureMapper.getCommitCountsByDuration(repo_id,since,until,developerName);
                commitTotalCount += commitCount;

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
                    if(repoPath!=null) {
                        restInterfaceManager.freeRepoPath(repo_id,repoPath);
                    }
                }
            }
        }

        return result;
    }

    @Override
    public Object getQualityChangesByDeveloperName(String developer_name, String token, String category, int counts, String project_name) {
        String spaceType= "project";
        //判断count是否合法
        if(counts<0){
            throw new RuntimeException("counts can not be  negative");
        }

        List<RepoMeasure> repoMeasures;
        //如果projectName的名字为null则表示用户在所有project列表中最近30次commit的代码质量信息
        if(project_name==null){
            repoMeasures = repoMeasureMapper.getRepoMeasureByDeveloperAndRepoId(null,developer_name,counts,null,null);
        }else{
            JSONObject result = restInterfaceManager.getProjectListByCondition(token,category,project_name,null);
            int code =result.getIntValue("code");
            if(code != 200){
                logger.error("request project api failed  ---> {}",project_name);
                throw new RuntimeException("request project api failed  ---> "+ project_name);
            }
            JSONArray projects = result.getJSONArray("data");
            if(projects.size() == 0){
                logger.info("do not have this project --> {}",project_name );
                throw new RuntimeException("do not have this project --> "+project_name);
            }
            if (projects.size() > 1){
                // 这一段的方法待优化 可以在多个project中取最近counts 次数的commits的度量
                logger.info("more than one project named --> {}",project_name );
                throw new RuntimeException("more than one project named --> "+project_name);
            }
            JSONObject project =  projects.getJSONObject(0);
            String repoId = project.getString("repo_id");
            repoMeasures = repoMeasureMapper.getRepoMeasureByDeveloperAndRepoId(repoId,developer_name,counts,null,null);

        }


        List<CodeQuality> queries = new ArrayList<>();

        CodeQuality codeQuality;
        String projectName = null;
        for(RepoMeasure repoMeasure :repoMeasures){
            int newIssueCounts = restInterfaceManager.getNumberOfNewIssueByCommit(repoMeasure.getRepo_id(),repoMeasure.getCommit_id(),category,spaceType,token);
            codeQuality = new CodeQuality(repoMeasure.getAdd_lines(),repoMeasure.getDel_lines(),newIssueCounts,repoMeasure.getCommit_time());
            JSONArray projects = restInterfaceManager.getProjectsOfRepo(repoMeasure.getRepo_id());
            if(projects.size()==0){
                logger.error("can not find project by repo_id --->{}",repoMeasure.getRepo_id());
            }else {
                projectName = projects.getJSONObject(0).getString("name");
            }
            codeQuality.setCommitId(repoMeasure.getCommit_id());
            codeQuality.setProjectName(projectName);
            if(newIssueCounts == -1){
                logger.info("this commit --> {} can not be compiled",repoMeasure.getCommit_id());
                codeQuality.setExpression("this commit can not be compiled!");
            }

            queries.add(codeQuality);
        }

        return queries;
    }

    @Override
    public Object InsertData(String repoId) {
        List<Commit> commits = repoMeasureMapper.getCommits(repoId);

        for(Commit commit: commits){
            String repoPath= null;
            try {
                repoPath = restInterfaceManager.getRepoPath(repoId,commit.getCommit_id());
                if (repoPath!=null){
                    saveMeasureData(commit.getRepo_id(),commit.getCommit_id(),commit.getCommit_time(),repoPath);
                }
            }finally {
                if(repoPath!=null) {
                    restInterfaceManager.freeRepoPath(repoId,repoPath);
                }
            }
        }
        return "success";
    }

    /**
     * @param jGitHelper
     * @param repo_path
     * @param commit_id
     * @return 通过JGit获取一次commit中开发者的新增行数，删除行数，新增注释行数，删除注释行数
     */
    public Map<String, Integer> getLinesDataByJGit(JGitHelper jGitHelper, String repo_path, String commit_id){
        Map<String, Integer> map = new HashMap<>();
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        builder.setMustExist(true);
        builder.addCeilingDirectory(new File(repo_path));
        builder.findGitDir(new File(repo_path));
        try {
            Repository repository = builder.build();
            RevCommit revCommit = jGitHelper.getCurrentRevCommit(repo_path,commit_id);
            List<DiffEntry> diffFix = JGitHelper.getChangedFileList(revCommit,repository);//获取变更的文件列表
            map = JGitHelper.getLinesData(diffFix);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * @param jGitHelper
     * @param repo_path
     * @param commit_id
     * @return 通过JGit获取本次commit修改的文件数量
     */
    public int getChangedFilesCount(JGitHelper jGitHelper, String repo_path, String commit_id){
        int result = 0;

        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        builder.setMustExist(true);
        builder.addCeilingDirectory(new File(repo_path));
        builder.findGitDir(new File(repo_path));
        try {
            Repository repository = builder.build();
            RevCommit revCommit = jGitHelper.getCurrentRevCommit(repo_path,commit_id);
            List<DiffEntry> diffFix = JGitHelper.getChangedFileList(revCommit,repository);//获取变更的文件列表
            result = JGitHelper.getChangedFilesCount(diffFix);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Object getDeveloperRankByCommitCount(String repo_id, String since, String until){
        since = dateFormatChange(since);
        until = dateFormatChange(until);
        List<Map<String, Object>> result = repoMeasureMapper.getDeveloperRankByCommitCount(repo_id, since, until);
        return result;
    }

    @Override
    public Object getDeveloperRankByLoc(String repo_id, String since, String until){
        since = dateFormatChange(since);
        until = dateFormatChange(until);
        List<Map<String, Object>> result = repoMeasureMapper.getDeveloperRankByLoc(repo_id, since, until);
        //如果LOC数据为0，则删除这条数据
        if (null != result && result.size() > 0) {
            for (int i = result.size() - 1; i >= 0; i--) {
                Map<String, Object> map = result.get(i);
                Object obj;
                //取出map中第一个元素
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    obj = entry.getValue();
                    if (obj != null) {
                        //将Object类型转换为int类型
                        if (Integer.parseInt(String.valueOf(obj)) == 0) {
                            result.remove(i);
                        }
                        break;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Object getCommitCountsDaily(String repo_id, String since, String until){
        since = dateFormatChange(since);
        until = dateFormatChange(until);
        List<Map<String, Object>> result = new ArrayList<>();

        LocalDate indexDay = LocalDate.parse(since,DateTimeUtil.Y_M_D_formatter);
        LocalDate untilDay = LocalDate.parse(until,DateTimeUtil.Y_M_D_formatter);
        while(untilDay.isAfter(indexDay) || untilDay.isEqual(indexDay)){
            Map<String, Object> map = new HashMap<>();
            int CommitCounts = repoMeasureMapper.getCommitCountsByDuration(repo_id, indexDay.toString(), indexDay.toString(),null);
            map.put("commit_date", indexDay.toString());
            map.put("commit_count", CommitCounts);
            result.add(map);
            indexDay = indexDay.plusDays(1);
        }
        return result;
    }

    @Override
    public Object getRepoLOCByDuration(String repo_id, String since, String until){
        since = dateFormatChange(since);
        until = dateFormatChange(until);
        int result = repoMeasureMapper.getRepoLOCByDuration(repo_id, since, until, null);
        return result;
    }

    @Override
    public Object getLOCDaily(String repo_id, String since, String until){
        since = dateFormatChange(since);
        until = dateFormatChange(until);
        List<Map<String, Object>> result = new ArrayList<>();

        LocalDate indexDay = LocalDate.parse(since,DateTimeUtil.Y_M_D_formatter);
        LocalDate untilDay = LocalDate.parse(until,DateTimeUtil.Y_M_D_formatter);
        while(untilDay.isAfter(indexDay) || untilDay.isEqual(indexDay)){
            Map<String, Object> map = new HashMap<>();
            int LOC = repoMeasureMapper.getRepoLOCByDuration(repo_id, indexDay.toString(), indexDay.toString(),null);
            map.put("commit_date", indexDay.toString());
            map.put("LOC", LOC);
            result.add(map);
            indexDay = indexDay.plusDays(1);
        }
        return result;
    }

    @Override
    public Object getCommitCountLOCDaily(String repo_id, String since, String until){
        since = dateFormatChange(since);
        until = dateFormatChange(until);
        List<Map<String, Object>> result = new ArrayList<>();

        LocalDate indexDay = LocalDate.parse(since,DateTimeUtil.Y_M_D_formatter);
        LocalDate untilDay = LocalDate.parse(until,DateTimeUtil.Y_M_D_formatter);
        while(untilDay.isAfter(indexDay) || untilDay.isEqual(indexDay)){
            Map<String, Object> map = new HashMap<>();
            int LOC = repoMeasureMapper.getRepoLOCByDuration(repo_id, indexDay.toString(), indexDay.toString(),null);
            int CommitCounts = repoMeasureMapper.getCommitCountsByDuration(repo_id, indexDay.toString(), indexDay.toString(),null);
            //这里只返回有commit的数据，并不是每天都返回
            if (CommitCounts > 0){
                map.put("commit_date", indexDay.toString());
                map.put("LOC", LOC);
                map.put("commit_count", CommitCounts);
                result.add(map);
            }
            indexDay = indexDay.plusDays(1);
        }
        return result;
    }

    @Override
    public Object getDeveloperActivenessByDuration(String repo_id, String since, String until, String developer_name) {
        since = dateFormatChange(since);
        until = dateFormatChange(until);
        List<Map<String, Object>> result = new ArrayList<>();

        LocalDate indexDay = LocalDate.parse(since,DateTimeUtil.Y_M_D_formatter);
        LocalDate untilDay = LocalDate.parse(until,DateTimeUtil.Y_M_D_formatter);
        while(untilDay.isAfter(indexDay) || untilDay.isEqual(indexDay)){
            Map<String, Object> map = new HashMap<>();

            List<CommitInfoDeveloper> CommitInfoDeveloper = repoMeasureMapper.getCommitInfoDeveloperListByDuration(repo_id, indexDay.toString(), indexDay.toString(), developer_name);
            if (CommitInfoDeveloper.size()==1){
                int add = CommitInfoDeveloper.get(0).getAdd();
                int del = CommitInfoDeveloper.get(0).getDel();
                //还差缺陷数量，计算E/L N/L
//                int newIssues = 0;
//                int elliminateIssues = 0;

//                double newIssuesQuality = newIssues*100.0 / (add+del);
//                double elliminateIssuesQuality = elliminateIssues*100.0 / (add+del);

                map.put("commit_date", indexDay.toString());
                map.put("add", add);
                map.put("del", del);
//                map.put("E/L", newIssuesQuality);
                result.add(map);
            }

            indexDay = indexDay.plusDays(1);
        }
        return result;

    }

    @Override
    public Object getDeveloperActivenessByGranularity(String repo_id, String granularity, String developer_name) {
        //获取查询时的日期 也就是今天的日期,也作为查询时间段中的截止日期
        LocalDate today = LocalDate.now();
        List<Map<String, Object>> result = new ArrayList<>();


        //此为“week”的情况
        LocalDate sinceDay = today.minusWeeks(1);
        String since = sinceDay.toString();
        String until = today.toString();

        switch (granularity){
            case "week":
                return getDeveloperActivenessByDuration(repo_id, since, until, developer_name);
            case "month":
                sinceDay = today.minusMonths(1);
                since = sinceDay.toString();
                return getDeveloperActivenessByDuration(repo_id, since, until, developer_name);
            case "year":
                sinceDay = today.minusYears(1);
                LocalDate indexDay = sinceDay;
                while(today.isAfter(indexDay)){
                    Map<String, Object> map = new HashMap<>();
                    List<CommitInfoDeveloper> list = repoMeasureMapper.getCommitInfoDeveloperListByDuration(repo_id, indexDay.toString(), indexDay.plusMonths(1).toString(), developer_name);

                    if (list.size()>0){
                        int add = list.get(0).getAdd();
                        int del = list.get(0).getDel();
                        map.put("commit_date", indexDay.toString().substring(0,7));
                        map.put("add", add);
                        map.put("del", del);
                        result.add(map);
                    }

                    //indexDay 变成indexDay这天的下个月的这一天
                    indexDay = indexDay.plusMonths(1);
                }
                break;
            default:
                throw new RuntimeException("please input correct granularity type: week or month or year");
        }
        return result;

    }

    @Override
    public Object getDeveloperListByRepoId(String repo_id) {
        return repoMeasureMapper.getDeveloperListByRepoId(repo_id);
    }

    @Override
    public Object getPortrait(String repoId, String developer, String beginDate, String endDate, String token) {
        //--------------------------以下是开发效率相关指标-----------------------------
        Efficiency efficiency = new Efficiency(restInterfaceManager);

        //提交频率指标
        int totalCommitCount = getCommitCountsByDuration(repoId, beginDate, endDate);
        int developerCommitCount = repoMeasureMapper.getCommitCountsByDuration(repoId, beginDate, endDate, developer);
        double commitFrequency = developerCommitCount*(1.0)/totalCommitCount;
        efficiency.setCommitFrequency(commitFrequency);

        //代码量指标
        int developerLOC = repoMeasureMapper.getRepoLOCByDuration(repoId, beginDate, endDate, developer);
        int totalLOC = repoMeasureMapper.getRepoLOCByDuration(repoId, beginDate, endDate, "");
        double workLoad = developerLOC*(1.0)/totalLOC;
        efficiency.setWorkLoad(workLoad);

        //获取代码新增、删除逻辑行数数据
        JSONArray projects = restInterfaceManager.getProjectsOfRepo(repoId);
        String branch = projects.getJSONObject(0).getString("branch");
        JSONObject statements = restInterfaceManager.getStatements(repoId, beginDate, endDate, branch);
        int developerAddStatement = 0;
        int totalAddStatement = 0;
        int developerDelStatement = 0;
        int totalDelStatement = 0;
        for(String str:statements.keySet()){
            if (str.equals(developer)){
                developerAddStatement = statements.getJSONObject(str).getIntValue("ADD");
                developerDelStatement = statements.getJSONObject(str).getIntValue("DELETE");
            }
            totalAddStatement += statements.getJSONObject(str).getIntValue("ADD");
            totalDelStatement += statements.getJSONObject(str).getIntValue("DELETE");
        }
        //新增逻辑行指标
        efficiency.setNewLogicLine(developerAddStatement*(1.0)/totalAddStatement);
        //删除逻辑行指标
        efficiency.setDelLogicLine(developerDelStatement*(1.0)/totalDelStatement);

        JSONObject validLines = restInterfaceManager.getValidLine(repoId, beginDate, endDate, branch);
        int developerValidLine = 0;
        int totalValidLine = 0;
        for(String key:validLines.keySet()){
            if (key.equals(developer)){
                developerValidLine = validLines.getIntValue(key);
            }
            totalValidLine += validLines.getIntValue(key);
        }
        //有效代码行指标
        if (totalAddStatement != 0){
            efficiency.setValidStatement(developerValidLine*(1.0)/totalValidLine);
        }else {
            efficiency.setValidStatement(-1);
        }
        return efficiency;
    }
}
