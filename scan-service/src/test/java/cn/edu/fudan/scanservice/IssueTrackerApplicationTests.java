package cn.edu.fudan.scanservice;


import cn.edu.fudan.scanservice.component.rest.RestInterfaceManager;
import cn.edu.fudan.scanservice.domain.Scan;
import cn.edu.fudan.scanservice.mapper.ScanMapper;
import cn.edu.fudan.scanservice.util.DateTimeUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-develop.properties")
@SpringBootTest
public class IssueTrackerApplicationTests {

	@Autowired
	private KafkaTemplate kafkaTemplate;

	@Autowired
	private ScanMapper scanMapper;

	@Autowired
	RestInterfaceManager restInterfaceManager;

	@Test
	public void test(){
		JSONArray array=new JSONArray();
        for(Scan scan:scanMapper.getScannedCommits("978765fc-8db5-11e9-b260-3f97950d3532","bug")){
			JSONObject jsonObject=new JSONObject();
			jsonObject.put("repoId",scan.getRepo_id());
			jsonObject.put("commitId",scan.getCommit_id());
			jsonObject.put("commitTime", DateTimeUtil.format(scan.getCommit_time()));
			array.add(jsonObject);
		}
        kafkaTemplate.send("Measure",JSONArray.toJSONString(array));
	}



	@Test
	public void testCommitTime(){
		JSONObject result = restInterfaceManager.getCommitTime("dd6604895528cddbf80fd74877fff1ee5bfa014b","ee4c0f3c-678f-11ea-9f24-d31dd1a362ec");
		System.out.println("result");
	}

}
