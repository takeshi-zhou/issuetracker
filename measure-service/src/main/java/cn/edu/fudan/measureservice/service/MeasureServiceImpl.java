package cn.edu.fudan.measureservice.service;

import cn.edu.fudan.measureservice.analyzer.MeasureAnalyzer;
import cn.edu.fudan.measureservice.component.RestInterfaceManager;
import cn.edu.fudan.measureservice.domain.CommitWithTime;
import cn.edu.fudan.measureservice.domain.Duration;
import cn.edu.fudan.measureservice.domain.Measure;
import cn.edu.fudan.measureservice.domain.RepoMeasure;
import cn.edu.fudan.measureservice.domain.Package;
import cn.edu.fudan.measureservice.handler.ResultHandler;
import cn.edu.fudan.measureservice.mapper.PackageMeasureMapper;
import cn.edu.fudan.measureservice.mapper.RepoMeasureMapper;
import cn.edu.fudan.measureservice.util.DateTimeUtil;
import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;


@Slf4j
@Service
public class MeasureServiceImpl implements MeasureService {

    private MeasureAnalyzer measureAnalyzer;
    private ResultHandler resultHandler;
    private RestInterfaceManager restInterfaceManager;
    private RepoMeasureMapper repoMeasureMapper;
    private PackageMeasureMapper packageMeasureMapper;

    public MeasureServiceImpl(MeasureAnalyzer measureAnalyzer,
                             ResultHandler resultHandler,
                              RestInterfaceManager restInterfaceManager,
                              RepoMeasureMapper repoMeasureMapper,
                              PackageMeasureMapper packageMeasureMapper) {
        this.measureAnalyzer = measureAnalyzer;
        this.resultHandler = resultHandler;
        this.restInterfaceManager=restInterfaceManager;
        this.repoMeasureMapper=repoMeasureMapper;
        this.packageMeasureMapper=packageMeasureMapper;
    }

    //获取一个用户所有项目在某个时间段前后度量值的变化
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
    public void commitInfoListener(ConsumerRecord<String, String> consumerRecord) {
        List<CommitWithTime> commits=JSONArray.parseArray(consumerRecord.value(),CommitWithTime.class);
        log.info("received message from topic -> " + consumerRecord.topic() + " : " + commits.size()+" commits need to scan!");
        for(CommitWithTime commit:commits){
            saveMeasureData(commit.getRepoId(),commit.getCommitId(),commit.getCommitTime());
        }
    }

    //保存某个项目某个commit的度量信息
    @Override
    public void saveMeasureData(String repoId, String commitId,String commitTime) {
        try{
            Measure measure=getMeasureDataOfOneCommit(repoId,commitId);
            saveRepoLevelMeasureData(measure,repoId,commitId,commitTime);
            savePackageMeasureData(measure,repoId,commitId,commitTime);
        }catch (Exception e){
            log.error(e.getMessage());
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
         for(Package p:measure.getPackages().getPackages()){
             p.setUuid(UUID.randomUUID().toString());
             p.setCommit_id(commitId);
             p.setCommit_time(commitTime);
             p.setRepo_id(repoId);
             packages.add(p);
         }
         if(!packages.isEmpty()){
             packageMeasureMapper.insertPackageMeasureDataList(packages);
         }
    }

    //保存某个项目某个commit项目级别的度量
    private void saveRepoLevelMeasureData(Measure measure,String repoId,String commitId,String commitTime){
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
        if(repoMeasureMapper.sameMeasureOfOneCommit(repoId,commitId)==0)
           repoMeasureMapper.insertOneRepoMeasure(repoMeasure);
    }

    /*
    private List<String> getCommitIdsToCompare(String repoId, Duration duration){
        List<String> twoCommits=new ArrayList<>();
        LocalDateTime now=LocalDateTime.now();
        LocalDateTime dateTimeBeforeDuration;
        switch (duration){
            case week:
                dateTimeBeforeDuration=now.minusWeeks(1);
                break;
            case month:
                dateTimeBeforeDuration=now.minusMonths(1);
                break;
                default:
                    dateTimeBeforeDuration=now.minusWeeks(1);
        }
        JSONArray commits=restInterfaceManager.getCommitsOfRepo(repoId);
        for(int i=0;i<commits.size();i++){
            if(i==0)
                twoCommits.add(commits.getJSONObject(0).getString("commit_id"));
            else if(DateTimeUtil.format(dateTimeBeforeDuration).compareTo(commits.getJSONObject(i).getString("commit_time"))>0){
                twoCommits.add(commits.getJSONObject(i).getString("commit_id"));
                break;
            }
        }
        return twoCommits;
    }
*/

}
