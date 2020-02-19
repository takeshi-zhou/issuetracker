package cn.edu.fudan.scanservice.component.strategy.impl;
import java.time.ZoneOffset;
import java.util.*;
import	java.util.stream.Collectors;

import cn.edu.fudan.scanservice.component.rest.RestInterfaceManager;
import cn.edu.fudan.scanservice.component.strategy.CommitFilterStrategy;
import cn.edu.fudan.scanservice.domain.ScanMessageWithTime;
import cn.edu.fudan.scanservice.util.DateTimeUtil;
import cn.edu.fudan.scanservice.util.ExecuteShellUtil;
import cn.edu.fudan.scanservice.util.JGitHelper;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AfterAggregationCommitStrategy implements CommitFilterStrategy<ScanMessageWithTime> {

    private RestInterfaceManager restInterfaceManager;

    private ExecuteShellUtil executeShellUtil;

    @Autowired
    public void setExecuteShellUtil(ExecuteShellUtil executeShellUtil) {
        this.executeShellUtil = executeShellUtil;
    }

    @Autowired
    public void setRestInterfaceManager(RestInterfaceManager restInterfaceManager) {
        this.restInterfaceManager = restInterfaceManager;
    }


    @Override
    public List<ScanMessageWithTime> filter(Map<LocalDate, List<ScanMessageWithTime>> map, List<LocalDate> dates) {
        String repoId = StrategyUtil.getRepoIdByMsg(map);
        String repoPath = null;
        LinkedList<ScanMessageWithTime> result=new LinkedList<>();
        LocalDateTime latestCommitTime = null;
        LocalDateTime firstScanCommitTime = null;
        List<RevCommit> qualified = null;
        try{
            repoPath = restInterfaceManager.getRepoPath(repoId,map.get(dates.get(1)).get(1).getCommitId());
            if(repoPath == null){
                return null;
            }

            JSONObject jsonObject = restInterfaceManager.getCommitsOfRepoByConditions(repoId,1,1,null);
            JSONArray scanMessageWithTimeJsonArray = jsonObject.getJSONArray("data");
            JSONObject latestScanMessageWithTime = scanMessageWithTimeJsonArray.getJSONObject(0);
            String completeCommitTime = latestScanMessageWithTime.getString("commit_time");
            latestCommitTime = DateTimeUtil.stringToLocalDate(completeCommitTime);
            firstScanCommitTime = latestCommitTime.minusMonths(6);

            final LocalDateTime finalFirstScanCommitTime = firstScanCommitTime;

            JGitHelper jGitHelper = new JGitHelper(repoPath);
            List<RevCommit> listCommits = jGitHelper.getAggregationCommit(DateTimeUtil.format(firstScanCommitTime));
            qualified = listCommits.stream().filter(revCommit -> String.valueOf(revCommit.getCommitTime()/1000).compareTo(DateTimeUtil.timeTotimeStamp(DateTimeUtil.format(finalFirstScanCommitTime))) > 0).sorted(Comparator.comparing(RevCommit::getCommitTime)).collect(Collectors.toList());


        }finally{
            if(repoPath != null){
                restInterfaceManager.freeRepoPath(repoId,repoPath);
            }
        }


        if(qualified == null || qualified.isEmpty()){
            return null;
        }

        RevCommit baseCommit = null;

        for (RevCommit revCommit:
                qualified) {
            String repoPathRevCommit = null;

            try{
                repoPathRevCommit = restInterfaceManager.getRepoPath(repoId,revCommit.getName());
                boolean isCompiled = executeShellUtil.executeMvn(repoPathRevCommit);
                if(isCompiled){
                    baseCommit = revCommit;
                    break;
                }
            }finally {
                if(repoPath != null){
                    restInterfaceManager.freeRepoPath(repoId,repoPath);
                }
            }
        }


        int sourceSize=dates.size();



        if(baseCommit != null){
            String baseCommitTime = DateTimeUtil.format(new Date(baseCommit.getCommitTime()*1000));
            LocalDateTime baseDateTime = new Date(baseCommit.getCommitTime()*1000).toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
            LocalDate baseDate = baseDateTime.toLocalDate();

            int i = 0;
            boolean isRecent=true;
            while(i<sourceSize){
                LocalDate date=dates.get(sourceSize-1-i);

                if(isRecent && (date.isAfter(baseDate) || date.isEqual(baseDate))){
                    List<ScanMessageWithTime> list=map.get(date);
                    if(date.isAfter(baseDate)){

                        result = addAllListIntoFirst(result,list);
                    }else{
                        List<ScanMessageWithTime> sortedList = list.stream().sorted(Comparator.comparing(ScanMessageWithTime::getCommitTime).reversed()).collect(Collectors.toList());
                        for(ScanMessageWithTime scanMessageWithTime : sortedList){
                            if(scanMessageWithTime.getCommitTime().compareTo(baseCommitTime) >= 0){
                                result.addFirst(scanMessageWithTime);
                            }

                        }

                    }

                }else{
                    isRecent=false;
                }

                i++;
            }
        }

        return result;
    }

    private LinkedList<ScanMessageWithTime> addAllListIntoFirst(LinkedList<ScanMessageWithTime> result,List<ScanMessageWithTime> scanMessageWithTimes){
        List<ScanMessageWithTime> sortedList = scanMessageWithTimes.stream().sorted(Comparator.comparing(ScanMessageWithTime::getCommitTime).reversed()).collect(Collectors.toList());
        for(ScanMessageWithTime scanMessageWithTime : sortedList){
            result.addFirst(scanMessageWithTime);
        }
        return result;
    }
}
