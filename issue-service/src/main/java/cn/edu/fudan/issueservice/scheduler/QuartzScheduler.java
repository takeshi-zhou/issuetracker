package cn.edu.fudan.issueservice.scheduler;

import cn.edu.fudan.issueservice.domain.IssueCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Component
public class QuartzScheduler {

    @Value("${inner.service.path}")
    private String innerServicePath;
    @Value("${inner.header.key}")
    private  String headerKey;
    @Value("${inner.header.value}")
    private  String headerValue;

    private RedisTemplate<Object,Object> redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate<Object,Object> redisTemplate){
        this.redisTemplate=redisTemplate;
    }

    private RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    private HttpEntity<?> requestEntity;

    private void initRequestEntity(){
        if(requestEntity!=null){
            return;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(headerKey,headerValue);
        requestEntity=new HttpEntity<>(headers);
    }

    @SuppressWarnings("unchecked")
    private List<String> getAccountIds(){
        initRequestEntity();
        return restTemplate.exchange(innerServicePath+"/user/accountIds", HttpMethod.GET, requestEntity,List.class).getBody();
    }

    @SuppressWarnings("unchecked")
    private List<String> getCurrentProjectList(String account_id){
        initRequestEntity();
        return restTemplate.exchange(innerServicePath+"/inner/project/project-id?account_id="+account_id, HttpMethod.GET, requestEntity,List.class).getBody();
    }

    private void issueCountListUpdate(String key,IssueCount issueCount){
        Long size=redisTemplate.opsForList().size(key);
        if(size!=null&&size==30){
            redisTemplate.opsForList().leftPop(key);
        }
        redisTemplate.opsForList().rightPush(key,issueCount);
    }

    private void updateTrend(String duration,String account_id,String project_id,int newIssueCount,int remainingIssueCount,int eliminatedIssueCount){

        String newKey="trend:"+duration+":new:"+account_id+":"+project_id;
        String remainingKey="trend:"+duration+":remaining:"+account_id+":"+project_id;
        String eliminatedKey="trend:"+duration+":eliminated:"+account_id+":"+project_id;
        stringRedisTemplate.setEnableTransactionSupport(true);
        stringRedisTemplate.multi();
        stringRedisTemplate.opsForList().rightPush(newKey,String.valueOf(newIssueCount));
        stringRedisTemplate.opsForList().rightPush(remainingKey,String.valueOf(remainingIssueCount));
        stringRedisTemplate.opsForList().rightPush(eliminatedKey,String.valueOf(eliminatedIssueCount));
        stringRedisTemplate.exec();
    }

    @Scheduled(cron = "0 0 0 * * *")
    private void PerDay(){
        for(String account_id:getAccountIds()){
            List<String> projectIds=getCurrentProjectList(account_id);
            if(projectIds==null)continue;
            int newSummary=0;
            int remainingSummary=0;
            int eliminatedSummary=0;
            for (String project_id : projectIds){
                //该用户当前项目前一天的统计结果
                String dashboardKey="dashboard:day:"+project_id;
                if(!stringRedisTemplate.hasKey(dashboardKey))
                    continue;
                int newIssueCount=(Integer)stringRedisTemplate.opsForHash().get(dashboardKey,"new");
                int remainingIssueCount=(Integer)stringRedisTemplate.opsForHash().get(dashboardKey,"remaining");
                int eliminatedIssueCount=(Integer)stringRedisTemplate.opsForHash().get(dashboardKey,"eliminated");
                updateTrend("day",account_id,project_id,newIssueCount,remainingIssueCount,eliminatedIssueCount);
                newSummary+=newIssueCount;
                remainingSummary+=remainingIssueCount;
                eliminatedSummary+=eliminatedIssueCount;
                //清零
                stringRedisTemplate.delete(dashboardKey);
            }
            updateTrend("day",account_id,null,newSummary,remainingSummary,eliminatedSummary);
        }
    }

    @Scheduled(cron="0 5 0 * * Mon")
    public void perWeek(){
        for(String account_id:getAccountIds()){
            List<String> projectIds=getCurrentProjectList(account_id);
            if(projectIds==null)continue;
            int newSummary=0;
            int remainingSummary=0;
            int eliminatedSummary=0;
            for (String project_id : projectIds){
                //该用户当前项目前一周的统计结果
                String dashboardKey="dashboard:week:"+project_id;
                if(!stringRedisTemplate.hasKey(dashboardKey))
                    continue;
                int newIssueCount=(Integer)stringRedisTemplate.opsForHash().get(dashboardKey,"new");
                int remainingIssueCount=(Integer)stringRedisTemplate.opsForHash().get(dashboardKey,"remaining");
                int eliminatedIssueCount=(Integer)stringRedisTemplate.opsForHash().get(dashboardKey,"eliminated");
                updateTrend("day",account_id,project_id,newIssueCount,remainingIssueCount,eliminatedIssueCount);
                newSummary+=newIssueCount;
                remainingSummary+=remainingIssueCount;
                eliminatedSummary+=eliminatedIssueCount;
                //清零
                stringRedisTemplate.delete(dashboardKey);
            }
            updateTrend("day",account_id,null,newSummary,remainingSummary,eliminatedSummary);
        }
    }

    @Scheduled(cron = "0 10 0 1 * *")
    private void perMonth(){
        List<String> projectList=getCurrentProjectList(null);
        if(projectList==null)return;
        for (String project_id : projectList){
            String dashboardKey="dashboard:month:"+project_id;
            stringRedisTemplate.delete(dashboardKey);
        }
    }

    //每天0:0:01触发
    @Scheduled(cron = "1 0 0 * * ?")
    public void timerToDay(){
        for(String account_id:getAccountIds()){
            List<String> projectIds=getCurrentProjectList(account_id);
            if(projectIds==null)continue;
            IssueCount summary=new IssueCount(0,0,0);
            for (String project_id : projectIds){
                //每天凌晨清零之前，把昨天一天的统计结果存起来
                IssueCount issueCount=(IssueCount) redisTemplate.opsForHash().get(project_id,"today");
                summary.issueCountUpdate(issueCount);
                String key=project_id+"day";
                issueCountListUpdate(key,issueCount);//存起来
                //清零
                redisTemplate.opsForHash().put(project_id,"today",new IssueCount(0,0,0));
            }
            String key=account_id+"day";
            issueCountListUpdate(key,summary);
        }

    }

    //表示每个星期一 0:0:01
    @Scheduled(cron = "1 0 0 ? * MON")
    public void timerToWeek(){
        for(String account_id:getAccountIds()){
            List<String> projectIds=getCurrentProjectList(account_id);
            if(projectIds==null)continue;
            IssueCount summary=new IssueCount(0,0,0);
            for (String project_id : projectIds){
                //每周一凌晨清零之前，把上一周的统计结果存起来
                IssueCount issueCount=(IssueCount) redisTemplate.opsForHash().get(project_id,"week");
                summary.issueCountUpdate(issueCount);
                String key=project_id+"week";
                issueCountListUpdate(key,issueCount);
                redisTemplate.opsForHash().put(project_id,"week",new IssueCount(0,0,0));
            }
            String key=account_id+"week";
            issueCountListUpdate(key,summary);
        }
    }

    //“0 15 10 15 * ?” 每月1日上午0:0:01触发
    @Scheduled(cron = "1 0 0 1 * ?")
    public void timerToMonth(){
        List<String> projectList=getCurrentProjectList(null);
        if(projectList==null)return;
        for (String project_id : projectList){
            redisTemplate.opsForHash().put(project_id,"month",new IssueCount(0,0,0));
        }
    }
}
