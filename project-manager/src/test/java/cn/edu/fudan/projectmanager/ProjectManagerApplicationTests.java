package cn.edu.fudan.projectmanager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectManagerApplicationTests {

	@Value("${account.service.path}")
	private String accountServicePath;
	@Value("${commit.service.path}")
	private String commitServicePath;
	@Value("${issue.service.path}")
	private String issueServicePath;
	@Value("${scan.service.path}")
	private String scanServicePath;

	private RestTemplate restTemplate;

	@Autowired
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Test
	public void contextLoads() {
		restTemplate.delete(issueServicePath+"/Location/delete/"+1);
	}

}
