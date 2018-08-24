package cn.edu.fudan.projectmanager;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class ProjectManagerApplicationTests {


	@BeforeClass
	public static void beforeTest(){
		System.out.println("开始测试..................................");
	}

	@AfterClass
	public static void afterTest(){
		System.out.println("结束测试..................................");
	}


}
