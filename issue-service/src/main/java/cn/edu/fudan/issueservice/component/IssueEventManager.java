package cn.edu.fudan.issueservice.component;

import cn.edu.fudan.issueservice.domain.EventType;
import cn.edu.fudan.issueservice.domain.Issue;
import cn.edu.fudan.issueservice.util.DateTimeUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author WZY
 * @version 1.0
 **/
@Component
public class IssueEventManager {

    @Value("${event.service.path}")
    private String eventServicePath;

    private RestTemplate restTemplate;

    public IssueEventManager(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendIssueEvent(EventType eventType, List<Issue> newIssues, String category, String committer, String repoId){
        JSONArray newIssueEvents=new JSONArray();
        String now= DateTimeUtil.format(LocalDateTime.now());
        for(Issue issue:newIssues){
            JSONObject event=new JSONObject();
            event.put("id", UUID.randomUUID());
            switch (eventType){
                case NEW:
                    if(category.equals("bug"))
                        event.put("eventType","NEW_BUG");
                    else if (category.equals("clone"))
                        event.put("eventType","NEW_CLONE_INSTANCE");
                    break;
                case MODIFY:
                    if(category.equals("bug"))
                        event.put("eventType","MODIFY_BUG");
                    else if (category.equals("clone"))
                        event.put("eventType","MODIFY_CLONE_INSTANCE");
                    break;
                case ELIMINATE:
                    if(category.equals("bug"))
                        event.put("eventType","ELIMINATE_BUG");
                    else if (category.equals("clone"))
                        event.put("eventType","REMOVE_CLONE_INSTANCE");
                    break;
                    default:break;
            }

            event.put("targetType",issue.getType());
            event.put("targetId",issue.getUuid());
            event.put("targetCommitter",committer);
            event.put("repoId",repoId);
            event.put("createTime",now);
            newIssueEvents.add(event);
        }
        restTemplate.postForObject(eventServicePath,newIssueEvents,JSONObject.class);
    }
}
