package cn.edu.fudan.scanservice;


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

	@Test
	public void test(){
		JSONArray array=new JSONArray();
        for(Scan scan:scanMapper.getScannedCommits("29a3b12e-653f-11e9-9ddc-f93dfaa9da61","bug")){
			JSONObject jsonObject=new JSONObject();
			jsonObject.put("repoId",scan.getRepo_id());
			jsonObject.put("commitId",scan.getCommit_id());
			jsonObject.put("commitTime", DateTimeUtil.format(scan.getCommit_time()));
			array.add(jsonObject);
		}
        kafkaTemplate.send("Measure",JSONArray.toJSONString(array));
	}


}
