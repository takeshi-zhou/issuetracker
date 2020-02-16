package cn.edu.fudan.issueservice.service.impl;

import cn.edu.fudan.issueservice.domain.*;
import cn.edu.fudan.issueservice.util.LocationCompare;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author WZY
 * @version 1.0
 **/
@Slf4j
@Service("bugMapping")
public class BugMappingServiceImpl extends BaseMappingServiceImpl {
    private Logger logger = LoggerFactory.getLogger(BaseMappingServiceImpl.class);

    @Override
    public void mapping(String repoId, String preCommitId, String currentCommitId, String category, String committer) {
        //存当前扫描后需要插入的新的issue
        List<Issue> insertIssueList = new ArrayList<>();
        List<JSONObject> tags = new ArrayList<>();
        //当前时间
        Date date = new Date();
        Date commitDate = getCommitDate(currentCommitId);
        String developer = getDeveloper(currentCommitId);
        //获取该项目ignore的issue类型
        JSONArray ignoreTypes = restInterfaceManager.getIgnoreTypesOfRepo(repoId);


        List<String> parentCommits =  restInterfaceManager.getPreScannedCommitByCurrentCommit(repoId,currentCommitId,category);


        if (parentCommits.isEmpty()) {
            //当前project第一次扫描，所有的rawIssue都是issue
            List<RawIssue> rawIssues = rawIssueDao.getRawIssueByCommitIDAndCategory(repoId, category, currentCommitId);
            if (rawIssues == null || rawIssues.isEmpty()) {
                return;
            }
            logger.info("first scan mapping!");
            for (RawIssue rawIssue : rawIssues) {
                Issue issue=generateOneNewIssue(repoId, rawIssue, category, currentCommitId, commitDate, date);
                insertIssueList.add(issue);
                addTag(tags, ignoreTypes, rawIssue,issue);
            }
            int newIssueCount = insertIssueList.size();
            int remainingIssueCount = insertIssueList.size();
            int eliminatedIssueCount = 0;
            logger.info("first mapping -> new:{},remaining:{},eliminated:{}", newIssueCount, remainingIssueCount, eliminatedIssueCount);
            dashboardUpdate(repoId, newIssueCount, remainingIssueCount, eliminatedIssueCount,category);
            logger.info("dashboard info updated!");
            rawIssueDao.batchUpdateIssueId(rawIssues);
            scanResultDao.addOneScanResult(new ScanResult(category,repoId,date,currentCommitId,commitDate,developer,0,eliminatedIssueCount,remainingIssueCount));
        } else {
            issueMapping(repoId, category, preCommitId, currentCommitId, commitDate,
                    date, insertIssueList, tags, ignoreTypes, committer, developer);
        }
        //新的issue
        if (!insertIssueList.isEmpty()) {
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
    }

    private void issueMapping(String repoId, String category, String preCommitId, String currentCommitId, Date commitDate,
                              Date date, List<Issue> insertIssueList, List<JSONObject> tags, JSONArray ignoreTypes, String committer, String developer) {
        //不是第一次扫描，需要和前一次的commit产生的issue进行mapping
        if(preCommitId == null || preCommitId.isEmpty()){
            preCommitId = rawIssueDao.getPreCommitIdByCurrentCommitId(repoId,category,currentCommitId);
        }
        List<RawIssue> preRawIssues = rawIssueDao.getRawIssueByCommitIDAndCategory(repoId, category, preCommitId);
        List<RawIssue> currentRawIssues = rawIssueDao.getRawIssueByCommitIDAndCategory(repoId, category, currentCommitId);
        if (currentRawIssues == null || currentRawIssues.isEmpty()) {
            logger.info("all issues were solved or raw issue insert error , commit id -->  {}", currentCommitId);
        }


        logger.info("not first mapping!");
        //mapping开始之前end commit是上一个commit的表示是上个commit存活的issue
        Set<String> existsIssueIds = issueDao.getIssuesByEndCommit(repoId, category, preCommitId).stream().map(Issue::getUuid).collect(Collectors.toSet());
        //装需要更新的
        List<Issue> issues = new ArrayList<>();
        List<String> mappedIssueIds = new ArrayList<>();
        int equalsCount = 0;
        int ignoreCountInNewIssues = 0;
        int mappedCount = 0;
        int eliminatedIssueCount = 0;

        // key filePath + detail; value rawIssues
        Map<String, List<RawIssue>> curRawIssueMap = classifyRawIssue(currentRawIssues);
        Map<String, List<RawIssue>> preRawIssueMap = classifyRawIssue(preRawIssues);

        Map<String,List<RawIssueMappingSort>>  currentRawIssueMappedRawIssueList= new HashMap<>();
        //用来判断匹配上的preRawIssue的下标位置，-1表示未匹配上。
        Map<String,Integer> currentRawIssueMappedIndex = new HashMap<>();
        //key 是preRawIssue的uuid,value是current raw issue的uuid,用来记录preRawIssue匹配了哪个currentRawIssue
        Map<String,RawIssue> preRawIssueMappedCurrentRawIssue = new HashMap<>();
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


        for (RawIssue currentRawIssue : currentRawIssues) {
            int mappedPreRawIssueIndex = currentRawIssueMappedIndex.get(currentRawIssue.getUuid());
            if( mappedPreRawIssueIndex == -1){
                Issue issue = generateOneNewIssue(repoId,currentRawIssue,category,currentCommitId,commitDate,date);
                insertIssueList.add(issue);
                addTag(tags, ignoreTypes, currentRawIssue,issue);
            }else{
                RawIssue preRawIssue = currentRawIssueMappedRawIssueList.get(currentRawIssue.getUuid()).get(mappedPreRawIssueIndex).getRawIssue();
                String preIssueId = preRawIssue.getIssue_id();
                //只有和上个commit存活的issue匹配上才算真正匹配上
                if(existsIssueIds.contains(preIssueId)){
                    equalsCount++;
                    currentRawIssue.setIssue_id(preIssueId);
                    Issue issue = issueDao.getIssueByID(preIssueId);
                    issue.setEnd_commit(currentCommitId);
                    issue.setEnd_commit_date(commitDate);
                    issue.setRaw_issue_end(currentRawIssue.getUuid());
                    issue.setUpdate_time(new Date());
                    issues.add(issue);
                    mappedIssueIds.add(preIssueId);
                    continue;
                }
            }
        }


        for(RawIssue preRawIssue : preRawIssues){
            if(preRawIssue.isMapped()){
                mappedCount++;
            }else{
                eliminatedIssueCount++;
            }
        }




        //存储上个commit没匹配上的，也就是被solved的rawIssue的信息
        List<RawIssue> list = preRawIssues.stream().filter(rawIssue -> !rawIssue.isMapped()).collect(Collectors.toList());
        saveSolvedInfo(list,repoId,preCommitId,currentCommitId);
        if (!issues.isEmpty()) {
            //更新issue
            issueDao.batchUpdateIssue(issues);
            logger.info("issue update success!");
        }
        //在匹配的issue中上次commit被ignore的个数
        //原先的计算方式，存在问题
//        int ignoredCountInMappedIssues=mappedIssueIds.isEmpty()?0:issueDao.getIgnoredCountInMappedIssues(ignoreTagId,mappedIssueIds);
//        int eliminatedIssueCount = preRawIssues.size() - equalsCount+ignoreCountInNewIssues+ignoredCountInMappedIssues;
//        int remainingIssueCount = currentRawIssues.size()-ignoreCountInNewIssues-ignoredCountInMappedIssues;
//        int newIssueCount = currentRawIssues.size() - equalsCount-ignoreCountInNewIssues;


        if(eliminatedIssueCount !=  preRawIssues.size() - (currentRawIssues.size()  - insertIssueList.size())){
            logger.error(" eliminatedIssueCount calculate error, mapping failed");
        }

        if(equalsCount !=  mappedCount){
            logger.error(" mappedCount calculate error, mapping failed");
        }
        int remainingIssueCount = currentRawIssues.size();
        int newIssueCount = insertIssueList.size();
        logger.info("finish mapping -> new:{},remaining:{},eliminated:{}",newIssueCount, remainingIssueCount, eliminatedIssueCount);
        dashboardUpdate(repoId, newIssueCount, remainingIssueCount, eliminatedIssueCount,category);
        logger.info("dashboard info updated!");
        rawIssueDao.batchUpdateIssueId(currentRawIssues);
        modifyToSolvedTag(repoId, category, preCommitId, EventType.ELIMINATE_BUG, committer, commitDate);
        scanResultDao.addOneScanResult(new ScanResult(category,repoId,date,currentCommitId,commitDate,developer,newIssueCount,eliminatedIssueCount,remainingIssueCount));
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


    /*
        废弃
     */
    @Deprecated
    private RawIssue findMostSimilarRawIssue(RawIssue currentRawIssue, List<RawIssue> preList) {

        RawIssue target = null;

        //获取currentRawIssue的location
        Location currentLocation = currentRawIssue.getLocations().isEmpty() ? null : currentRawIssue.getLocations().get(0);
        if(currentLocation == null){
            return null;
        }

        double maxCommonality = 0;
        //OverLapping，由于目前只有1条location，所以非0即1，暂不考虑使用。
        double maxOverLapping = 0;
        double maxLcs = 0;
        double commonality ;
        double overLapping ;
        double lcs ;
        boolean isFirstLevMapped = false;
        boolean isOverLappingHadAnalyzed = false;
        for (RawIssue rawIssue : preList) {

            LocationCompare.computeSimilarity(currentRawIssue, rawIssue);
            commonality = LocationCompare.getCommonality();
            overLapping = LocationCompare.getOverLapping();
            lcs = LocationCompare.getLcs();


            //判断是否是同一个文件，同一个类，同一个方法名，bug lines的数目相同------此时的mapping的优先级最高
            //目前commonality 不是0就是1，所以不存在错误，当存在多个location时，下面的逻辑需要修改。
            if(commonality > LocationCompare.getThresholdCommonality()){
                if(commonality > maxCommonality && lcs == 1.0){
                    if (!rawIssue.isMapped()) {
                        maxCommonality = commonality;
                        maxLcs = lcs;
                        isOverLappingHadAnalyzed = true;
                        target = rawIssue;
                        isFirstLevMapped = true;
                        continue;
                    }


                }else if (commonality == maxCommonality && lcs == 1.0){
                    if (!rawIssue.isMapped()) {
                        maxCommonality = commonality;
                        maxLcs = lcs;
                        isOverLappingHadAnalyzed = true;
                        isFirstLevMapped = true;
                        continue;
                    }
                }

                if(!isOverLappingHadAnalyzed && lcs > maxLcs && lcs > LocationCompare.getThresholdLcs()){
                    if (!rawIssue.isMapped()) {
                        maxCommonality = commonality;
                        maxLcs = lcs;
                        isFirstLevMapped = true;
                        target = rawIssue;
                        continue;
                    }

                }

            }else {
                Location preLocation = rawIssue.getLocations().isEmpty() ? null : rawIssue.getLocations().get(0);
                if(  !isFirstLevMapped && currentLocation.isInSameClass(preLocation)  && lcs > maxLcs && lcs > LocationCompare.getDiffMethodsThresholdLcs()){

                    if(!rawIssue.isMapped()){
                        maxLcs = lcs;
                        target = rawIssue;
                    }
                }
            }

        }
        return target;
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
        return issue;
    }

    /**
     * 更改中，比对两个raw issue列表
     * @param preRawIssues
     * @param currentRawIssues
     */

    private void mappingTwoRawIssueList(List<RawIssue> preRawIssues,List<RawIssue> currentRawIssues){
        Map<String, List<RawIssue>> curRawIssueMap = classifyRawIssue(currentRawIssues);
        Map<String, List<RawIssue>> preRawIssueMap = classifyRawIssue(preRawIssues);

        Map<String,List<RawIssueMappingSort>>  currentRawIssueMappedRawIssueList= new HashMap<>();
        //用来判断匹配上的preRawIssue的下标位置，-1表示未匹配上。key是currentRawIssue的uuid，value是相匹配的preRawIssue的下标。
        Map<String,Integer> currentRawIssueMappedIndex = new HashMap<>();
        //key 是preRawIssue的uuid,value是current raw issue的uuid,用来记录preRawIssue匹配了哪个currentRawIssue
        Map<String,RawIssue> preRawIssueMappedCurrentRawIssue = new HashMap<>();
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