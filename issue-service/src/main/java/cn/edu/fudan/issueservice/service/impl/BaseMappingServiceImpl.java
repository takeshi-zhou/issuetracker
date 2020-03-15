package cn.edu.fudan.issueservice.service.impl;

import cn.edu.fudan.issueservice.component.IssueEventManager;
import cn.edu.fudan.issueservice.component.RestInterfaceManager;
import cn.edu.fudan.issueservice.component.TagMapHelper;
import cn.edu.fudan.issueservice.dao.IssueDao;
import cn.edu.fudan.issueservice.dao.RawIssueDao;
import cn.edu.fudan.issueservice.dao.ScanResultDao;
import cn.edu.fudan.issueservice.domain.*;
import cn.edu.fudan.issueservice.service.MappingService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static cn.edu.fudan.issueservice.domain.StatusEnum.IGNORE;

/**
 * @author WZY
 * @version 1.0
 **/
@Slf4j
@Service
public class BaseMappingServiceImpl implements MappingService {
    private Logger logger = LoggerFactory.getLogger(BaseMappingServiceImpl.class);

    @Value("${solved.tag_id}")
    private String solvedTagId;
    @Value("${open.tag_id}")
    private String openTagId;
    @Value("${to_review.tag_id}")
    private String toReviewTagId;
    @Value("${ignore.tag_id}")
    String ignoreTagId;
    @Value("${misinformation.tag_id}")
    String misinformationTagId;

    int currentDisplayId = 1;
    volatile boolean  isDefaultDisplayId = true;
    IssueEventManager issueEventManager;
    IssueDao issueDao;
    RawIssueDao rawIssueDao;
    ScanResultDao scanResultDao;
    RestInterfaceManager restInterfaceManager;

    private StringRedisTemplate stringRedisTemplate;
    private TagMapHelper tagMapHelper;
    private KafkaTemplate kafkaTemplate;

    @Autowired
    public void setKafkaTemplate(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Autowired
    public void setTagMapHelper(TagMapHelper tagMapHelper) {
        this.tagMapHelper = tagMapHelper;
    }

    @Autowired
    public void setScanResultDao(ScanResultDao scanResultDao) {
        this.scanResultDao = scanResultDao;
    }

    @Autowired
    public void setRestInterfaceManager(RestInterfaceManager restInterfaceManager) {
        this.restInterfaceManager = restInterfaceManager;
    }

    @Autowired
    public void setIssueEventManager(IssueEventManager issueEventManager) {
        this.issueEventManager = issueEventManager;
    }

    @Autowired
    public void setIssueDao(IssueDao issueDao) {
        this.issueDao = issueDao;
    }

    @Autowired
    public void setRawIssueDao(RawIssueDao rawIssueDao) {
        this.rawIssueDao = rawIssueDao;
    }

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    void newIssueInfoUpdate(List<Issue> issueList,String category,String repo_id){
        String todayNewIssueKey="dashboard:"+category+":day:new:" + repo_id;
        String weekNewIssueKey="dashboard:"+category+":week:new:" + repo_id;
        String monthNewIssueKey="dashboard:"+category+":month:new:"+repo_id;
        stringRedisTemplate.setEnableTransactionSupport(true);
        stringRedisTemplate.multi();
        for(Issue issue:issueList){
            stringRedisTemplate.opsForList().rightPush(todayNewIssueKey,issue.getUuid());
            stringRedisTemplate.opsForList().rightPush(weekNewIssueKey,issue.getUuid());
            stringRedisTemplate.opsForList().rightPush(monthNewIssueKey,issue.getUuid());
        }
        stringRedisTemplate.exec();
    }

   private void eliminatedInfoUpdate(List<Issue> issueList,String category,String repo_id){
        String todayEliminatedIssueKey="dashboard:"+category+":day:eliminated:" + repo_id;
        String weekEliminatedIssueKey="dashboard:"+category+":week:eliminated:" + repo_id;
        String monthEliminatedIssueKey="dashboard:"+category+":month:eliminated:"+repo_id;
        stringRedisTemplate.setEnableTransactionSupport(true);
        stringRedisTemplate.multi();
        for(Issue issue:issueList){
            stringRedisTemplate.opsForList().rightPush(todayEliminatedIssueKey,issue.getUuid());
            stringRedisTemplate.opsForList().rightPush(weekEliminatedIssueKey,issue.getUuid());
            stringRedisTemplate.opsForList().rightPush(monthEliminatedIssueKey,issue.getUuid());
        }
        stringRedisTemplate.exec();
    }


    void dashboardUpdate(String repo_id, int newIssueCount, int remainingIssueCount, int eliminatedIssueCount, String category) {
        //注意只有remaining是覆盖的，其余是累增的
        String todayKey = "dashboard:"+category+":day:" + repo_id;
        String weekKey = "dashboard:"+category+":week:" + repo_id;
        String monthKey = "dashboard:"+category+":month:" + repo_id;
        stringRedisTemplate.setEnableTransactionSupport(true);
        stringRedisTemplate.multi();
        stringRedisTemplate.opsForHash().increment(todayKey, "new", newIssueCount);
        stringRedisTemplate.opsForHash().put(todayKey, "remaining", String.valueOf(remainingIssueCount));
        stringRedisTemplate.opsForHash().increment(todayKey, "eliminated", eliminatedIssueCount);
        stringRedisTemplate.opsForHash().increment(weekKey, "new", newIssueCount);
        stringRedisTemplate.opsForHash().put(weekKey, "remaining", String.valueOf(remainingIssueCount));
        stringRedisTemplate.opsForHash().increment(weekKey, "eliminated", eliminatedIssueCount);
        stringRedisTemplate.opsForHash().increment(monthKey, "new", newIssueCount);
        stringRedisTemplate.opsForHash().put(monthKey, "remaining", String.valueOf(remainingIssueCount));
        stringRedisTemplate.opsForHash().increment(monthKey, "eliminated", eliminatedIssueCount);
        stringRedisTemplate.exec();
    }

    void dashboardUpdateForMergeVersion(String repo_id, int newIssueCount, int remainingIssueCountChanged, int eliminatedIssueCount, String category) {
        //注意只有remaining是覆盖的，其余是累增的
        String todayKey = "dashboard:"+category+":day:" + repo_id;
        String weekKey = "dashboard:"+category+":week:" + repo_id;
        String monthKey = "dashboard:"+category+":month:" + repo_id;
        stringRedisTemplate.setEnableTransactionSupport(true);
        stringRedisTemplate.multi();

        stringRedisTemplate.opsForHash().increment(todayKey, "new", newIssueCount);
        stringRedisTemplate.opsForHash().increment(todayKey, "remaining", remainingIssueCountChanged);
//        Object todayRemainingObject=stringRedisTemplate.opsForHash().get(todayKey,"remaining");
//        int todayRemaining = todayRemainingObject==null?0:Integer.parseInt((String)todayRemainingObject);
//        stringRedisTemplate.opsForHash().put(todayKey, "remaining", String.valueOf(todayRemaining+remainingIssueCountChanged));
        stringRedisTemplate.opsForHash().increment(todayKey, "eliminated", eliminatedIssueCount);


        stringRedisTemplate.opsForHash().increment(weekKey, "new", newIssueCount);
        stringRedisTemplate.opsForHash().increment(weekKey, "remaining", remainingIssueCountChanged);
//        Object weekRemainingObject=stringRedisTemplate.opsForHash().get(todayKey,"remaining");
//        int weekRemaining = weekRemainingObject==null?0:Integer.parseInt((String)weekRemainingObject);
//        stringRedisTemplate.opsForHash().put(todayKey, "remaining", String.valueOf(weekRemaining+remainingIssueCountChanged));
        stringRedisTemplate.opsForHash().increment(weekKey, "eliminated", eliminatedIssueCount);

        stringRedisTemplate.opsForHash().increment(monthKey, "new", newIssueCount);
        stringRedisTemplate.opsForHash().increment(monthKey, "remaining", remainingIssueCountChanged);
//        Object monthRemainingObject=stringRedisTemplate.opsForHash().get(todayKey,"remaining");
//        int monthRemaining = monthRemainingObject==null?0:Integer.parseInt((String)monthRemainingObject);
//        stringRedisTemplate.opsForHash().put(monthKey, "remaining", String.valueOf(monthRemaining+remainingIssueCountChanged));
        stringRedisTemplate.opsForHash().increment(monthKey, "eliminated", eliminatedIssueCount);
        stringRedisTemplate.exec();
    }

    Date getCommitDate(String commitId){
        JSONObject response=restInterfaceManager.getCommitTime(commitId);
        if(response!=null && response.getJSONObject("data") != null){
            return response.getJSONObject("data").getDate("commit_time");
        }
        return null;
    }

    String getDeveloper(String commitId){
        JSONObject response=restInterfaceManager.getOneCommitByCommitId(commitId);
        if(response!=null){
            return response.getJSONObject("data").getString("developer");
        }
        return null;
    }

    @Override
    public void mapping(String repo_id, String pre_commit_id, String current_commit_id, String category,String committer) {
        throw new UnsupportedOperationException();
    }

    int addTag(List<JSONObject> tags, JSONArray ignoreTypes, RawIssue rawIssue, Issue issue){
        int result=0;
        String priorityTagID = null;
        if(ignoreTypes!=null&&!ignoreTypes.isEmpty()&&ignoreTypes.contains(rawIssue.getType())){
            //如果新增的issue的类型包含在ignore的类型之中，打ignore的tag
            priorityTagID=ignoreTagId;
            issue.setPriority(5);
            result=1;
        }else if (rawIssue.getCategory().equals(Scanner.FINDBUGS.getType())){
            RawIssueDetail rawIssueDetail= JSONObject.parseObject(rawIssue.getDetail(),RawIssueDetail.class);
            priorityTagID=tagMapHelper.getTagIdByRank(Integer.parseInt(rawIssueDetail.getRank()));
        }else if(rawIssue.getCategory().equals(Scanner.SONAR.getType())){
            priorityTagID = tagMapHelper.getTagIdByPriority(issue.getPriority());
        }
        if(priorityTagID!=null){
            JSONObject priorityTagged = new JSONObject();
            priorityTagged.put("item_id", issue.getUuid());
            priorityTagged.put("tag_id", priorityTagID);
            tags.add(priorityTagged);

            JSONObject statusTagged = new JSONObject();
            statusTagged.put("item_id", issue.getUuid());
            String statusTagId = null;
            statusTagId = getTagIdByStatus(issue.getStatus());
            statusTagged.put("tag_id", statusTagId);
            tags.add(statusTagged);
        }
        return result;
    }

    void modifyToSolvedTag(String repo_id,String category, String pre_commit_id,EventType eventType,String committer,Date currentCommitTime) {
        //mapping完后end commit还是上一个commit说明被solved
        List<Issue> issues=issueDao.getIssuesByEndCommit(repo_id,category,pre_commit_id);
        if(issues != null) {
            issueEventManager.sendIssueEvent(eventType, issues, committer, repo_id, currentCommitTime);
            if (!issues.isEmpty()) {
                eliminatedInfoUpdate(issues, category, repo_id);
                List<JSONObject> taggeds = new ArrayList<>();
                for (Issue issue : issues) {
                    JSONObject tagged = new JSONObject();
                    tagged.put("item_id", issue.getUuid());
                    tagged.put("tag_id", solvedTagId);
                    taggeds.add(tagged);
                    issueDao.updateOneIssuePriority(issue.getUuid(),6);
                }
                restInterfaceManager.modifyTags(taggeds);
            }
        }
    }


    void modifyToSolvedTag(List<Issue> issues,boolean isSendToEvent,boolean isUpdateIssueIdsToDashboard,
                           EventType eventType,String committer,Date currentCommitTime,String repo_id,String category) {
        if(issues != null) {
            //暂不发送event消息
            if(isSendToEvent){
                issueEventManager.sendIssueEvent(eventType, issues, committer, repo_id, currentCommitTime);
            }
            if (!issues.isEmpty()) {
                //暂不更新消除缺陷的id到dashboard
                if(isUpdateIssueIdsToDashboard){
                    eliminatedInfoUpdate(issues, category, repo_id);
                }

                List<JSONObject> taggeds = new ArrayList<>();
                for (Issue issue : issues) {
                    String preTagId = null;
                    preTagId = getTagIdByStatus(issue.getStatus());
                    JSONObject tagged = new JSONObject();
                    tagged.put("itemId", issue.getUuid());
                    tagged.put("preTagId", preTagId);
                    tagged.put("newTagId", solvedTagId);
                    taggeds.add(tagged);
                }
                restInterfaceManager.modifyTags(taggeds);
            }
        }
    }


    /**
     * 此更新tag必须在更新完issue状态后进行
     * @param rawIssues
     */
    void modifyToOpenTagByRawIssues(List<RawIssue> rawIssues) {
        List<JSONObject> taggeds = new ArrayList<>();
        for(RawIssue rawIssue : rawIssues){
            String tagId = null;
            String issueId = rawIssue.getIssue_id();
            Issue issue = issueDao.getIssueByID(issueId);

            JSONObject tagged = new JSONObject();
            tagged.put("itemId", issue.getUuid());
            tagged.put("preTagId", solvedTagId);
            tagId = getTagIdByStatus(issue.getStatus());
            tagged.put("newTagId", tagId);

            taggeds.add(tagged);
        }
        restInterfaceManager.modifyTags(taggeds);

    }


    void modifyToOpenTagByIssues(List<Issue> issues) {
        List<JSONObject> taggeds = new ArrayList<>();
        for(Issue issue : issues){
            String tagId = null;
            JSONObject tagged = new JSONObject();
            tagged.put("itemId", issue.getUuid());
            tagged.put("preTagId", solvedTagId);
            tagId = getTagIdByStatus(issue.getStatus());
            tagged.put("newTagId", tagId);
            taggeds.add(tagged);
        }
        restInterfaceManager.modifyTags(taggeds);

    }




    @SuppressWarnings("unchecked")
    void saveSolvedInfo(List<RawIssue> rawIssues,String repo_id,String pre_commit_id,String current_commit_id){
        List<JSONObject> solvedInfos=new ArrayList<>();
        for(RawIssue rawIssue:rawIssues){
            JSONObject solvedInfo=new JSONObject();
            solvedInfo.put("type",rawIssue.getType());
            solvedInfo.put("location",rawIssue.firstLocation().getFile_path());
            solvedInfo.put("bug_lines",rawIssue.firstLocation().getBug_lines());
            solvedInfo.put("class_name",rawIssue.firstLocation().getClass_name());
            solvedInfo.put("method_name",rawIssue.firstLocation().getMethod_name());
            solvedInfo.put("start_line",rawIssue.firstLocation().getStart_line());
            solvedInfo.put("end_line",rawIssue.firstLocation().getEnd_line());
            solvedInfo.put("curr_commitid",pre_commit_id);
            solvedInfo.put("next_commitid",current_commit_id);
            solvedInfo.put("repoid",repo_id);
            solvedInfos.add(solvedInfo);
        }
        logger.info("solvedBug -> {}",JSONArray.toJSONString(solvedInfos));
        if(!solvedInfos.isEmpty()){
            kafkaTemplate.send("solvedBug",JSONArray.toJSONString(solvedInfos));
        }
    }


    boolean judgeStatusIsClosedButNotSolved(String status){
        boolean result = false;
        switch (StatusEnum.getStatusByName(status)){
            case IGNORE:
                result = true;
                break;
            case OPEN:
                result = false;
                break;
            case SOLVED:
                result = false;
                break;
            case TO_REVIEW:
                result = false;
                break;
            case MISINFORMATION:
                result = true;
                break;
            default:
                logger.warn("status --> {} is not recorded", status);
                result = false;

        }
        return result;
    }

    private String getTagIdByStatus(String status){
        String tagId = null;
        switch (StatusEnum.getStatusByName(status)){
            case IGNORE :
                tagId = ignoreTagId;
                break;
            case MISINFORMATION:
                tagId = misinformationTagId;
                break;
            case TO_REVIEW:
                tagId = toReviewTagId;
                break;
            case SOLVED:
                tagId = solvedTagId;
                break;
            case OPEN:
                tagId = openTagId;
                break;
            default:
                tagId = null;
        }
        return tagId;
    }

}
