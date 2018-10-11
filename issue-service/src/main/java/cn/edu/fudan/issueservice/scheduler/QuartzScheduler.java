package cn.edu.fudan.issueservice.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

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

    @SuppressWarnings("unchecked")
    private List<String> getCurrentRepoList(String account_id) {
        HttpEntity<String> requestEntity=new HttpEntity<>(headers);
        return restTemplate.exchange(innerServicePath + "/inner/project/repo-ids?account_id=" + account_id, HttpMethod.GET, requestEntity, List.class).getBody();
    }

    private void updateTrend(String duration, String account_id, String project_id, int newIssueCount, int remainingIssueCount, int eliminatedIssueCount) {
        String newKey = "";
        String remainingKey = "";
        String eliminatedKey = "";
        if (project_id == null) {
            newKey = "trend:" + duration + ":new:" + account_id;
            remainingKey = "trend:" + duration + ":remaining:" + account_id;
            eliminatedKey = "trend:" + duration + ":eliminated:" + account_id;
        } else {
            newKey = "trend:" + duration + ":new:" + account_id + ":" + project_id;
            remainingKey = "trend:" + duration + ":remaining:" + account_id + ":" + project_id;
            eliminatedKey = "trend:" + duration + ":eliminated:" + account_id + ":" + project_id;
        }
        stringRedisTemplate.setEnableTransactionSupport(true);
        stringRedisTemplate.multi();
        stringRedisTemplate.opsForList().rightPush(newKey, String.valueOf(newIssueCount));
        stringRedisTemplate.opsForList().rightPush(remainingKey, String.valueOf(remainingIssueCount));
        stringRedisTemplate.opsForList().rightPush(eliminatedKey, String.valueOf(eliminatedIssueCount));
        stringRedisTemplate.exec();
    }

    @Scheduled(cron = "0 0 0 * * *")
    private void PerDay() {
        for (String account_id : getAccountIds()) {
            List<String> repoIds = getCurrentRepoList(account_id);
            if (repoIds == null) continue;
            int newSummary = 0;
            int remainingSummary = 0;
            int eliminatedSummary = 0;
            for (String repo_id : repoIds) {
                //该用户当前项目前一天的统计结果
                String dashboardKey = "dashboard:day:" + repo_id;
                if (!stringRedisTemplate.hasKey(dashboardKey))
                    continue;
                int newIssueCount = Integer.parseInt((String) stringRedisTemplate.opsForHash().get(dashboardKey, "new"));
                int remainingIssueCount = Integer.parseInt((String) stringRedisTemplate.opsForHash().get(dashboardKey, "remaining"));
                int eliminatedIssueCount = Integer.parseInt((String) stringRedisTemplate.opsForHash().get(dashboardKey, "eliminated"));
                updateTrend("day", account_id, repo_id, newIssueCount, remainingIssueCount, eliminatedIssueCount);
                newSummary += newIssueCount;
                remainingSummary += remainingIssueCount;
                eliminatedSummary += eliminatedIssueCount;
                //清零
                stringRedisTemplate.delete(dashboardKey);
            }
            updateTrend("day", account_id, null, newSummary, remainingSummary, eliminatedSummary);
        }
    }

    @Scheduled(cron = "0 5 0 * * Mon")
    public void perWeek() {
        for (String account_id : getAccountIds()) {
            List<String> repoIds = getCurrentRepoList(account_id);
            if (repoIds == null) continue;
            int newSummary = 0;
            int remainingSummary = 0;
            int eliminatedSummary = 0;
            for (String repo_id : repoIds) {
                //该用户当前项目前一周的统计结果
                String dashboardKey = "dashboard:week:" + repo_id;
                if (!stringRedisTemplate.hasKey(dashboardKey))
                    continue;
                int newIssueCount = Integer.parseInt((String) stringRedisTemplate.opsForHash().get(dashboardKey, "new"));
                int remainingIssueCount = Integer.parseInt((String) stringRedisTemplate.opsForHash().get(dashboardKey, "remaining"));
                int eliminatedIssueCount = Integer.parseInt((String) stringRedisTemplate.opsForHash().get(dashboardKey, "eliminated"));
                updateTrend("week", account_id, repo_id, newIssueCount, remainingIssueCount, eliminatedIssueCount);
                newSummary += newIssueCount;
                remainingSummary += remainingIssueCount;
                eliminatedSummary += eliminatedIssueCount;
                //清零
                stringRedisTemplate.delete(dashboardKey);
            }
            updateTrend("week", account_id, null, newSummary, remainingSummary, eliminatedSummary);
        }
    }

    @Scheduled(cron = "0 10 0 1 * *")
    private void perMonth() {
        List<String> repoList = getCurrentRepoList(null);
        if (repoList == null) return;
        for (String repo_id : repoList) {
            String dashboardKey = "dashboard:month:" + repo_id;
            stringRedisTemplate.delete(dashboardKey);
        }
    }
}
