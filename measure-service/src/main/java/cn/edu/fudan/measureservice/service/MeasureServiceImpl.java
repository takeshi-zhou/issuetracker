package cn.edu.fudan.measureservice.service;

import cn.edu.fudan.measureservice.analyzer.MeasureAnalyzer;
import cn.edu.fudan.measureservice.component.RestInterfaceManager;
import cn.edu.fudan.measureservice.domain.CommitWithTime;
import cn.edu.fudan.measureservice.domain.Duration;
import cn.edu.fudan.measureservice.domain.Measure;
import cn.edu.fudan.measureservice.domain.RepoMeasure;
import cn.edu.fudan.measureservice.handler.ResultHandler;
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

    public MeasureServiceImpl(MeasureAnalyzer measureAnalyzer,
                             ResultHandler resultHandler,
                              RestInterfaceManager restInterfaceManager,
                              RepoMeasureMapper repoMeasureMapper) {
        this.measureAnalyzer = measureAnalyzer;
        this.resultHandler = resultHandler;
        this.restInterfaceManager=restInterfaceManager;
        this.repoMeasureMapper=repoMeasureMapper;
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

    @Override
    public void saveMeasureData(String repoId, String commitId,String commitTime) {
        try{
            Measure measure=getMeasureDateOfOneCommit(repoId,commitId);
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
            repoMeasureMapper.insertOneRepoMeasure(repoMeasure);
        }catch (Exception e){
            log.error(e.getMessage());
        }

    }

    private Map<String,Object> getMeasureChangeOfOneProject(String repoId,String repoName,Duration duration){
        List<String> twoCommits=getCommitIdsToCompare(repoId,duration);
        //当前时间该项目的度量值
        log.info("开始获取第一个度量........");
        Measure measure1=getMeasureDateOfOneCommit(repoId,twoCommits.get(0));
        //某个时间跨度之前项目的度量值
        log.info("开始获取第二个度量........");
        Measure measure2=getMeasureDateOfOneCommit(repoId,twoCommits.get(1));
        if(measure1!=null&&measure2!=null){
            Map<String,Object> measureChanges=new HashMap<>();
            measureChanges.put("repoName",repoName);
            int change1;double change2;
            change1=measure1.getTotal().getClasses()-measure2.getTotal().getClasses();
            measureChanges.put("classes",change1);
            change1=measure1.getTotal().getFunctions()-measure2.getTotal().getFunctions();
            measureChanges.put("functions",change1);
            change1=measure1.getTotal().getNcss()-measure2.getTotal().getNcss();
            measureChanges.put("ncss",change1);
            change1=measure1.getTotal().getJavaDocs()-measure2.getTotal().getJavaDocs();
            measureChanges.put("java_docs",change1);
            change1=measure1.getTotal().getJavaDocsLines()-measure2.getTotal().getJavaDocsLines();
            measureChanges.put("java_docs_lines",change1);
            change1=measure1.getTotal().getSingleCommentLines()-measure2.getTotal().getSingleCommentLines();
            measureChanges.put("single_comment_lines",change1);
            change1=measure1.getTotal().getMultiCommentLines()-measure2.getTotal().getMultiCommentLines();
            measureChanges.put("multi_comment_lines",change1);
            change2=measure1.getFunctions().getFunctionAverage().getCcn()-measure2.getFunctions().getFunctionAverage().getCcn();
            measureChanges.put("ccn",change2);
            return measureChanges;
        }
        return null;
    }


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

    private Measure getMeasureDateOfOneCommit(String repoId,String commitId){
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

    @KafkaListener(id = "measure", topics = {"Measure"}, groupId = "measure")
    public void commitInfoListener(ConsumerRecord<String, String> consumerRecord) {
        List<CommitWithTime> commits=JSONArray.parseArray(consumerRecord.value(),CommitWithTime.class);
        log.info("received message from topic -> " + consumerRecord.topic() + " : " + commits.size()+" commits need to scan!");
        for(CommitWithTime commit:commits){
            saveMeasureData(commit.getRepoId(),commit.getCommitId(),commit.getCommitTime());
        }
    }

}
