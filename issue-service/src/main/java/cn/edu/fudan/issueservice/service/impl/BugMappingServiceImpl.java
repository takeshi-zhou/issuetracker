package cn.edu.fudan.issueservice.service.impl;
import	java.util.HashMap;

import cn.edu.fudan.issueservice.domain.*;
import cn.edu.fudan.issueservice.util.JGitHelper;
import cn.edu.fudan.issueservice.util.LocationCompare;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author LSW
 * @version 1.0
 **/
@Slf4j
@Service("bugMapping")
public class BugMappingServiceImpl extends BaseMappingServiceImpl {
    private Logger logger = LoggerFactory.getLogger(BaseMappingServiceImpl.class);

    @Override
    public void mapping(String repoId, String preCommitId, String currentCommitId, String category, String committer) throws RuntimeException{

        // 每个merge点应该也需要判断是否是聚合点，如果是聚合点，同时需要对issue的resolution清0，此时可以得出每个issue有多少次被抛弃的修复。
        // 避免对下一次merge的影响。

        log.info("start bug mapping!");
        try{
            //存当前扫描后需要插入的新的issue
            List<Issue> insertIssueList = new ArrayList<>();
            List<JSONObject> tags = new ArrayList<>();
            //当前时间
            Date date = new Date();
            Date commitDate = getCommitDate(currentCommitId,repoId);
            log.info("get commit date success!");
            //获取该项目ignore的issue类型
            JSONArray ignoreTypes = restInterfaceManager.getIgnoreTypesOfRepo(repoId);
            log.info("get ignore types success!");

            List<String> parentCommits =  restInterfaceManager.getPreScannedCommitByCurrentCommit(repoId,currentCommitId,category);
            log.info("get parent commits success!");

            if (parentCommits.isEmpty()) {
                //当前project第一次扫描，所有的rawIssue都 是issue
                List<RawIssue> rawIssues = rawIssueDao.getRawIssueByCommitIDAndCategory(repoId, category, currentCommitId);
                if (rawIssues == null || rawIssues.isEmpty()) {
                    return;
                }
                logger.info("first scan mapping!");
                for (RawIssue rawIssue : rawIssues) {
                    Issue issue=generateOneNewIssue(repoId, rawIssue, category, currentCommitId, commitDate, date);
                    insertIssueList.add(issue);
                    addTag(tags, ignoreTypes, rawIssue,issue);
                    addIssueTypeTag(tags,rawIssue,issue);
                }
                int newIssueCount = insertIssueList.size();
                int remainingIssueCount = insertIssueList.size();
                int eliminatedIssueCount = 0;
                logger.info("first mapping -> new:{},remaining:{},eliminated:{}", newIssueCount, remainingIssueCount, eliminatedIssueCount);
                dashboardUpdate(repoId, newIssueCount, remainingIssueCount, eliminatedIssueCount,category);
                logger.info("dashboard info updated!");
                //更新rawIssue的状态
                for(RawIssue rawIssue : rawIssues){
                    rawIssue.setStatus(RawIssueStatus.ADD.getType());
                }
                if(!rawIssues.isEmpty()){
                    rawIssueDao.batchUpdateIssueIdAndStatus(rawIssues);
                }

                String[] currentCommitParentCommits = getParentCommits(repoId,currentCommitId);
                int devAddIssues = 0;
                if(currentCommitParentCommits != null && currentCommitParentCommits.length == 0){
                    devAddIssues = newIssueCount;
                }else{
                    logger.error("can't get commit parent commits!");
                }
                scanResultDao.addOneScanResult(new ScanResult(category,repoId,date,currentCommitId,commitDate,committer,devAddIssues,eliminatedIssueCount,remainingIssueCount));
            } else {

                if(parentCommits.size() == 1){
                    log.info("not first mapping and parent commit is one!");
                    preCommitId = parentCommits.get(0);
                    issueMapping(repoId, category, preCommitId, currentCommitId, commitDate,
                            date, insertIssueList, tags, ignoreTypes, committer, committer);
                }else{
                    log.info("not first mapping and count of parent commits  is more than one!");
                    boolean isAggregation = verifyWhetherAggregation(repoId, currentCommitId);
                    log.info("judge is aggregation --> {}",isAggregation);
                    issueMapping(repoId, category, parentCommits, currentCommitId, commitDate,
                            date, insertIssueList, tags, ignoreTypes, committer, committer,isAggregation);
                }

            }
            //新的issue
            if (!insertIssueList.isEmpty()) {
                int errorCount = 0;
                for(Issue issue :insertIssueList){

                    if(issue.getStatus()==null){
                        errorCount++;
                    }


                }
                logger.error(" the status of {}  issues  is null! " , errorCount);
                issueDao.insertIssueList(insertIssueList);
                issueEventManager.sendIssueEvent(EventType.NEW_BUG,insertIssueList,committer,repoId,commitDate);
                newIssueInfoUpdate(insertIssueList,category,repoId);
                logger.info("new issue insert success! size:{} " ,insertIssueList.size());
            }
            //打tag
            if(!tags.isEmpty()){
                restInterfaceManager.addTags(tags);
            }
            logger.info("mapping finished!");
        }finally{

        }


    }

    private void issueMapping(String repoId, String category, String preCommitId, String currentCommitId, Date commitDate,
                              Date date, List<Issue> insertIssueList, List<JSONObject> tags, JSONArray ignoreTypes, String committer, String developer) {
        //不是第一次扫描，需要和前一次的commit产生的issue进行mapping
        logger.info("not first mapping!");
        if(preCommitId == null || preCommitId.isEmpty()){
            preCommitId = rawIssueDao.getPreCommitIdByCurrentCommitId(repoId,category,currentCommitId);
        }
        logger.info("get preCommitId success!");
        List<RawIssue> preRawIssues = rawIssueDao.getRawIssueByCommitIDAndCategory(repoId, category, preCommitId);
        logger.info("get preRawIssues success!");
        List<RawIssue> currentRawIssues = rawIssueDao.getRawIssueByCommitIDAndCategory(repoId, category, currentCommitId);
        logger.info("get currentRawIssues success!");
        if (currentRawIssues == null || currentRawIssues.isEmpty()) {
            logger.info("all issues were solved or raw issue insert error , commit id -->  {}", currentCommitId);
        }


        //mapping开始之前end commit是上一个commit的表示是上个commit存活的issue
        Set<String> existsIssueIds = issueDao.getIssuesByEndCommit(repoId, category, preCommitId).stream().map(Issue::getUuid).collect(Collectors.toSet());
        //装需要更新的
        List<Issue> issues = new ArrayList<>();
        List<String> mappedIssueIds = new ArrayList<>();
        int equalsCount = 0;
        int ignoreCountInNewIssues = 0;
        int ignoreCountInEliminatedIssues = 0;
        int mappedCount = 0;
        int eliminatedIssueCount = 0;
        int remainingIssueChangedCount = 0;
        int notAdoptEliminateCount = 0;


        Map<String,List<RawIssueMappingSort>>  currentRawIssueMappedRawIssueList= new HashMap<>();
        //用来判断匹配上的preRawIssue的下标位置，-1表示未匹配上。
        Map<String,Integer> currentRawIssueMappedIndex = new HashMap<>();
        //key 是preRawIssue的uuid,value是current raw issue的uuid,用来记录preRawIssue匹配了哪个currentRawIssue
        Map<String,RawIssue> preRawIssueMappedCurrentRawIssue = new HashMap<>();

        //两个rawIssue进行mapping
        mappingTwoRawIssueList(preRawIssues,currentRawIssues,currentRawIssueMappedRawIssueList,currentRawIssueMappedIndex,preRawIssueMappedCurrentRawIssue);

        List<RawIssue> mappedPreSolvedRawIssues  = new ArrayList<>();

        for (RawIssue currentRawIssue : currentRawIssues) {
            int mappedPreRawIssueIndex = currentRawIssueMappedIndex.get(currentRawIssue.getUuid());
            if( mappedPreRawIssueIndex == -1){
                Issue issue = generateOneNewIssue(repoId,currentRawIssue,category,currentCommitId,commitDate,date);
                insertIssueList.add(issue);
                addTag(tags, ignoreTypes, currentRawIssue,issue);
                addIssueTypeTag(tags,currentRawIssue,issue);
                currentRawIssue.setStatus(RawIssueStatus.ADD.getType());
            }else{
                RawIssue preRawIssue = currentRawIssueMappedRawIssueList.get(currentRawIssue.getUuid()).get(mappedPreRawIssueIndex).getRawIssue();
                String preIssueId = preRawIssue.getIssue_id();
                //只有和上个commit存活的issue匹配上才算真正匹配上

                equalsCount++;
                currentRawIssue.setIssue_id(preIssueId);
                Issue issue = issueDao.getIssueByID(preIssueId);
                String status = issue.getStatus();
                if(StatusEnum.SOLVED.getName().equals(status)){
                    //此处先暂时置为Open，等后面结合ignore_record表再确定什么类型
                    if(issue.getManual_status() != null){
                        issue.setStatus(issue.getManual_status());
                    }else{
                        issue.setStatus(StatusEnum.OPEN.getName());
                    }

                    mappedPreSolvedRawIssues.add(preRawIssue);
                    String resolution = issue.getResolution();
                    if(resolution == null){
                        issue.setResolution(String.valueOf(1));
                    }else{
                        issue.setResolution(String.valueOf(Integer.parseInt(resolution) + 1));
                    }

                }

                issue.setEnd_commit(currentCommitId);
                issue.setEnd_commit_date(commitDate);
                issue.setRaw_issue_end(currentRawIssue.getUuid());
                issue.setUpdate_time(new Date());
                issues.add(issue);
                mappedIssueIds.add(preIssueId);

                //判断rawIssue 是否发生变化
                List<Location> preRawIssueLocations = locationDao.getLocations(preRawIssue.getUuid());
                List<Location> currentRawIssueLocations = locationDao.getLocations(currentRawIssue.getUuid());
                if(preRawIssueLocations.size() == currentRawIssueLocations.size()){
                    Location preRawIssueLocation = preRawIssueLocations.get(0);
                    Location currentRawIssueLocation = currentRawIssueLocations.get(0);
                    if(!preRawIssueLocation.judgeWhetherLocationIsEqual(currentRawIssueLocation)){
                        currentRawIssue.setStatus(RawIssueStatus.CHANGED.getType());
                    }
                }

                continue;

            }
        }




        for(RawIssue preRawIssue : preRawIssues){
            if(preRawIssue.isMapped()){
                mappedCount++;
            }else{
                eliminatedIssueCount++;
            }
        }



        List<Issue> solvedIssues = new ArrayList<>();
        Map<String,String> solvedIssuesPreStatusTag = new HashMap<>();

        // 标记为solved 的raw issue 集合
        List<RawIssue> solvedStatusRawIssues = new ArrayList<>();

        //存储上个commit没匹配上的，也就是被solved的rawIssue的信息
        List<RawIssue> list = preRawIssues.stream().filter(rawIssue -> !rawIssue.isMapped()).collect(Collectors.toList());
        for(RawIssue solvedRawIssue : list){
            Issue issue = issueDao.getIssueByID(solvedRawIssue.getIssue_id());
            if(StatusEnum.SOLVED.getName().equals(issue.getStatus())){
                notAdoptEliminateCount++;
                String resolution = issue.getResolution();
                if(resolution == null){
                    issue.setResolution(String.valueOf(1));
                }else{
                    issue.setResolution(String.valueOf(Integer.parseInt(resolution) + 1));
                }

            }else if(judgeStatusIsClosedButNotSolved(issue.getStatus())){
                ignoreCountInEliminatedIssues++;
                solvedIssuesPreStatusTag.put(issue.getUuid(),issue.getStatus());
                issue.setStatus(StatusEnum.SOLVED.getName());

                solvedIssues.add(issue);
            }else{
                solvedIssuesPreStatusTag.put(issue.getUuid(),issue.getStatus());
                issue.setStatus(StatusEnum.SOLVED.getName());
                solvedIssues.add(issue);
            }
            issues.add(issue);

            // 插入一条raw issue 标记为解决
            RawIssue solvedStatusRawIssue = generateSolvedStatusRawIssue(solvedRawIssue,currentCommitId,commitDate);
            solvedStatusRawIssues.add(solvedStatusRawIssue);

        }
        saveSolvedInfo(list,repoId,preCommitId,currentCommitId);
        if (!issues.isEmpty()) {
            //更新issue
            issueDao.batchUpdateIssue(issues);
            logger.info("issue update success!");
        }

        modifyToOpenTagByRawIssues(mappedPreSolvedRawIssues);
        //在匹配的issue中上次commit被ignore的个数
        //原先的计算方式，存在问题
//        int ignoredCountInMappedIssues=mappedIssueIds.isEmpty()?0:issueDao.getIgnoredCountInMappedIssues(ignoreTagId,mappedIssueIds);
//        int eliminatedIssueCount = preRawIssues.size() - equalsCount+ignoreCountInNewIssues+ignoredCountInMappedIssues;
//        int remainingIssueCount = currentRawIssues.size()-ignoreCountInNewIssues-ignoredCountInMappedIssues;
//        int newIssueCount = currentRawIssues.size() - equalsCount-ignoreCountInNewIssues;

        for(RawIssue currentRawIssue : currentRawIssues){
            if(currentRawIssue.getIssue_id() == null){
                logger.error("update rawIssue error , commit --> {}",currentCommitId );
            }

        }

        int newIssueCount = insertIssueList.size();
        remainingIssueChangedCount = mappedPreSolvedRawIssues.size() + newIssueCount - eliminatedIssueCount + notAdoptEliminateCount + ignoreCountInEliminatedIssues;
        logger.info("finish mapping -> new:{},remainingChangedCount:{},eliminated:{}",newIssueCount, remainingIssueChangedCount, eliminatedIssueCount);
        dashboardUpdateForMergeVersion(repoId, newIssueCount, remainingIssueChangedCount, eliminatedIssueCount,category);
        logger.info("dashboard info updated!");
        if(!currentRawIssues.isEmpty()){
            rawIssueDao.batchUpdateIssueIdAndStatus(currentRawIssues);
        }
        if(!solvedStatusRawIssues.isEmpty()){
            rawIssueDao.insertRawIssueList(solvedStatusRawIssues);
        }

        modifyToSolvedTag(solvedIssues,true,true,EventType.ELIMINATE_BUG, committer,commitDate,repoId,category,solvedIssuesPreStatusTag);
//        modifyToSolvedTag(repoId, category, preCommitId, EventType.ELIMINATE_BUG, committer, commitDate);
        scanResultDao.addOneScanResult(new ScanResult(category,repoId,date,currentCommitId,commitDate,developer,newIssueCount,eliminatedIssueCount-ignoreCountInEliminatedIssues,currentRawIssues.size()));
    }


    private void issueMapping(String repoId, String category, List<String> parentCommits, String currentCommitId, Date commitDate,
                              Date date, List<Issue> insertIssueList, List<JSONObject> tags, JSONArray ignoreTypes, String committer, String developer, boolean isAggregation) {

        logger.info("not first mapping!");

        int equalsCount = 0;
        int ignoreCountInNewIssues = 0;
        int ignoreCountInEliminatedIssues = 0;
        int mappedCount = 0;
        int eliminatedIssueCount = 0;
        int actualEliminatedIssueCount = 0;
        int notAdoptEliminateCount = 0;
        int remainingIssueChangedCount = 0;

        List<Issue> issues = new ArrayList<>();
        List<String> mappedIssueIds = new ArrayList<>();

        int parentCommitsSize = parentCommits.size();
        List<Map<String,List<RawIssueMappingSort>>> currentRawIssueMappedRawIssueLists = new ArrayList<>();
        List<Map<String,Integer>> currentRawIssueMappedIndexes = new ArrayList<>();
        List<Map<String,RawIssue>> preRawIssueMappedCurrentRawIssues = new ArrayList<>();
        List<List<RawIssue>> parentCommitRawIssueList = new ArrayList<>();

        for(int i = 0; i < parentCommitsSize ; i++){
            currentRawIssueMappedRawIssueLists.add(new HashMap<String,List<RawIssueMappingSort>>());
            currentRawIssueMappedIndexes.add(new HashMap<>());
            preRawIssueMappedCurrentRawIssues.add(new HashMap<>());
        }
        List<RawIssue> currentRawIssues = rawIssueDao.getRawIssueByCommitIDAndCategory(repoId, category, currentCommitId);
        if (currentRawIssues == null || currentRawIssues.isEmpty()) {
            logger.info("all issues were solved or raw issue insert error , commit id -->  {}", currentCommitId);
        }

        //用于全匹配的issue列表
        List<Issue>  allIssues = new ArrayList<>();
        //记录匹配上的所有的issue列表
        List<Issue>  allMappedIssues = new ArrayList<>();
        //记录未匹配上的所有的issue列表
        List<Issue>  allNotMappedIssues = new ArrayList<>();

        for(int j = 0; j < parentCommitsSize ; j++){
            List<RawIssue> parentRawIssues = rawIssueDao.getRawIssueByCommitIDAndCategory(repoId, category, parentCommits.get(j));
            parentRawIssues = filterRawIssue(parentRawIssues,allIssues);
            parentCommitRawIssueList.add(parentRawIssues);

            Map<String,List<RawIssueMappingSort>>  currentRawIssueMappedRawIssueList= currentRawIssueMappedRawIssueLists.get(j);
            //用来判断匹配上的preRawIssue的下标位置，-1表示未匹配上。
            Map<String,Integer> currentRawIssueMappedIndex = currentRawIssueMappedIndexes.get(j);
            //key 是preRawIssue的uuid,value是current raw issue的uuid,用来记录preRawIssue匹配了哪个currentRawIssue
            Map<String,RawIssue> preRawIssueMappedCurrentRawIssue = preRawIssueMappedCurrentRawIssues.get(j);


            mappingTwoRawIssueList(parentRawIssues,currentRawIssues,currentRawIssueMappedRawIssueList,currentRawIssueMappedIndex,preRawIssueMappedCurrentRawIssue);
        }

        Map<String,Integer> mostMappingParentCommit = new HashMap<>();

        for(RawIssue currentRawIssue : currentRawIssues){
            String currentRawIssueUUid = currentRawIssue.getUuid();
            int mostLikelyIndex = -1;
            double matchingDegree = 0;
            for(int m = 0 ; m < parentCommitsSize ; m++){
                int index = currentRawIssueMappedIndexes.get(m).get(currentRawIssueUUid);
                if(index == -1){
                    continue;
                }
                double oneMatchingDegree = currentRawIssueMappedRawIssueLists.get(m).get(currentRawIssueUUid).get(index).getMappingSort() ;
                if(oneMatchingDegree > matchingDegree){
                    matchingDegree = oneMatchingDegree;
                    mostLikelyIndex = m;
                }

            }
            mostMappingParentCommit.put(currentRawIssueUUid,mostLikelyIndex);
        }

        //对未匹配上的parentCommit的相应的current raw issue 的下标置为-1,同时移除pre raw issue 匹配的current raw issue id,同时将is mapped置为false
        for(RawIssue currentRawIssue : currentRawIssues){
            String currentRawIssueUUid = currentRawIssue.getUuid();
            int mostLikelyIndex = mostMappingParentCommit.get(currentRawIssueUUid);
            if(mostLikelyIndex == -1){
                continue;
            }
            for(int n = 0 ; n < parentCommitsSize ; n++){
                if(mostLikelyIndex != n){
                    int earlyIndex = currentRawIssueMappedIndexes.get(n).get(currentRawIssueUUid);
                    if(earlyIndex != -1){
                        currentRawIssueMappedIndexes.get(n).put(currentRawIssueUUid,-1);
                        currentRawIssueMappedRawIssueLists.get(n).get(currentRawIssueUUid).get(earlyIndex).getRawIssue().setMapped(false);
                        preRawIssueMappedCurrentRawIssues.get(n).entrySet().removeIf(value -> value.equals(currentRawIssueUUid));
                    }
                }
            }
        }

        //缺陷变化commit
        String causeIssueChangedCommit = null;

        // ----------------获取缺陷引入commit ---------------------
        //此时先获取parent commit 是否存在编译失败的情况，如果存在就归给编译失败的commit
        //1.先判断当前commit是否是merge 点
        String[] verifyParentCommitsIsMerge = getParentCommits(repoId,currentCommitId);
        if(verifyParentCommitsIsMerge.length < 2){
            causeIssueChangedCommit = currentCommitId;
        }else{

            String latestFailedCommit = restInterfaceManager.getLatestScanFailedCommitId(repoId,currentCommitId,category);
            if(latestFailedCommit != null ){
                String latestFailedCommitAndIsNotMerge = getCompileFailedParentCommit(repoId,latestFailedCommit,category);

                if(latestFailedCommitAndIsNotMerge == null){
                    causeIssueChangedCommit = currentCommitId;
                }else{
                    causeIssueChangedCommit = latestFailedCommitAndIsNotMerge;
                }

            }else{
                causeIssueChangedCommit = currentCommitId;
            }



        }

        Date causeIssueChangedCommitDate = getCommitDate(causeIssueChangedCommit , repoId);
        String causeIssueChangedCommitDeveloper = getDeveloper(causeIssueChangedCommit,repoId);

        // -------------------------------------------------

        List<RawIssue> mappedPreSolvedRawIssues  = new ArrayList<>();

        for (RawIssue currentRawIssue : currentRawIssues) {
            String currentRawIssueUUid = currentRawIssue.getUuid();
            int mostLikelyIndex = mostMappingParentCommit.get(currentRawIssueUUid);
            if(mostLikelyIndex == -1){

                //按理merge不存在修改代码的情况，此时不应该引入新的缺陷。

                Issue issue = generateOneNewIssue(repoId,currentRawIssue,category,causeIssueChangedCommit,causeIssueChangedCommitDate,date);
                issue.setEnd_commit(currentCommitId);
                issue.setEnd_commit_date(commitDate);
                insertIssueList.add(issue);
                addTag(tags, ignoreTypes, currentRawIssue,issue);
                addIssueTypeTag(tags,currentRawIssue,issue);
                currentRawIssue.setStatus(RawIssueStatus.ADD.getType());

            }else{
                Map<String,List<RawIssueMappingSort>> currentRawIssueMappedRawIssueList = currentRawIssueMappedRawIssueLists.get(mostLikelyIndex);
                Map<String,Integer> currentRawIssueMappedIndex = currentRawIssueMappedIndexes.get(mostLikelyIndex);

                int mappedPreRawIssueIndex = currentRawIssueMappedIndex.get(currentRawIssue.getUuid());
                RawIssue preRawIssue = currentRawIssueMappedRawIssueList.get(currentRawIssue.getUuid()).get(mappedPreRawIssueIndex).getRawIssue();
                String preIssueId = preRawIssue.getIssue_id();

                equalsCount++;
                currentRawIssue.setIssue_id(preIssueId);
                Issue issue = issueDao.getIssueByID(preIssueId);

                String status = issue.getStatus();
                if(StatusEnum.SOLVED.getName().equals(status)){
                    //此处先暂时置为Open，等后面结合ignore_record表再确定什么类型
                    if(issue.getManual_status() != null){
                        issue.setStatus(issue.getManual_status());
                    }else{
                        issue.setStatus(StatusEnum.OPEN.getName());
                    }
                    notAdoptEliminateCount++;
                    mappedPreSolvedRawIssues.add(preRawIssue);
                    String resolution = issue.getResolution();
                    if(resolution == null){
                        issue.setResolution(String.valueOf(1));
                    }else{
                        issue.setResolution(String.valueOf(Integer.parseInt(resolution) + 1));
                    }

                }

                issue.setEnd_commit(currentCommitId);
                issue.setEnd_commit_date(commitDate);
                issue.setRaw_issue_end(currentRawIssue.getUuid());
                issue.setUpdate_time(new Date());
                issues.add(issue);
                mappedIssueIds.add(preIssueId);
                allMappedIssues.add(issue);

                //判断rawIssue 是否发生变化
                List<Location> preRawIssueLocations = locationDao.getLocations(preRawIssue.getUuid());
                List<Location> currentRawIssueLocations = locationDao.getLocations(currentRawIssue.getUuid());
                if(preRawIssueLocations.size() == currentRawIssueLocations.size()){
                    Location preRawIssueLocation = preRawIssueLocations.get(0);
                    Location currentRawIssueLocation = currentRawIssueLocations.get(0);
                    if(!preRawIssueLocation.judgeWhetherLocationIsEqual(currentRawIssueLocation)){
                        currentRawIssue.setStatus(RawIssueStatus.CHANGED.getType());
                    }
                }
            }
        }




        for(Issue issue : allIssues){
            boolean flag = true;
            for(Issue mappedIssue : allMappedIssues){
                if(mappedIssue.getUuid().equals(issue.getUuid())){
                    flag = false;
                    break;
                }
            }

            if(flag){
                allNotMappedIssues.add(issue);
            }
        }

        List<Issue> needToModifyTagToSolvedIssue = new ArrayList<>();
        Map<String,String> solvedIssuesPreStatusTag = new HashMap<>();

        // 标记为solved 的raw issue 集合
        List<RawIssue> solvedStatusRawIssues = new ArrayList<>();

        for(Issue notMappedIssue : allNotMappedIssues){
            String resolution = notMappedIssue.getResolution();
            if(resolution == null){
                resolution = String.valueOf(0);
            }
            if(StatusEnum.SOLVED.getName().equals(notMappedIssue.getStatus())){
                //后面结合code tracker，判断该消除是采用了哪种消除
            }else if(judgeStatusIsClosedButNotSolved(notMappedIssue.getStatus())){
                ignoreCountInEliminatedIssues++;
                solvedIssuesPreStatusTag.put(notMappedIssue.getUuid(),notMappedIssue.getStatus());
                notMappedIssue.setStatus(StatusEnum.SOLVED.getName());
                notMappedIssue.setUpdate_time(new Date());
                needToModifyTagToSolvedIssue.add(notMappedIssue);
                issues.add(notMappedIssue);
            }else{

                //有两大类：
                // 其中一大类可能是merge点存在conflict，修改了相应的代码解决了问题
                //第二大类是缺陷在parent commit中就已经被解决了

                solvedIssuesPreStatusTag.put(notMappedIssue.getUuid(),notMappedIssue.getStatus());
                if(resolution.equals("0")){
                    //第一大类
                    //这种情况 后续补充,包括发送解决问题的信息，
                    notMappedIssue.setStatus("Solved");
                    actualEliminatedIssueCount++;
                }else{
                    //第二大类
                    notMappedIssue.setStatus(StatusEnum.SOLVED.getName());
                    notMappedIssue.setUpdate_time(new Date());
                    notMappedIssue.setResolution(String.valueOf(Integer.parseInt(resolution) - 1));

                }
                needToModifyTagToSolvedIssue.add(notMappedIssue);
                issues.add(notMappedIssue);
                eliminatedIssueCount++;
            }

            // 插入一条raw issue 标记为解决
            RawIssue rawIssue = rawIssueDao.getRawIssueById(notMappedIssue.getRaw_issue_end());
            RawIssue solvedStatusRawIssue = generateSolvedStatusRawIssue(rawIssue,currentCommitId,commitDate);
            solvedStatusRawIssues.add(solvedStatusRawIssue);
        }

        modifyToSolvedTag(needToModifyTagToSolvedIssue,false,false,null,null,null,null,null,solvedIssuesPreStatusTag);



        if (!issues.isEmpty()) {
            //更新issue
            issueDao.batchUpdateIssue(issues);
            logger.info("issue update success!");
        }



        int newIssueCount = insertIssueList.size();
        if(causeIssueChangedCommit == currentCommitId && newIssueCount != 0 ){
            logger.error("issue mapping may exists bugs");
        }
        remainingIssueChangedCount = notAdoptEliminateCount + newIssueCount -  eliminatedIssueCount;

        int realNotAdoptEliminateCount = 0;
        if(isAggregation){
            List<Issue> haveNotAdoptEliminateIssues = issueDao.getHaveNotAdoptEliminateIssuesByCategoryAndRepoId(repoId,category);
            for(Issue manyChangedIssue : haveNotAdoptEliminateIssues){
                String resolution = manyChangedIssue.getResolution();
                realNotAdoptEliminateCount += Integer.parseInt(resolution);
                manyChangedIssue.setResolution("0");
            }

            if(!haveNotAdoptEliminateIssues.isEmpty()){
                issueDao.batchUpdateIssue(haveNotAdoptEliminateIssues);
                logger.info(" have not adopt eliminate issues update success!");
            }

        }

        modifyToOpenTagByRawIssues(mappedPreSolvedRawIssues);
        logger.info("finish mapping -> new:{},remainingChangedCount:{},eliminated:{}",newIssueCount, remainingIssueChangedCount, actualEliminatedIssueCount-realNotAdoptEliminateCount);
        dashboardUpdateForMergeVersion(repoId, newIssueCount, remainingIssueChangedCount, actualEliminatedIssueCount-realNotAdoptEliminateCount,category);
        logger.info("dashboard info updated!");
        if(!currentRawIssues.isEmpty()){
            rawIssueDao.batchUpdateIssueIdAndStatus(currentRawIssues);
        }
        if(!solvedStatusRawIssues.isEmpty()){
            rawIssueDao.insertRawIssueList(solvedStatusRawIssues);
        }


        scanResultDao.addOneScanResult(new ScanResult(category,repoId,date,causeIssueChangedCommit,causeIssueChangedCommitDate,causeIssueChangedCommitDeveloper,newIssueCount,actualEliminatedIssueCount,currentRawIssues.size()));



    }

    private void findBestMatching(Map<String,RawIssue> preRawIssueMappedCurrentRawIssue , Map<String,Integer> currentRawIssueMappedIndex ,
                                      Map<String,List<RawIssueMappingSort>>  currentRawIssueMappedRawIssueList ,
                                      RawIssue currentRawIssue,
                                      int mappingIndex){
        String currentRawIssueId = currentRawIssue.getUuid();
        if(currentRawIssueMappedIndex.get(currentRawIssueId) == null){
            currentRawIssueMappedIndex.put(currentRawIssueId,-1);
        }
        if(currentRawIssueMappedIndex.get(currentRawIssueId) == -1){
            return ;
        }

        List<RawIssueMappingSort> mappedPreRawIssues = currentRawIssueMappedRawIssueList.get(currentRawIssueId);
        for(int i = mappingIndex+1 ; i < mappedPreRawIssues.size() ; i++){
            RawIssueMappingSort rawIssueMappingSort  = mappedPreRawIssues.get(i);
            RawIssue rawIssue = rawIssueMappingSort.getRawIssue();
            //如果已经匹配过则比对两个的比对值的大小
            if(rawIssue.isMapped()){
                RawIssue otherMatchedCurrentRawIssue = preRawIssueMappedCurrentRawIssue.get(rawIssue.getUuid());
                Integer otherMatchedCurrentRawIssueIndex = currentRawIssueMappedIndex.get(otherMatchedCurrentRawIssue.getUuid());
                Double otherMatchedCurrentRawIssueMatchedDegree = currentRawIssueMappedRawIssueList.get(otherMatchedCurrentRawIssue.getUuid()).get(otherMatchedCurrentRawIssueIndex).getMappingSort();

                if(otherMatchedCurrentRawIssueMatchedDegree.compareTo(rawIssueMappingSort.getMappingSort()) >= 0){
                    continue;
                }else{
                    preRawIssueMappedCurrentRawIssue.put(rawIssue.getUuid(),currentRawIssue);
                    currentRawIssueMappedIndex.put(currentRawIssue.getUuid(),i);
                    //匹配度不高的current raw issue重新匹配
                    findBestMatching(preRawIssueMappedCurrentRawIssue,currentRawIssueMappedIndex,currentRawIssueMappedRawIssueList,otherMatchedCurrentRawIssue,otherMatchedCurrentRawIssueIndex);
                    return;
                }
            }else{
                rawIssue.setMapped(true);
                preRawIssueMappedCurrentRawIssue.put(rawIssue.getUuid(),currentRawIssue);
                currentRawIssueMappedIndex.put(currentRawIssue.getUuid(),i);
                return;
            }

        }

        currentRawIssueMappedIndex.put(currentRawIssue.getUuid(),-1);

    }



    private List<RawIssueMappingSort> findMostSimilarRawIssues(RawIssue currentRawIssue, List<RawIssue> preList) {


        //因为重写了RawIssue的hashcode 以及equals方法，故可以使用RawIssue作为key
        List<RawIssueMappingSort> similarRawIssues = new ArrayList<>();


        //获取currentRawIssue的location
        Location currentLocation = currentRawIssue.getLocations().isEmpty() ? null : currentRawIssue.getLocations().get(0);
        if(currentLocation == null){
            return null;
        }

        double maxCommonality = 0;
        double maxLcs = 0;
        double commonality ;
        double overLapping ;
        double lcs ;
        boolean isFirstLevMapped = false;
        boolean isOverLappingHadAnalyzed = false;
        for (RawIssue rawIssue : preList) {

            LocationCompare.computeSimilarity(currentRawIssue, rawIssue);
            overLapping = LocationCompare.getOverLapping();
            commonality = LocationCompare.getCommonality();
            lcs = LocationCompare.getLcs();


            //判断是否是同一个文件，同一个类，同一个方法名，bug lines的数目相同------此时的mapping的优先级最高
            //目前commonality 不是0就是1，所以不存在错误，当存在多个location时，下面的逻辑需要修改。
            if(commonality > LocationCompare.getThresholdCommonality()){
                if(commonality > maxCommonality && overLapping == 1.0 ){

                    maxCommonality = commonality;
                    isOverLappingHadAnalyzed = true;
                    RawIssueMappingSort rawIssueMappingSort = new RawIssueMappingSort();
                    rawIssueMappingSort.setRawIssue(rawIssue);
                    rawIssueMappingSort.setMappingSort(lcs + overLapping + commonality);
                    similarRawIssues.add(rawIssueMappingSort);
                    isFirstLevMapped = true;
                    continue;



                }else if (commonality == maxCommonality && overLapping == 1.0){

                    maxCommonality = commonality;
                    isOverLappingHadAnalyzed = true;
                    isFirstLevMapped = true;
                    RawIssueMappingSort rawIssueMappingSort = new RawIssueMappingSort();
                    rawIssueMappingSort.setRawIssue(rawIssue);
                    rawIssueMappingSort.setMappingSort(lcs + overLapping + commonality);
                    similarRawIssues.add(rawIssueMappingSort);
                    continue;

                }

                if(!isOverLappingHadAnalyzed  && lcs > LocationCompare.getThresholdLcs()){

                    maxCommonality = commonality;
                    RawIssueMappingSort rawIssueMappingSort = new RawIssueMappingSort();
                    rawIssueMappingSort.setRawIssue(rawIssue);
                    rawIssueMappingSort.setMappingSort(lcs + overLapping + commonality);
                    similarRawIssues.add(rawIssueMappingSort);
                    continue;


                }

            }else {
                Location preLocation = rawIssue.getLocations().isEmpty() ? null : rawIssue.getLocations().get(0);
                if(  !isFirstLevMapped && currentLocation.isInSameClass(preLocation)  && lcs > LocationCompare.getDiffMethodsThresholdLcs()){


                    RawIssueMappingSort rawIssueMappingSort = new RawIssueMappingSort();
                    rawIssueMappingSort.setRawIssue(rawIssue);
                    rawIssueMappingSort.setMappingSort(lcs + overLapping + commonality);
                    similarRawIssues.add(rawIssueMappingSort);

                }
            }

        }

        List<RawIssueMappingSort> similarRawIssueSorted = similarRawIssues.stream().sorted(Comparator.comparing(RawIssueMappingSort :: getMappingSort).reversed()).collect(Collectors.toList());



        return similarRawIssueSorted;
    }




    private Map<String, List<RawIssue>> classifyRawIssue(List<RawIssue> rawIssues) {
        Map<String, List<RawIssue>> map = new HashMap<>(2);
        for (RawIssue rawIssue : rawIssues) {
            Assert.notEmpty(rawIssue.getLocations(),"no location");
            Location location = rawIssue.getLocations().get(0);
            String key = location.getFile_path() + " " + rawIssue.getDetail();
            if (map.containsKey(key)) {
                map.get(key).add(rawIssue);
            } else {
                List<RawIssue> list = new ArrayList<>();
                list.add(rawIssue);
                map.put(key, list);
            }
        }
        return map;
    }


    private int getPriorityByRawIssue(RawIssue rawIssue){
        int priority =  Integer.parseInt(JSONObject.parseObject(rawIssue.getDetail(),RawIssueDetail.class).getRank())/5 + 1 ;
        priority = priority == 5 ? 4 : priority ;
        return priority;
    }

    //根据rawIssue产生一个新的Issue对象
    private Issue generateOneNewIssue(String repoId,RawIssue rawIssue,String category,String currentCommitId,Date currentCommitDate,Date addTime){
        String newIssueId = UUID.randomUUID().toString();
        rawIssue.setIssue_id(newIssueId);
        String targetFiles = rawIssue.getFile_name();
        boolean hasDisplayId=issueDao.getMaxIssueDisplayId(repoId) != null;
        if (isDefaultDisplayId){
             currentDisplayId = hasDisplayId ? issueDao.getMaxIssueDisplayId(repoId) : 0;
            isDefaultDisplayId = false;
        }
        // 映射 （1-4）1 、（5-9）2、（10-14）3 、（15 -20） 4
        int priority =  Integer.parseInt(JSONObject.parseObject(rawIssue.getDetail(),RawIssueDetail.class).getRank())/5 + 1 ;
        priority = priority == 5 ? 4 : priority ;
        Issue issue = new Issue(newIssueId, rawIssue.getType(),category, currentCommitId,
                currentCommitDate, currentCommitId,currentCommitDate, rawIssue.getUuid(),
                rawIssue.getUuid(), repoId, targetFiles,addTime,addTime,++currentDisplayId);
        issue.setPriority(priority);
        issue.setStatus(StatusEnum.OPEN.getName());
        return issue;
    }

    /**
     * 更改中，比对两个raw issue列表
     * @param preRawIssues
     * @param currentRawIssues
     */

    private void mappingTwoRawIssueList(List<RawIssue> preRawIssues,List<RawIssue> currentRawIssues,Map<String,
                                        List<RawIssueMappingSort>>  currentRawIssueMappedRawIssueList,
                                        Map<String,Integer> currentRawIssueMappedIndex,
                                        Map<String,RawIssue> preRawIssueMappedCurrentRawIssue){
        Map<String, List<RawIssue>> curRawIssueMap = classifyRawIssue(currentRawIssues);
        Map<String, List<RawIssue>> preRawIssueMap = classifyRawIssue(preRawIssues);


        for (Map.Entry<String, List<RawIssue>> entry : curRawIssueMap.entrySet()) {
            if (preRawIssueMap.containsKey(entry.getKey())) {
                List<RawIssue> preList = preRawIssueMap.get(entry.getKey());
                for (RawIssue currentRawIssue : entry.getValue()) {
                    List<RawIssueMappingSort> mappedRawIssues = findMostSimilarRawIssues(currentRawIssue,preList);
                    currentRawIssueMappedRawIssueList.put(currentRawIssue.getUuid(),mappedRawIssues);
                    if(mappedRawIssues.isEmpty()){
                        currentRawIssueMappedIndex.put(currentRawIssue.getUuid(),-1);
                    }else{
                        currentRawIssueMappedIndex.put(currentRawIssue.getUuid(),0);
                    }

                }
            }
        }

        for (RawIssue currentRawIssue : currentRawIssues) {
            findBestMatching(preRawIssueMappedCurrentRawIssue,currentRawIssueMappedIndex,currentRawIssueMappedRawIssueList,currentRawIssue,-1);
        }

    }

    /**
     * 筛选出raw issue 所属的 issue 的最新commit 与 raw issue commit 相同的 raw issue
     * @return
     */
    private List<RawIssue> filterRawIssue(List<RawIssue> originalRawIssues,List<Issue> issues){
        List<RawIssue>  rawIssueList = new ArrayList<>();
        if(issues.isEmpty()){
            for(RawIssue rawIssue : originalRawIssues){
                Issue issue = issueDao.getIssueByID(rawIssue.getIssue_id());
                issues.add(issue);
            }
            return originalRawIssues;
        }else{
            for(RawIssue rawIssue : originalRawIssues){
                boolean isMapped = false;
                for(Issue issue : issues ){
                    if(rawIssue.getIssue_id().equals(issue.getUuid())){
                        isMapped = true;
                        break;
                    }
                }
                if(!isMapped){
                    rawIssueList.add(rawIssue);
                    Issue issue = issueDao.getIssueByID(rawIssue.getIssue_id());
                    issues.add(issue);
                }
            }
        }

        return rawIssueList;
    }


    private String[] getParentCommits(String repoId,String commitId){
        String repoPath = null;
        JSONObject repoPathJson = null;
        JGitHelper jGitHelper = null;
        try{
            repoPathJson = restInterfaceManager.getRepoPath(repoId,commitId);
            if(repoPathJson == null){
                throw new RuntimeException("can not get repo path");
            }
            repoPath = repoPathJson.getJSONObject("data").getString("content");
            if(repoPath != null){
                jGitHelper = new JGitHelper(repoPath);
                String[] verifyParentCommitsIsMerge = jGitHelper.getCommitParents(commitId);
                return verifyParentCommitsIsMerge;
            }


        }finally{
            if(repoPath!= null){
                restInterfaceManager.freeRepoPath(repoId,repoPath);
            }
        }
        return null;
    }

    private boolean verifyWhetherAggregation(String repoId,String commitId) throws RuntimeException{
        boolean result = false;

        JSONObject jsonObject = restInterfaceManager.getCommitsOfRepoByConditions(repoId, 1, 1, null);
        if(jsonObject == null){
            throw  new RuntimeException("can't  get commit");
        }
        JSONArray scanMessageWithTimeJsonArray = jsonObject.getJSONArray("data");
        JSONObject latestScanMessageWithTime = scanMessageWithTimeJsonArray.getJSONObject(0);
        String completeCommitTime = latestScanMessageWithTime.getString("commit_time");
        String checkCommitId = latestScanMessageWithTime.getString("commit_id");


        String repoPath = null;
        JSONObject repoPathJson = null;
        JGitHelper jGitHelper = null;
        try{
            repoPathJson = restInterfaceManager.getRepoPath(repoId,checkCommitId);
            if(repoPathJson == null){
                throw new RuntimeException("can not get repo path");
            }
            repoPath = repoPathJson.getJSONObject("data").getString("content");
            if(repoPath != null){
                jGitHelper = new JGitHelper(repoPath);
                result = jGitHelper.verifyWhetherAggregationCommit(commitId);
            }


        }finally{
            if(repoPath!= null){
                restInterfaceManager.freeRepoPath(repoId,repoPath);
            }
        }
        return result;
    }

    /**
     * 获取不是merge点的编译失败的commit
     * @param repoId
     * @param commit
     * @param category
     * @return
     */
    private String getCompileFailedParentCommit(String repoId,String commit,String category){
        String[] parentCommits = getParentCommits(repoId,commit);
        if(parentCommits.length < 2){
            return commit;
        }else{
            String latestFailedCommit = restInterfaceManager.getLatestScanFailedCommitId(repoId,commit,category);
            if(latestFailedCommit == null ){
                return null;
            }
            return getCompileFailedParentCommit(repoId,latestFailedCommit,category);
        }


    }


    private void addIssueTypeTag(List<JSONObject> tags,RawIssue rawIssue,Issue issue){
        String tagId = null;
        IssueType issueType = issueTypeDao.getIssueTypeByTypeName(rawIssue.getType());
        JSONArray tagsJson = restInterfaceManager.getTagByCondition(null,issueType.getCategory(),null);
        if(tagsJson.size()==1){
            tagId = tagsJson.getJSONObject(0).getString("uuid");
        }
        JSONObject issueTypeTagged = new JSONObject();
        issueTypeTagged.put("item_id", issue.getUuid());
        issueTypeTagged.put("tag_id", tagId);
        tags.add(issueTypeTagged);
    }

    private RawIssue generateSolvedStatusRawIssue(RawIssue preRawIssue,String currentCommitId,
                                                  Date currentCommitDate){
        RawIssue solvedStatusRawIssue = new RawIssue();
        String newRawIssueId = UUID.randomUUID().toString();
        solvedStatusRawIssue.setUuid(newRawIssueId);
        solvedStatusRawIssue.setType(preRawIssue.getType());
        solvedStatusRawIssue.setCategory(preRawIssue.getCategory());
        solvedStatusRawIssue.setDetail(preRawIssue.getDetail());
        solvedStatusRawIssue.setFile_name(preRawIssue.getFile_name());

        JSONObject scan = restInterfaceManager.getScanByCategoryAndRepoIdAndCommitId(preRawIssue.getRepo_id(),currentCommitId,preRawIssue.getCategory());
        if(scan == null){
            logger.error("can't get scan throw inner interface, repo id --> {} ,commit id -->" ,preRawIssue.getRepo_id(),currentCommitId);
        }
        String scanId = scan.getString("uuid");
        solvedStatusRawIssue.setScan_id(scanId);
        //此处可能会有影响issue对raw issue 的筛选！！
        solvedStatusRawIssue.setIssue_id(preRawIssue.getIssue_id());
        solvedStatusRawIssue.setCommit_id(currentCommitId);
        solvedStatusRawIssue.setRepo_id(preRawIssue.getRepo_id());
        solvedStatusRawIssue.setCode_lines(preRawIssue.getCode_lines());
        solvedStatusRawIssue.setStatus(RawIssueStatus.SOLVED.getType());
        //此处应该在raw issue 类中修改，针对存储location ，存储的是list的clone
        solvedStatusRawIssue.setLocations(preRawIssue.getLocations());
        solvedStatusRawIssue.setCommit_time(currentCommitDate);
        solvedStatusRawIssue.setIssue(preRawIssue.getIssue());

        return solvedStatusRawIssue;
    }




}







class RawIssueMappingSort {
    private RawIssue rawIssue;
    private double mappingSort;

    public RawIssue getRawIssue() {
        return rawIssue;
    }

    public void setRawIssue(RawIssue rawIssue) {
        this.rawIssue = rawIssue;
    }

    public double getMappingSort() {
        return mappingSort;
    }

    public void setMappingSort(double mappingSort) {
        this.mappingSort = mappingSort;
    }
}