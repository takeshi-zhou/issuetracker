/**
 * @description:
 * @author: fancying
 * @create: 2019-09-24 19:01
 **/
package cn.edu.fudan.issueservice.component;

import cn.edu.fudan.issueservice.IssueServiceApplicationTests;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@PrepareForTest({ RestTemplate.class})
public class RestInterfaceManagerTest extends IssueServiceApplicationTests {



    public static void main(String[] args) {

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://10.141.221.85:8005/measurement/remainingIssue/aa41c6ae-6fd0-11e9-b723-0f92b2ad63bf/18c74e4ad86de23c41ea57abc1e88ebed3140fe3?spaceType=project";
        HttpHeaders headers = new HttpHeaders();
        headers.add("token", "ec15d79e36e14dd258cfff3d48b73d35");
/*        // header填充
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.put("token", Collections.singletonList("ec15d79e36e14dd258cfff3d48b73d35"));

        // 获取单例RestTemplate
        RestTemplate restTemplate = HttpInvoker.getRestTemplate();
        HttpEntity request = new HttpEntity(headers);*/
        ResponseEntity responseEntity = restTemplate.exchange(url,
                HttpMethod.GET,
                new HttpEntity<String>(headers),Object.class);



        System.out.println(responseEntity.getStatusCode());



    }

    @Autowired
    @InjectMocks
    RestInterfaceManager restInterfaceManager;

    @Test
    public void getSonarIssueResults() {
        JSONObject result =restInterfaceManager.getSonarIssueResults("swagger-maven-plugin-master",null,100,false,1);
        System.out.println(1);
    }

    @Test
    public void getRuleInfo() {
        JSONObject result =restInterfaceManager.getRuleInfo("squid:S1313",null,null);
        int  a=1;
        a= a+1;
        System.out.println(a);
    }

    @Test
    public void getSonarSourceLines() {
        JSONObject result =restInterfaceManager.getSonarSourceLines("scala-maven-plugin-master:src/main/java/scala_maven/TychoUtilities.java",0,1000);
        int  a=1;
        a= a+1;
        System.out.println(a);
    }

    @Test
    public void getSonarIssueResultsBySonarIssueKey() {
        JSONObject result =restInterfaceManager.getSonarIssueResultsBySonarIssueKey("AW7upw0tbHwuxD0P5CSU",0);
        int  a=1;
        a= a+1;
        System.out.println(a);
    }

    @Test
    public void getSonarSourcesLinesShow(){
        List<String> resultList = restInterfaceManager.getSonarSourcesLinesShow("issueTrackerScannerTest:issue-service/src/main/java/cn/edu/fudan/issueservice/component/RestInterfaceManager.java",1,239);
        int index = 0;
        for (String code:resultList) {
            System.out.println((index++)+": "+code);
        }
    }

    @Test
    public void getDeveloperListByDuration(){
        JSONObject resultList = restInterfaceManager.getDeveloperListByDuration(null,"2019-01-09","2020-01-10","8aeccc72-337a-11ea-8e39-871d7ac5caa9");
        int  a=1;
        a= a+1;
        System.out.println(a);
    }


    @Test
    public void getPreScannedCommitByCurrentCommit(){
        List<String> resultList = restInterfaceManager.getPreScannedCommitByCurrentCommit("c98bfefa-5326-11ea-840d-074d4d76eca2",  "17bbc18ccc7c000a35d072627981b484dfe0b7ea","bug");
        int  a=1;
        a= a+1;
        System.out.println(a);
    }


    @Test
    public void getLatestScanFailedCommitId(){
        String resultList = restInterfaceManager.getLatestScanFailedCommitId("294b6600-56b0-11ea-807d-2b3ae82c4553",  "ffb7104917c7caf4b841c3c8e6cbff84199013a5","bug");
        int  a=1;
        a= a+1;
        System.out.println(a);
    }

    @Test
    public void getOneCommitByCommitId(){
        JSONObject result = restInterfaceManager.getOneCommitByCommitId("71b599686d8cc73ad71db9f4497823cbd7926705");
        int  a=1;
        a= a+1;
        System.out.println(a);
    }

    @Test
    public void getScanByRepoIdAndStatus(){
        JSONArray result = restInterfaceManager.getScanByRepoIdAndStatus("ae9e7ff0-6ff6-11e9-b723-0f92b2ad63bf","doing...");
        int  a=1;
        a= a+1;
        System.out.println(a);
    }
}