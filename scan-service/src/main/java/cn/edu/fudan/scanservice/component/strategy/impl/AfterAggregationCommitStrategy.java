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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component("AACS")
public class AfterAggregationCommitStrategy implements CommitFilterStrategy<ScanMessageWithTime> {

    @Value("${scan.start.time}")
    private int scanStartTime;

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
    public List<ScanMessageWithTime> filter(Map<LocalDate, List<ScanMessageWithTime>> map, List<LocalDate> dates) throws RuntimeException{
        System.out.println("start from " + scanStartTime + "month ago.");
        String repoId = StrategyUtil.getRepoIdByMsg(map);
        String repoPath = null;
        LinkedList<ScanMessageWithTime> result = new LinkedList<>();
        LocalDateTime latestCommitTime = null;
        LocalDateTime firstScanCommitTime = null;
        List<RevCommit> qualified = null;
        String  baseCommitId = null;
        boolean isHaveFindAggregation = false;


        JSONObject jsonObject = restInterfaceManager.getCommitsOfRepoByConditions(repoId, 1, 1, null);
        if(jsonObject == null){
            throw new RuntimeException("request base server failed");
        }
        JSONArray scanMessageWithTimeJsonArray = jsonObject.getJSONArray("data");
        JSONObject latestScanMessageWithTime = scanMessageWithTimeJsonArray.getJSONObject(0);
        String completeCommitTime = latestScanMessageWithTime.getString("commit_time");
        String checkCommitId = latestScanMessageWithTime.getString("commit_id");


        Scan scan = scanDao.getScanByCategoryAndRepoIdAndCommitId(repoId,"test", null);

        if(scan != null){
            baseCommitId = scan.getCommit_id();
        }else{

            baseCommitId = getBaseCommitId(completeCommitTime, repoId, checkCommitId);

            if(baseCommitId == null){
                return null;
            }
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



    private String getBaseCommitId(String  completeCommitTime, String  repoId, String checkCommitId) throws RuntimeException{
        int findCounts = 0;
        String baseCommitId = null;
        LocalDateTime latestCommitTime = null;
        LocalDateTime firstScanCommitTime = null;


        latestCommitTime = DateTimeUtil.stringToLocalDate(completeCommitTime);
        firstScanCommitTime = latestCommitTime.minusMonths(scanStartTime);

        int allAggregationCommitCount = getAllAggregationCommitCount(repoId,checkCommitId);
        int lastListCommitsSize = 0;
        String lastScannedCommit = null;

        while(baseCommitId == null){
            List<RevCommit> qualified = null;
            RevCommit  baseCommit = null;
            String repoPath = null;

            try {

                repoPath = restInterfaceManager.getRepoPath(repoId, checkCommitId);
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
                    if (repoPathRevCommit == null) {
                        throw new RuntimeException("request base server failed");
                    }

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
