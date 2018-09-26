package cn.edu.fudan.issueservice;

import cn.edu.fudan.issueservice.domain.IssueCount;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class IssueServiceApplicationTests {

    @Value("${inner.service.path}")
    private String innerServicePath;
    @Value("${inner.header.key}")
    private String headerKey;
    @Value("${inner.header.value}")
    private String headerValue;

    private RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @BeforeClass
    public static void beforeTest() {
        System.out.println("开始测试..................................");
    }

    @AfterClass
    public static void afterTest() {
        System.out.println("结束测试..................................");
    }

    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


}
