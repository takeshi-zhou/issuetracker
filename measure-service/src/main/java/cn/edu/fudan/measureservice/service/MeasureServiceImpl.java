package cn.edu.fudan.measureservice.service;

import cn.edu.fudan.measureservice.analyzer.MeasureAnalyzer;
import cn.edu.fudan.measureservice.component.RestInterfaceManager;
import cn.edu.fudan.measureservice.domain.Duration;
import cn.edu.fudan.measureservice.domain.Measure;
import cn.edu.fudan.measureservice.handler.ResultHandler;
import cn.edu.fudan.measureservice.util.DateTimeUtil;
import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class MeasureServiceImpl implements MeasureService {

    private MeasureAnalyzer measureAnalyzer;
    private ResultHandler resultHandler;
    private RestInterfaceManager restInterfaceManager;

    public MeasureServiceImpl(MeasureAnalyzer measureAnalyzer,
                             ResultHandler resultHandler,
                              RestInterfaceManager restInterfaceManager) {
        this.measureAnalyzer = measureAnalyzer;
        this.resultHandler = resultHandler;
        this.restInterfaceManager=restInterfaceManager;
    }
    @Override
    public Object getMeasureDataChange(String projectId, Duration duration) {
        String repoId=restInterfaceManager.getRepoIdOfProject(projectId);
        List<String> twoCommits=getCommitIdsToCompare(repoId,duration);
        //当前时间该项目的度量值
        log.info("开始获取第一个度量........");
        Measure measure1=getMeasureDateOfOneCommit(repoId,twoCommits.get(0));
        //某个时间跨度之前项目的度量值
        log.info("开始获取第二个度量........");
        Measure measure2=getMeasureDateOfOneCommit(repoId,twoCommits.get(1));
        Map<String,Object> measureChanges=new HashMap<>();
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

}
