package cn.edu.fudan.scanservice.component.strategy.impl;
import java.time.ZoneOffset;
import java.util.*;
import	java.util.stream.Collectors;

import cn.edu.fudan.scanservice.component.rest.RestInterfaceManager;
import cn.edu.fudan.scanservice.component.strategy.CommitFilterStrategy;
import cn.edu.fudan.scanservice.dao.ScanDao;
import cn.edu.fudan.scanservice.domain.Scan;
import cn.edu.fudan.scanservice.domain.ScanMessageWithTime;
import cn.edu.fudan.scanservice.util.DateTimeUtil;
import cn.edu.fudan.scanservice.util.ExecuteShellUtil;
import cn.edu.fudan.scanservice.util.JGitHelper;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component("AACS")
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

    private ScanDao scanDao;

    @Autowired
    public void setScanDao(ScanDao scanDao) {
        this.scanDao = scanDao;
    }


    @Override
    public List<ScanMessageWithTime> filter(Map<LocalDate, List<ScanMessageWithTime>> map, List<LocalDate> dates) {
        String repoId = StrategyUtil.getRepoIdByMsg(map);
        String repoPath = null;
        LinkedList<ScanMessageWithTime> result = new LinkedList<>();
        LocalDateTime latestCommitTime = null;
        LocalDateTime firstScanCommitTime = null;
        List<RevCommit> qualified = null;
        String  baseCommitId = null;
        boolean isHaveFindAggregation = false;


        JSONObject jsonObject = restInterfaceManager.getCommitsOfRepoByConditions(repoId, 1, 1, null);
        JSONArray scanMessageWithTimeJsonArray = jsonObject.getJSONArray("data");
        JSONObject latestScanMessageWithTime = scanMessageWithTimeJsonArray.getJSONObject(0);
        String completeCommitTime = latestScanMessageWithTime.getString("commit_time");
        String checkCommitId = latestScanMessageWithTime.getString("commit_id");




        Scan scan = scanDao.getScanByCategoryAndRepoIdAndCommitId(repoId,"test", null);

        if(scan != null){
            baseCommitId = scan.getCommit_id();
        }else{
            RevCommit  baseCommit = null;
            try {

                repoPath = restInterfaceManager.getRepoPath(repoId, checkCommitId);
                if (repoPath == null) {
                    return null;
                }


                latestCommitTime = DateTimeUtil.stringToLocalDate(completeCommitTime);
                firstScanCommitTime = latestCommitTime.minusMonths(3).minusDays(5);


                JGitHelper jGitHelper = new JGitHelper(repoPath);
                String formatTime = DateTimeUtil.format(firstScanCommitTime);
                List<RevCommit> listCommits = jGitHelper.getAggregationCommit(formatTime);
                while (listCommits.isEmpty()) {
                    firstScanCommitTime = firstScanCommitTime.minusMonths(6);
                    formatTime = DateTimeUtil.format(firstScanCommitTime);
                    listCommits = jGitHelper.getAggregationCommit(formatTime);
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

                try {
                    repoPathRevCommit = restInterfaceManager.getRepoPath(repoId, revCommit.getName());
                    boolean isCompiled = executeShellUtil.executeMvn(repoPathRevCommit);
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

            baseCommitId = baseCommit.getName();
            isHaveFindAggregation = true;
        }


        int sourceSize = dates.size();
        LinkedList<ScanMessageWithTime> originalCommitList = new LinkedList<>();
        int i = 0;
        while (i < sourceSize) {
            LocalDate date = dates.get(sourceSize - 1 - i);
            List<ScanMessageWithTime> list = map.get(date);
            List<ScanMessageWithTime> sortedList = list.stream().sorted(Comparator.comparing(ScanMessageWithTime::getCommitTime).reversed()).collect(Collectors.toList());
            for (ScanMessageWithTime scanMessageWithTime : sortedList) {
                originalCommitList.addFirst(scanMessageWithTime);
            }
            i++;
        }

        boolean flag = false;

        for (ScanMessageWithTime scanMessageWithTime : originalCommitList) {
            if (flag || scanMessageWithTime.getCommitId().equals(baseCommitId)) {
                result.add(scanMessageWithTime);
                flag = true;
            }
        }

        if(result.isEmpty()){
            if(isHaveFindAggregation){
                Scan insertScan = new Scan();
                insertScan.setCategory("test");
                insertScan.setName("test--" + repoId);
                insertScan.setStatus("test");
                insertScan.setRepo_id(repoId);
                insertScan.setCommit_id(baseCommitId);
                //scan.set
                String uuid = UUID.randomUUID().toString();
                insertScan.setUuid(uuid);
                scanDao.insertOneScan(insertScan);
            }
        }else{
            scanDao.deleteScanByRepoIdAndCategory(repoId,"test");
        }
        return result;
    }


//        if(baseCommit != null){
//            String baseCommitTime = DateTimeUtil.format(DateTimeUtil.timestampToDate(baseCommit.getCommitTime()));
//            LocalDateTime baseDateTime = DateTimeUtil.timestampToDate(baseCommit.getCommitTime()).toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
//            LocalDate baseDate = baseDateTime.toLocalDate();
//
//            int i = 0;
//            boolean isRecent=true;
//            while(i<sourceSize){
//                LocalDate date=dates.get(sourceSize-1-i);
//
//                if(isRecent && (date.isAfter(baseDate) || date.isEqual(baseDate))){
//                    List<ScanMessageWithTime> list=map.get(date);
//                    if(date.isAfter(baseDate)){
//
//                        result = addAllListIntoFirst(result,list);
//                    }else{
//                        List<ScanMessageWithTime> sortedList = list.stream().sorted(Comparator.comparing(ScanMessageWithTime::getCommitTime).reversed()).collect(Collectors.toList());
//                        for(ScanMessageWithTime scanMessageWithTime : sortedList){
//                            if(scanMessageWithTime.getCommitTime().compareTo(baseCommitTime) >= 0){
//                                result.addFirst(scanMessageWithTime);
//                            }
//
//                        }
//
//                    }
//
//                }else{
//                    isRecent=false;
//                }
//
//                i++;
//            }
//        }
//
//        return result;
//    }
//
//    private LinkedList<ScanMessageWithTime> addAllListIntoFirst(LinkedList<ScanMessageWithTime> result,List<ScanMessageWithTime> scanMessageWithTimes){
//        List<ScanMessageWithTime> sortedList = scanMessageWithTimes.stream().sorted(Comparator.comparing(ScanMessageWithTime::getCommitTime).reversed()).collect(Collectors.toList());
//        for(ScanMessageWithTime scanMessageWithTime : sortedList){
//            result.addFirst(scanMessageWithTime);
//        }
//        return result;
//    }
}
