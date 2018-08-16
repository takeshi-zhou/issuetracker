package cn.edu.fudan.scanservice;

import cn.edu.fudan.scanservice.dao.ScanDao;
import cn.edu.fudan.scanservice.service.ScanOperation;
import cn.edu.fudan.scanservice.service.ScanService;
import cn.edu.fudan.scanservice.service.impl.ScanOperationAdapter;
import cn.edu.fudan.scanservice.service.impl.ScanServiceImpl;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ScanServiceApplicationTests {

	private final static Logger logger= LoggerFactory.getLogger(ScanOperationAdapter.class);

	@Value("${commit.service.path}")
	private String commitServicePath;
	//@Value("${project.service.path}")
	private String projectServicePath;
	@Value("${issue.service.path}")
	private String issueServicePath;

	protected RestTemplate restTemplate;

	@Autowired
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Test
	public void contextLoads() {
		JSONObject requestParam =new JSONObject();
		requestParam.put("uuid","f2e6189f-e4c8-47d3-b664-9973fdd3038b");
		requestParam.put("till_commit_time","2016-05-19 10:00:02");
		projectServicePath="http://10.141.221.80:8002/project";
		restTemplate.put(projectServicePath,requestParam,JSONObject.class);
	}
}
