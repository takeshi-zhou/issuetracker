package cn.edu.fudan.scanscheduler;

import cn.edu.fudan.scanscheduler.domain.Scan;
import cn.edu.fudan.scanscheduler.util.DateTimeUtil;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ScanSchedulerApplicationTests {

	@Value("${commit.service.path}")
	private String commitServicePath;
	@Value("${project.service.path}")
	private String projectServicePath;
	@Value("${scan.service.path}")
	private String scanServicePath;

	private RestTemplate restTemplate;

	@Autowired
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Test
	public void contextLoads() {
		JSONObject commitParam=new JSONObject();
		commitParam.put("is_scanned",0);
		commitParam.put("commit_id","9eddf7894c88984f2499f17e2bfa0ed7f3ff5192");
		JSONObject json = restTemplate.postForEntity(commitServicePath+"/update", commitParam, JSONObject.class).getBody();
		System.out.println(json.toJSONString());
	}

	@Test
	public void test1(){
		List<JSONObject> rawIssues=new ArrayList<>();
		JSONObject rawIssue1=new JSONObject();
		rawIssue1.put("type","test1");
		rawIssue1.put("detail","test1");
		rawIssue1.put("file_name","test1");
		rawIssue1.put("scan_id","test1");
		rawIssue1.put("commit_id","test1");
		rawIssue1.put("uuid","test1");
		rawIssues.add(rawIssue1);

		JSONObject rawIssue2=new JSONObject();
		rawIssue2.put("type","test2");
		rawIssue2.put("detail","test2");
		rawIssue2.put("file_name","test2");
		rawIssue2.put("scan_id","test2");
		rawIssue2.put("commit_id","test2");
		rawIssue2.put("uuid","test2");
		rawIssues.add(rawIssue2);
		JSONObject json=restTemplate.postForEntity("http://localhost:8003/RawIssue/add",rawIssues,JSONObject.class).getBody();
		System.out.println(json.toJSONString());
	}

	@Test
	public void test2(){
		List<JSONObject> locations=new ArrayList<>();
		JSONObject location=new JSONObject();
		location.put("uuid", UUID.randomUUID().toString());
		location.put("start_line",1);
		location.put("end_line",1);
		location.put("bug_lines","test1");
		location.put("file_path","test1");
		location.put("class_name","test1");
		location.put("method_name","test1");
		location.put("rawIssue_id","test1");
		location.put("code","test1");
		locations.add(location);

		JSONObject location2=new JSONObject();
		location2.put("uuid", UUID.randomUUID().toString());
		location2.put("start_line",2);
		location2.put("end_line",2);
		location2.put("bug_lines","test2");
		location2.put("file_path","test2");
		location2.put("class_name","test2");
		location2.put("method_name","test2");
		location2.put("rawIssue_id","test2");
		location2.put("code","test2");
		locations.add(location2);
		JSONObject json=restTemplate.postForEntity("http://localhost:8003/Location/add",locations,JSONObject.class).getBody();
		System.out.println(json.toJSONString());
	}

	@Test
	public void test3(){
		//更新till_commit_time
		String commit_time=restTemplate.getForObject(commitServicePath+"/commitDate/35acea7e7918f5678618ce907a462481321a807b",Object.class).toString();
		String till_commit=restTemplate.getForObject(commitServicePath+"/tillCommitDate/1",Object.class).toString();
		System.out.println(commit_time);
		System.out.println(till_commit);
		if(till_commit==null||commit_time.compareTo(till_commit)>0){
			JSONObject requestParam =new JSONObject();
			requestParam.put("uuid","1");
			requestParam.put("till_commit_time", commit_time);
			JSONObject result=restTemplate.postForEntity(projectServicePath+"/update",requestParam,JSONObject.class).getBody();
			System.out.println(result.toJSONString());
		}
	}

	@Test
	public void test4(){
		Scan scan=new Scan();
		scan.setUuid("dc7d6cae-8211-4325-9a09-970a7e3c779f");
		scan.setName("boilerpipe-1533010901352");
		scan.setStart_time(new Date());
		scan.setEnd_time(new Date());
		scan.setStatus("done");
		scan.setResult_summary("{\"timestamp\":\"Tue, 31 Jul 2018 12:21:45 +0800\",\"total_classes\":\"104\",\"referenced_classes\":\"209\",\"total_bugs\":\"22\",\"total_size\":\"3679\",\"num_packages\":\"13\",\"java_version\":\"1.8.0_131\",\"vm_version\":\"25.131-b11\",\"cpu_seconds\":\"21.97\",\"clock_seconds\":\"6.07\",\"peak_mbytes\":\"269.04\",\"alloc_mbytes\":\"683.00\",\"gc_seconds\":\"0.28\",\"priority_2\":\"20\",\"priority_1\":\"2\"}");
		scan.setProject_id("1");
		scan.setCommit_id("35acea7e7918f5678618ce907a462481321a807b");

		JSONObject param=new JSONObject();
		param.put("uuid",scan.getUuid());
		param.put("name",scan.getName());
		param.put("start_time",DateTimeUtil.format(scan.getStart_time()));
		param.put("end_time",DateTimeUtil.format(scan.getEnd_time()));
		param.put("status",scan.getStatus());
		param.put("result_summary",scan.getResult_summary());
		param.put("project_id",scan.getUuid());
		param.put("commit_id",scan.getCommit_id());
		JSONObject result=restTemplate.postForEntity(scanServicePath+"/add",param,JSONObject.class).getBody();
		System.out.println(result.toJSONString());
	}

	@Test
	public void test5(){

	}
}
