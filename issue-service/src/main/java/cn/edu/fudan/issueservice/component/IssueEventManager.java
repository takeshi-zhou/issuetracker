package cn.edu.fudan.issueservice.component;

import cn.edu.fudan.issueservice.dao.IssueDao;
import cn.edu.fudan.issueservice.domain.EventType;
import cn.edu.fudan.issueservice.domain.Issue;
import cn.edu.fudan.issueservice.domain.RawIssue;
import cn.edu.fudan.issueservice.util.DateTimeUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.*;

/**
 * @author WZY
 * @version 1.0
 **/
@Component
public class IssueEventManager {

    @Value("${event.service.path}")
    private String eventServicePath;

    private RestTemplate restTemplate;
    private IssueDao issueDao;

    public IssueEventManager(RestTemplate restTemplate,
                             IssueDao issueDao) {
        this.restTemplate = restTemplate;
        this.issueDao = issueDao;
    }

    public void sendIssueEvent(EventType eventType, List<Issue> issues, String committer, String repoId, Date currentCommitTime){
        JSONArray issueEvents=new JSONArray();
        String commitTime= DateTimeUtil.format(currentCommitTime);
        for(Issue issue:issues){
            JSONObject event=new JSONObject();
            event.put("id", UUID.randomUUID().toString());
            event.put("category",issue.getCategory());
            event.put("eventType",eventType.toString());
            event.put("targetType",issue.getType());
            event.put("targetId",issue.getUuid());
            event.put("targetDisplayId",issue.getDisplayId());
            event.put("targetCommitter",committer);
            event.put("repoId",repoId);
            event.put("commitTime",commitTime);
            issueEvents.add(event);
        }
        if(!issueEvents.isEmpty())
             restTemplate.postForObject(eventServicePath,issueEvents,JSONObject.class);
    }

    public void sendRawIssueEvent(EventType eventType,List<RawIssue> rawIssues,String committer,String repoId,Date currentCommitTime){
        JSONArray issueEvents=new JSONArray();
        String commitTime= DateTimeUtil.format(currentCommitTime);
        for(RawIssue rawIssue:rawIssues){
            JSONObject event=new JSONObject();
            event.put("id", UUID.randomUUID());
            event.put("category",rawIssue.getCategory());
            event.put("eventType",eventType.toString());
            event.put("targetType",rawIssue.getType());
            Issue issue=issueDao.getIssueByID(rawIssue.getIssue_id());
            event.put("targetId",issue.getUuid());
            event.put("targetDisplayId",issue.getDisplayId());
            event.put("targetCommitter",committer);
            event.put("repoId",repoId);
            event.put("commitTime",commitTime);
            issueEvents.add(event);
        }
        if(!issueEvents.isEmpty())
            restTemplate.postForObject(eventServicePath,issueEvents,JSONObject.class);
    }
}
