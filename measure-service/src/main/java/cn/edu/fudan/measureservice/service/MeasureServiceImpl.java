package cn.edu.fudan.measureservice.service;

import cn.edu.fudan.measureservice.analyzer.MeasureAnalyzer;
import cn.edu.fudan.measureservice.component.RestInterfaceManager;
import cn.edu.fudan.measureservice.domain.*;
import cn.edu.fudan.measureservice.domain.Package;
import cn.edu.fudan.measureservice.domain.test.Commit;
import cn.edu.fudan.measureservice.handler.ResultHandler;
import cn.edu.fudan.measureservice.mapper.PackageMeasureMapper;
import cn.edu.fudan.measureservice.mapper.RepoMeasureMapper;
import cn.edu.fudan.measureservice.util.DateTimeUtil;
import cn.edu.fudan.measureservice.util.GitUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.tomcat.jni.Local;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import javax.swing.text.html.parser.Entity;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
        logger.info("开始获取第一个度量........");
        RepoMeasure measure1=repoMeasureMapper.getLatestMeasureData(repoId);
        //某个时间跨度之前项目的度量值
        logger.info("开始获取第二个度量........");
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
            String developer_name =commit.getDeveloperName();
            String developer_email =commit.getDeveloperEmail();
            if(developer_name==null || developer_email==null || developer_name.isEmpty() || developer_email.isEmpty() ){
                try{
                    JSONObject jsonCommit = restInterfaceManager.getCommitByCommitId(commitId);
                    developer_name = jsonCommit.getJSONObject("data").getString("developer");
                    developer_email = jsonCommit.getJSONObject("data").getString("developer_email");
                }catch(Exception e){
                    logger.error("commit is not available, commit's value is --> {}",commit);
                    e.printStackTrace();
                }
            }
            saveMeasureData(commit.getRepoId(),commitId,commit.getCommitTime(),developer_name,developer_email);
        }
        logger.info("all complete!!!");
    }

    //保存某个项目某个commit的度量信息
    public void saveMeasureData(String repoId, String commitId,String commitTime,String developerName,String developerEmail) {
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
            if(repoPath!=null) {
                measure=measureAnalyzer.analyze(repoPath,"",resultHandler);
            }
        }catch (Exception e){
            e.printStackTrace();
            if(repoPath!=null) {
                restInterfaceManager.freeRepoPath(repoId,repoPath);
            }
        }finally {
            if(repoPath!=null) {
                restInterfaceManager.freeRepoPath(repoId,repoPath);
            }
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
        CommitBase commitBase = getCommitBaseInformationByCLI(repoId,commitId);
        repoMeasure.setAdd_lines(commitBase.getAddLines());
        repoMeasure.setDel_lines(commitBase.getDelLines());
        if(repoMeasureMapper.sameMeasureOfOneCommit(repoId,commitId)==0) {
            repoMeasureMapper.insertOneRepoMeasure(repoMeasure);
        }
    }

    @Override
    public List<RepoMeasure> getRepoMeasureByRepoId(String repoId,String since,String until,Granularity granularity) {
        List<RepoMeasure> result=new ArrayList<>();
        LocalDate preTimeLimit= DateTimeUtil.parse(until).plusDays(1);
        //先统一把这个时间段的所有度量值对象都取出来，然后按照时间单位要求来过滤
        List<RepoMeasure> repoMeasures=repoMeasureMapper.getRepoMeasureBetween(repoId,since,until);
        if(repoMeasures==null||repoMeasures.isEmpty()) {
            return Collections.emptyList();
        }

        //过滤符合要求的repoMeasure，并且按照日期归类
        Map<LocalDate,List<RepoMeasure>> map=repoMeasures.stream()
                .filter(rm -> rm.getCommit_time().compareTo(since)>0)
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
                    while(!(localDate.isBefore(preTimeLimit) && localDate.isAfter(preTimeLimit.minusWeeks(1)))){

                        preTimeLimit = preTimeLimit.minusWeeks(1);
                    }
                    result.add(map.get(localDate).get(0));
                    preTimeLimit = preTimeLimit.minusWeeks(1);

                }

                break;
            case month:
                for(LocalDate localDate : dates){
                    if(localDate.isAfter(preTimeLimit)){
                        continue;
                    }
                    while(!(localDate.isBefore(preTimeLimit) && localDate.isAfter(preTimeLimit.minusMonths(1)))){

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
        return repoHome+repoPath;
    }

    @Override
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
        int sumCommitCounts = repoMeasureMapper.getCommitCountsByDuration(repo_id, sinceDay, untilDay);
        commitBaseInfoDuration.setCommitInfoList(CommitInfoDeveloper);
        commitBaseInfoDuration.setSumAddLines(addLines);
        commitBaseInfoDuration.setSumDelLines(delLines);
        commitBaseInfoDuration.setSumCommitCounts(sumCommitCounts);
        return commitBaseInfoDuration;
    }

    @Override
    public List<CommitBaseInfoGranularity> getCommitBaseInfoGranularity(String repo_id, String granularity, String since, String until, String developer_name){
        //用于从数据库获取数据
        CommitBaseInfoDuration commitBaseInfoDuration = new CommitBaseInfoDuration();

        List<CommitBaseInfoGranularity> result=new ArrayList<>();
        //获取查询时的日期 也就是今天的日期 记作indexDay
        LocalDate indexDay = LocalDate.now();
//        System.out.println(indexDay);
        //获取最早一次提交的commit信息
        String startDateStr = repoMeasureMapper.getStartDateOfRepo(repo_id).substring(0,10);
        //最早一次commit日期
        LocalDate startDay=LocalDate.parse(startDateStr,DateTimeUtil.Y_M_D_formatter);
        //以下时请求参数中的两个日期点
        if (since != null){
            LocalDate sinceDay = LocalDate.parse(dateFormatChange(since),DateTimeUtil.Y_M_D_formatter);
            if (startDay.isBefore(sinceDay)){
                startDay = sinceDay;
            }
        }
        if (until != null){
            LocalDate untilDay = LocalDate.parse(dateFormatChange(until),DateTimeUtil.Y_M_D_formatter);
            if (indexDay.isAfter(untilDay)){
                indexDay = untilDay;
            }
        }
        if (startDay.isAfter(indexDay)){
            throw new RuntimeException("please input correct date!");
        }
        if (startDay.equals(indexDay)){
            LocalDate tomorrow = indexDay.plusDays(1);
            commitBaseInfoDuration = getCommitBaseInformationByDuration(repo_id, indexDay.toString(), tomorrow.toString(), developer_name);
            result.add(getCommitBaseMonth(indexDay.toString().substring(0,10), commitBaseInfoDuration));
            return result;
        }else{
            switch (granularity){
                case "day":
                    while(indexDay.isAfter(startDay)){
                        //first 为 参数until这天的前一天
                        LocalDate first = indexDay.minusDays(1);
//                        System.out.println(first);
                        commitBaseInfoDuration = getCommitBaseInformationByDuration(repo_id, first.toString(), indexDay.toString(), developer_name);
                        result.add(getCommitBaseMonth(first.toString().substring(0,10), commitBaseInfoDuration));
                        //indexDay 变成前一天
                        indexDay = first;
                    }
                    break;
                case "week":
                    while(indexDay.isAfter(startDay)){
                        if (startDay.until(indexDay,ChronoUnit.DAYS) < 7){
                            commitBaseInfoDuration = getCommitBaseInformationByDuration(repo_id, startDay.toString(), indexDay.toString(), developer_name);
                            result.add(getCommitBaseMonth(startDay.toString().substring(0,10), commitBaseInfoDuration));
                            break;
                        }
                        //first 为 参数until这天的前一周（前七天的那一天）
                        LocalDate first = indexDay.minusWeeks(1);
                        commitBaseInfoDuration = getCommitBaseInformationByDuration(repo_id, first.toString(), indexDay.toString(), developer_name);
                        result.add(getCommitBaseMonth(first.toString().substring(0,10), commitBaseInfoDuration));
                        //indexDay 变成前七天
                        indexDay = first;
                    }
                    break;
                case "month":
                    while(indexDay.isAfter(startDay)){
                        if (startDay.until(indexDay,ChronoUnit.DAYS) < 30){
                            commitBaseInfoDuration = getCommitBaseInformationByDuration(repo_id, startDay.toString(), indexDay.toString(), developer_name);
                            result.add(getCommitBaseMonth(startDay.toString().substring(0,10), commitBaseInfoDuration));
                            break;
                        }
                        //first 为 参数until这天的上个月的这一天
                        LocalDate first = indexDay.minusMonths(1);
                        commitBaseInfoDuration = getCommitBaseInformationByDuration(repo_id, first.toString(), indexDay.toString(), developer_name);
                        result.add(getCommitBaseMonth(first.toString().substring(0,10), commitBaseInfoDuration));
                        //indexDay 变成until这天的上个月的这一天
                        indexDay = first;
                    }
                    break;
                default:
                    throw new RuntimeException("please input correct granularity type: day or week or month");
            }
        }
        return result;
    }

    private CommitBaseInfoGranularity getCommitBaseMonth(String time, CommitBaseInfoDuration commitBaseInfoDuration){
        CommitBaseInfoGranularity commitBaseInfoGranularity=new CommitBaseInfoGranularity();
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
        System.out.println(indexDay);
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

        int commitCountsByDuration = repoMeasureMapper.getCommitCountsByDuration(repo_id, sinceday, untilday);
        return commitCountsByDuration;
    }

    //把日期格式从“2010.10.10转化为2010-10-10”
    private String dateFormatChange(String dateStr){
        String newdateStr = dateStr.replace('.','-');
        return newdateStr;
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
                logger.error("ScanResult 未记录该commit");
            }

            int eliminatedIssues = restInterfaceManager.getNumberOfEliminateIssueByCommit(repo_id,commit_id,category,spaceType,token);

            if(eliminatedIssues != 0){
                changes.put("eliminatedQuantity",changeLines/eliminatedIssues);
            }else{
                changes.put("eliminatedQuantity",-1);
            }
        }else {
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
        System.out.println(start);
        System.out.println(now);
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
                if(repoPath!=null) {
                    restInterfaceManager.freeRepoPath(repo_id,repoPath);
                }
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
           saveMeasureData(commit.getRepo_id(),commit.getCommit_id(),commit.getCommit_time(),commit.getDeveloper(),commit.getDeveloper_email());
        }
        return "success";
    }


}
