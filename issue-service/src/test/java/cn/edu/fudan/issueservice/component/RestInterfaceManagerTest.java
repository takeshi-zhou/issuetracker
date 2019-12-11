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

import java.util.Collections;

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
        JSONObject result =restInterfaceManager.getSonarSourceLines("issueTrackerScannerTest:issue-service/src/main/java/cn/edu/fudan/issueservice/component/RestInterfaceManager.java",144,144);
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
}