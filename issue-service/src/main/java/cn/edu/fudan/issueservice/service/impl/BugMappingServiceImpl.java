package cn.edu.fudan.issueservice.service.impl;

import cn.edu.fudan.issueservice.domain.*;
import cn.edu.fudan.issueservice.util.LocationCompare;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
        if (preCommitId.equals(currentCommitId)) {
            //当前project第一次扫描，所有的rawIssue都是issue
            List<RawIssue> rawIssues = rawIssueDao.getRawIssueByCommitIDAndCategory(repoId,category,currentCommitId);
            if (rawIssues == null || rawIssues.isEmpty()) {
                return;
            }
            logger.info("first scan mapping!");
            for (RawIssue rawIssue : rawIssues) {
                Issue issue=generateOneNewIssue(repoId,rawIssue,category,currentCommitId,commitDate,date);
                insertIssueList.add(issue);
                addTag(tags,ignoreTypes,rawIssue,issue);
            }
            int newIssueCount = insertIssueList.size();
            int remainingIssueCount = insertIssueList.size();
            int eliminatedIssueCount = 0;
            logger.info("finish mapping -> new:{},remaining:{},eliminated:{}",newIssueCount,remainingIssueCount,eliminatedIssueCount);
            dashboardUpdate(repoId, newIssueCount, remainingIssueCount, eliminatedIssueCount,category);
            logger.info("dashboard info updated!");
            rawIssueDao.batchUpdateIssueId(rawIssues);
            scanResultDao.addOneScanResult(new ScanResult(category,repoId,date,currentCommitId,commitDate,developer,newIssueCount,eliminatedIssueCount,remainingIssueCount));
        } else {
            //不是第一次扫描，需要和前一次的commit产生的issue进行mapping
            List<RawIssue> preRawIssues = rawIssueDao.getRawIssueByCommitIDAndCategory(repoId, category, preCommitId);
            List<RawIssue> currentRawIssues = rawIssueDao.getRawIssueByCommitIDAndCategory(repoId, category, currentCommitId);
            if (currentRawIssues == null || currentRawIssues.isEmpty()) {
                return;
            }
            logger.info("not first mapping!");
            //mapping开始之前end commit是上一个commit的表示是上个commit存活的issue
            Set<String> existsIssueIds = issueDao.getIssuesByEndCommit(repoId, category, preCommitId).stream().map(Issue::getUuid).collect(Collectors.toSet());
            //装需要更新的
            List<Issue> issues = new ArrayList<>();
            List<String> mappedIssueIds = new ArrayList<>();
            int equalsCount = 0;
            int ignoreCountInNewIssues=0;
            for (RawIssue currentRawIssue : currentRawIssues) {
                boolean mapped = false;
                boolean mappedButSolved = false;
                for (RawIssue preRawIssue : preRawIssues) {
                    //如果已经匹配到一个,内部循环不再继续
                    if (!preRawIssue.isMapped() && !currentRawIssue.isMapped() && LocationCompare.isSameIssue(preRawIssue, currentRawIssue)) {
                        preRawIssue.setMapped(true);
                        currentRawIssue.setMapped(true);
                        mapped = true;
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
                        }else{
                            //匹配到上一个commit的一个rawIssue，但是这个rawIssue对应的issue已经被solved
                            mappedButSolved=true;
                        }
                        break;
                    }
                }
                //如果当前commit的某个rawIssue没有在上个commit的rawIssue列表里面找到匹配，将它作为新的issue插入
                //如果匹配上，但是匹配到的是已经solved的，此时也应该作为新的issue插入
                if (!mapped || mappedButSolved) {
                    Issue issue=generateOneNewIssue(repoId,currentRawIssue,category,currentCommitId,commitDate,date);
                    insertIssueList.add(issue);
                    ignoreCountInNewIssues+=addTag(tags,ignoreTypes,currentRawIssue,issue);
                }
            }
            //存储上个commit没匹配上的，也就是被solved的rawIssue的信息
            List<RawIssue> list=preRawIssues.stream().filter(rawIssue -> !rawIssue.isMapped()).collect(Collectors.toList());
            saveSolvedInfo(list,repoId,preCommitId,currentCommitId);
            if (!issues.isEmpty()) {
                //更新issue
                issueDao.batchUpdateIssue(issues);
                logger.info("issue update success!");
            }
            //在匹配的issue中上次commit被ignore的个数
            int ignoredCountInMappedIssues=mappedIssueIds.isEmpty()?0:issueDao.getIgnoredCountInMappedIssues(ignoreTagId,mappedIssueIds);
            int eliminatedIssueCount = preRawIssues.size() - equalsCount+ignoreCountInNewIssues+ignoredCountInMappedIssues;
            int remainingIssueCount = currentRawIssues.size()-ignoreCountInNewIssues-ignoredCountInMappedIssues;
            int newIssueCount = currentRawIssues.size() - equalsCount-ignoreCountInNewIssues;
            logger.info("finish mapping -> new:{},remaining:{},eliminated:{}",newIssueCount,remainingIssueCount,eliminatedIssueCount);
            dashboardUpdate(repoId, newIssueCount, remainingIssueCount, eliminatedIssueCount,category);
            logger.info("dashboard info updated!");
            rawIssueDao.batchUpdateIssueId(currentRawIssues);
            modifyToSolvedTag(repoId, category,preCommitId,EventType.ELIMINATE_BUG,committer,commitDate);
            scanResultDao.addOneScanResult(new ScanResult(category,repoId,date,currentCommitId,commitDate,developer,newIssueCount,eliminatedIssueCount,remainingIssueCount));
        }
        //新的issue
        if (!insertIssueList.isEmpty()) {
            issueDao.insertIssueList(insertIssueList);
            issueEventManager.sendIssueEvent(EventType.NEW_BUG,insertIssueList,committer,repoId,commitDate);
            newIssueInfoUpdate(insertIssueList,category,repoId);
            logger.info("new issue insert success!");
        }
        //打tag
        if(!tags.isEmpty()){
            restInterfaceManager.addTags(tags);
        }
        logger.info("mapping finished!");
    }

    //根据rawIssue产生一个新的Issue对象
    private Issue generateOneNewIssue(String repo_id,RawIssue rawIssue,String category,String current_commit_id,Date currentCommitDate,Date addTime){
        String new_IssueId = UUID.randomUUID().toString();
        rawIssue.setIssue_id(new_IssueId);
        String targetFiles = rawIssue.getFile_name();
        boolean hasDisplayId=issueDao.getMaxIssueDisplayId(repo_id) != null;
        if (isDefaultDisplayId){
             currentDisplayId = hasDisplayId ? issueDao.getMaxIssueDisplayId(repo_id) : 0;
            isDefaultDisplayId = false;
        }
        // 映射 （1-4）1 、（5-9）2、（10-14）3 、（15 -20） 4
        int priority =  Integer.parseInt(JSONObject.parseObject(rawIssue.getDetail(),RawIssueDetail.class).getRank())/5 + 1 ;
        priority = priority == 5 ? 4 : priority ;
        Issue issue = new Issue(new_IssueId, rawIssue.getType(),category, current_commit_id,
                currentCommitDate, current_commit_id,currentCommitDate, rawIssue.getUuid(),
                rawIssue.getUuid(), repo_id, targetFiles,addTime,addTime,++currentDisplayId);
        issue.setPriority(priority);
        return issue;
    }
}
