package cn.edu.fudan.issueservice.service.impl;

import cn.edu.fudan.issueservice.component.IssueEventManager;
import cn.edu.fudan.issueservice.dao.IssueDao;
import cn.edu.fudan.issueservice.dao.RawIssueDao;
import cn.edu.fudan.issueservice.dao.ScanResultDao;
import cn.edu.fudan.issueservice.domain.EventType;
import cn.edu.fudan.issueservice.domain.Issue;
import cn.edu.fudan.issueservice.domain.ScanResult;
import cn.edu.fudan.issueservice.service.MappingService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author WZY
 * @version 1.0
 **/
@Service
public class BaseMappingServiceImpl implements MappingService {

    @Value("${solved.tag_id}")
    private String solvedTagId;
    @Value("${tag.service.path}")
    protected String tagServicePath;
    @Value("${commit.service.path}")
    private String commitServicePath;

    IssueEventManager issueEventManager;
    IssueDao issueDao;
    RawIssueDao rawIssueDao;
    ScanResultDao scanResultDao;
    private StringRedisTemplate stringRedisTemplate;
    protected RestTemplate restTemplate;

    @Autowired
    public void setScanResultDao(ScanResultDao scanResultDao) {
        this.scanResultDao = scanResultDao;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
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

    Date getCommitDate(String commitId){
        JSONObject response=restTemplate.getForObject(commitServicePath+"/commit-time?commit_id="+commitId,JSONObject.class);
        if(response!=null){
            return response.getJSONObject("data").getDate("commit_time");
        }
        return null;
    }

    void newIssueInfoUpdate(List<Issue> issueList,String category,String repo_id){
        String todayNewIssueKey="dashboard:"+category+":day:new:" + repo_id;
        String weekNewIssueKey="dashboard:"+category+":week:new" + repo_id;
        String monthNewIssueKey="dashboard:"+category+":month:new"+repo_id;
        stringRedisTemplate.setEnableTransactionSupport(true);
        stringRedisTemplate.multi();
        for(Issue issue:issueList){
            stringRedisTemplate.opsForList().rightPush(todayNewIssueKey,issue.getUuid());
            stringRedisTemplate.opsForList().rightPush(weekNewIssueKey,issue.getUuid());
            stringRedisTemplate.opsForList().rightPush(monthNewIssueKey,issue.getUuid());
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

    void modifyToSolvedTag(String repo_id, String pre_commit_id,EventType eventType,String committer) {
        List<Issue> issues=issueDao.getSolvedIssues(repo_id, pre_commit_id);
        if(issues == null){
            issues = new ArrayList<>();
            issueEventManager.sendIssueEvent(eventType,issues,committer,repo_id);
        }
        if (issues != null && !issues.isEmpty()) {
            List<JSONObject> taggeds = new ArrayList<>();
            for (Issue issue : issues) {
                JSONObject tagged = new JSONObject();
                tagged.put("item_id", issue.getUuid());
                tagged.put("tag_id", solvedTagId);
                taggeds.add(tagged);
            }
            restTemplate.postForObject(tagServicePath+"/tagged-modify", taggeds, JSONObject.class);
        }
    }

    void addSolvedTag(String repo_id, String pre_commit_id,EventType eventType,String committer) {
        List<Issue> issues=issueDao.getSolvedIssues(repo_id, pre_commit_id);
        if(issues == null){
            issues = new ArrayList<>();
            issueEventManager.sendIssueEvent(eventType,issues,committer,repo_id);
        }
        if (issues != null && !issues.isEmpty()) {
            List<JSONObject> taggeds = new ArrayList<>();
            for (Issue issue : issues) {
                JSONObject tagged = new JSONObject();
                tagged.put("item_id", issue.getUuid());
                tagged.put("tag_id", solvedTagId);
                taggeds.add(tagged);
            }
            restTemplate.postForObject(tagServicePath,taggeds, JSONObject.class);
        }
    }

    @Override
    public void mapping(String repo_id, String pre_commit_id, String current_commit_id, String category,String committer) {
        throw new UnsupportedOperationException();
    }
}
