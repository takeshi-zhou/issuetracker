package cn.edu.fudan.projectmanager;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProjectManagerApplication.class)
@TestPropertySource("classpath:testApplication.properties")
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
