package cn.edu.fudan.issueservice.util;

import cn.edu.fudan.issueservice.domain.IssueCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class QuartzScheduler {

    @Value("${project.service.path}")
    private String projectServicePath;

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

    List<String> projectList = restTemplate.getForObject(projectServicePath+"/all-project", List.class);

    //每天0:0:01触发
    @Scheduled(cron = "1 0 0 * * ?")
    public void timerToDay(){
        if (projectList == null)
            return;
        for (String project_id : projectList){
            redisTemplate.opsForHash().put(project_id,"today",new IssueCount(0,0,0));
        }
    }

    //表示每个星期一 0:0:01
    @Scheduled(cron = "1 0 0 ? * MON ")
    public void timerToWeek(){
        for (String project_id : projectList){
            redisTemplate.opsForHash().put(project_id,"week",new IssueCount(0,0,0));
        }
    }

    //“0 15 10 15 * ?” 每月1日上午0:0:01触发
    @Scheduled(cron = "1 0 0 1 * ?")
    public void timerToMonth(){
        for (String project_id : projectList){
            redisTemplate.opsForHash().put(project_id,"month",new IssueCount(0,0,0));
        }
    }
}
