package cn.edu.fudan.eventservice;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@PowerMockIgnore({"javax.crypto.*","javax.management.*"})
public class EventServiceApplicationTests {

    @Value("${inner.service.path}")
    private String innerServicePath;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private HttpHeaders httpHeaders;

    @Test
    public void contextLoads() {
//        HttpEntity<String> httpEntity=new HttpEntity<>(httpHeaders);
//        Map<String,String> urlParameters=new HashMap<>();
//        urlParameters.put("userToken","ec15d79e36e14dd258cfff3d48b73d35");
//        String accountId=restTemplate.exchange(innerServicePath+"/user/accountId?userToken={userToken}", HttpMethod.GET,httpEntity,String.class,urlParameters).getBody();
//        log.info(accountId);
    }

}
