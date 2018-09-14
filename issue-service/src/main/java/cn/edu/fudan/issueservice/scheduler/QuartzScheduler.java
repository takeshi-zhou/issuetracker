package cn.edu.fudan.issueservice.scheduler;

import cn.edu.fudan.issueservice.domain.IssueCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
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
