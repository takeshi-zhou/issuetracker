package cn.edu.fudan.issueservice;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author WZY
 * @version 1.0
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class Test {

    @Value("${inner.service.path}")
    private String innerServicePath;
    @Value("${inner.header.key}")
    private String headerKey;
    @Value("${inner.header.value}")
    private String headerValue;

    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @org.junit.Test
    public void test(){
        System.out.println(stringRedisTemplate.opsForHash().get("dashboard:day:123", "new"));
    }
}
