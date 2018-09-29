package cn.edu.fudan.cloneservice;


import cn.edu.fudan.cloneservice.domain.Scan;
import cn.edu.fudan.cloneservice.domain.ScanInitialInfo;
import cn.edu.fudan.cloneservice.lock.RedisLuaLock;
import cn.edu.fudan.cloneservice.task.CloneScanTask;
import cn.edu.fudan.cloneservice.util.DateTimeUtil;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CloneServiceApplicationTests {

    @Value("${resultFileHome}")
    private String resultFileHome;
    @Value("${inner.service.path}")
    private String innerServicePath;

    @Autowired
    private CloneScanTask cloneScanTask;

    @Autowired
    private HttpHeaders httpHeaders;

    @Autowired
    private RedisLuaLock redisLock;

    @Test
    public void contextLoads() {
       // cloneScanTask.analyzeResultFile("test","test",resultFileHome+"B_merge.csv.xml");
    }

    @Test
    public void testMapping(){
      //  cloneScanTask.mapping("test","test","clone");
    }

    @Test
    public void addScan(){
        TestRestTemplate restTemplate=new TestRestTemplate();
        String msg="{\n" +
                "  \"repoId\": \"e5e2575e-bbd6-11e8-b32d-d067e5ea858d\",\n" +
                "  \"repoName\": \"emoji-java\",\n" +
                "  \"repoPath\": \"github/vdurmont/emoji-java\",\n" +
                "  \"scan\": {\n" +
                "    \"category\": \"clone\",\n" +
                "    \"commit_id\": \"b41cdfae1cc4e2fd0a5e0ae699ea67df7b6d6aee\",\n" +
                "    \"commit_time\": 1501405702000,\n" +
                "    \"name\": \"emoji-java-1538135844739\",\n" +
                "    \"repo_id\": \"e5e2575e-bbd6-11e8-b32d-d067e5ea858d\",\n" +
                "    \"start_time\": 1538135844000,\n" +
                "    \"status\": \"doing...\",\n" +
                "    \"uuid\": \"24cd4f97-70c9-4192-8df0-039dbc11ed23\"\n" +
                "  }\n" +
                "}";
        ScanInitialInfo scanInitialInfo = JSONObject.parseObject(msg, ScanInitialInfo.class);
        Scan scan=scanInitialInfo.getScan();
       // scan.setStart_time(DateTimeUtil.formatedDate(scan.getStart_time()));
        scan.setStatus("done");//设为结束状态
        scan.setEnd_time(DateTimeUtil.formatedDate(new Date()));
        HttpEntity<Object> entity=new HttpEntity<>(scan,httpHeaders);
        JSONObject response =restTemplate.exchange(innerServicePath+"/inner/scan", HttpMethod.POST,entity,JSONObject.class).getBody();
        if(response==null||response.getIntValue("code")!=200){
            System.out.println("scan add failed!");
        }
    }

    @Test
    public void testLock() throws InterruptedException {
        String repoId="1eb8a048-a36d-4699-9f92-3626249f0f37";
        String identifier = redisLock.acquireLockWithTimeOut(repoId, 60, 60, TimeUnit.SECONDS);
        try {
             TimeUnit.SECONDS.sleep(5);
        } finally {
            if (redisLock.releaseLock(repoId, identifier)) {
                System.out.println("repo->" + repoId + " release lock success!");
            }
        }
    }

}
