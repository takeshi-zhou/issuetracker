package cn.edu.fudan.issueservice;

import cn.edu.fudan.issueservice.scheduler.QuartzScheduler;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author WZY
 * @version 1.0
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class Test {

    @Value("${inner.service.path}")
    private String innerServicePath;
    @Value("${inner.header.key}")
    private String headerKey;
    @Value("${inner.header.value}")
    private String headerValue;

    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Autowired
    private QuartzScheduler quartzScheduler;

    private List<String> repoIds= Arrays.asList(new String[]{"00411c78-b72e-11e8-b32d-d067e5ea858d","2216c7fc-b4eb-11e8-885d-d067e5ea858d"});

    private void dashboardUpdate(String repo_id, int newIssueCount, int remainingIssueCount, int eliminatedIssueCount,String category) {
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

    private void  scan(){
        Random random1=new Random();
        String repo_id=repoIds.get(random1.nextInt(2));
        Random random2=new Random();
        int newIssueCount=random2.nextInt(50);
        Random random3=new Random();
        int remainingIssueCount=random3.nextInt(50);
        Random random4=new Random();
        int eliminatedIssueCount=random4.nextInt(50);
        dashboardUpdate(repo_id,newIssueCount,remainingIssueCount,eliminatedIssueCount,"bug");
    }

    @org.junit.Test
    public void test(){
    }


}
