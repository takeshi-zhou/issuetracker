package cn.edu.fudan.issueservice;

import cn.edu.fudan.issueservice.domain.IssueCount;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class IssueServiceApplicationTests {

	@BeforeClass
	public static void beforeTest(){
		System.out.println("开始测试..................................");
	}

	@AfterClass
	public static void afterTest(){
		System.out.println("结束测试..................................");
	}

	private RedisTemplate<Object,Object> redisTemplate;

	@Autowired
	public void setRedisTemplate(RedisTemplate<Object,Object> redisTemplate){
		this.redisTemplate=redisTemplate;
	}

	@Test
	public void test(){
		String project_id="616bb11c-2a6f-4cf3-9aa4-60cfa26fa90f";
		//redisTemplate.opsForHash().put(project_id,"today",new IssueCount(0,0,0));
		//redisTemplate.opsForHash().put(project_id,"week",new IssueCount(0,0,0));
		//redisTemplate.opsForHash().put(project_id,"month",new IssueCount(0,0,0));
		IssueCount today=(IssueCount) redisTemplate.opsForHash().get(project_id,"today");
		IssueCount week=(IssueCount) redisTemplate.opsForHash().get(project_id,"week");
		IssueCount month=(IssueCount) redisTemplate.opsForHash().get(project_id,"month");
		System.out.println(today);
		System.out.println(week);
		System.out.println(month);
	}


}
