package cn.edu.fudan.issueservice;

import cn.edu.fudan.issueservice.dao.RawIssueDao;
import cn.edu.fudan.issueservice.domain.RawIssue;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.*;

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


}
