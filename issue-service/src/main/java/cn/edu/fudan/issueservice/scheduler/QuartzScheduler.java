package cn.edu.fudan.issueservice.scheduler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class QuartzScheduler {

    @Value("${inner.service.path}")
    private String innerServicePath;
    private RestTemplate restTemplate;

    private HttpHeaders headers;

    @Autowired
    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @SuppressWarnings("unchecked")
    private List<String> getAccountIds() {
        HttpEntity<String> requestEntity=new HttpEntity<>(headers);
        return restTemplate.exchange(innerServicePath + "/user/accountIds", HttpMethod.GET, requestEntity, List.class).getBody();
    }

    private JSONArray getProjectList(String account_id) {
        HttpEntity<String> requestEntity=new HttpEntity<>(headers);
        return restTemplate.exchange(innerServicePath + "/inner/projects?account_id=" + account_id, HttpMethod.GET, requestEntity, JSONArray.class).getBody();
    }

    private void updateTrend(String duration, String account_id, String repo_id, String category,int newIssueCount, int remainingIssueCount, int eliminatedIssueCount) {
        String newKey = "";
        String remainingKey = "";
        String eliminatedKey = "";
        if (repo_id == null) {
            newKey = "trend:"+category+":"+ duration + ":new:" + account_id;
            remainingKey = "trend:"+category+":"+ duration + ":remaining:" + account_id;
            eliminatedKey = "trend:"+category+":"+ duration + ":eliminated:" + account_id;
        } else {
            newKey = "trend:"+category+":"+ duration + ":new:" + account_id + ":" + repo_id;
            remainingKey = "trend:"+category+":"+ duration + ":remaining:" + account_id + ":" + repo_id;
            eliminatedKey = "trend:"+category+":"+ duration + ":eliminated:" + account_id + ":" + repo_id;
        }
        stringRedisTemplate.setEnableTransactionSupport(true);
        stringRedisTemplate.multi();
        stringRedisTemplate.opsForList().rightPush(newKey, String.valueOf(newIssueCount));
        stringRedisTemplate.opsForList().trim(newKey,0,29);
        stringRedisTemplate.opsForList().rightPush(remainingKey, String.valueOf(remainingIssueCount));
        stringRedisTemplate.opsForList().trim(remainingKey,0,29);
        stringRedisTemplate.opsForList().rightPush(eliminatedKey, String.valueOf(eliminatedIssueCount));
        stringRedisTemplate.opsForList().trim(eliminatedKey,0,29);
        stringRedisTemplate.exec();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void perDay() {
       durationUpdate("day");
    }

    @Scheduled(cron = "0 5 0 * * Mon")
    public void perWeek() {
        durationUpdate("week");
    }

    @Scheduled(cron = "0 10 0 1 * *")
    public void perMonth() {
        JSONArray projects = getProjectList(null);//所有用户的所有project
        if (projects == null||projects.isEmpty()) return;
        for (int i=0;i<projects.size();i++) {
            JSONObject project=projects.getJSONObject(i);
            if(project!=null){
                String repo_id=project.getString("uuid");
                String category=project.getString("type");
                String dashboardKey = "dashboard:"+category+":month:"+ repo_id;
                stringRedisTemplate.delete(dashboardKey);
            }
        }
    }

    private void initialNewAndEliminated(String key){
        stringRedisTemplate.setEnableTransactionSupport(true);
        stringRedisTemplate.multi();
        stringRedisTemplate.opsForHash().put(key,"new","0");
        stringRedisTemplate.opsForHash().put(key,"eliminated","0");
        stringRedisTemplate.exec();
    }

    private void durationUpdate(String duration){
        List<String> accountIds=getAccountIds();
        for (String account_id : accountIds) {
            JSONArray projects = getProjectList(account_id);
            if (projects == null||projects.isEmpty()) continue;
            int newSummary = 0;
            int remainingSummary = 0;
            int eliminatedSummary = 0;
            Set<String> categories=new HashSet<>();
            for (int i=0;i<projects.size();i++) {
                JSONObject project=projects.getJSONObject(i);
                if(project!=null){
                    String repo_id=project.getString("repo_id");
                    String category=project.getString("type");
                    String dashboardKey = "dashboard:"+category+":"+duration+":"+ repo_id;
                    Boolean hasKey=stringRedisTemplate.hasKey(dashboardKey);
                    if(hasKey!=null&&hasKey){
                        categories.add(category);
                        int newIssueCount = Integer.parseInt((String) stringRedisTemplate.opsForHash().get(dashboardKey, "new"));
                        int remainingIssueCount = Integer.parseInt((String) stringRedisTemplate.opsForHash().get(dashboardKey, "remaining"));
                        int eliminatedIssueCount = Integer.parseInt((String) stringRedisTemplate.opsForHash().get(dashboardKey, "eliminated"));
                        updateTrend(duration, account_id, repo_id,category, newIssueCount, remainingIssueCount, eliminatedIssueCount);
                        newSummary += newIssueCount;
                        remainingSummary += remainingIssueCount;
                        eliminatedSummary += eliminatedIssueCount;
                        //new和eliminated清零
                        initialNewAndEliminated(dashboardKey);
                    }
                }
            }
            //当前用户所有
            for(String category:categories){
                updateTrend(duration, account_id, null,category, newSummary, remainingSummary, eliminatedSummary);
            }
        }
    }

    public void updateIssueCount(String time) {
        if (time.equals("day")) {
            perDay();
        }else if (time.equals("week")){
            perWeek();
        }else {
            perMonth();
        }
    }

}
