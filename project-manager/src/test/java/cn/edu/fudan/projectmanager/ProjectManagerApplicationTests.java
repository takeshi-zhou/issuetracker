package cn.edu.fudan.projectmanager;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@SpringBootTest(classes = ProjectManagerApplication.class)
@TestPropertySource("classpath:testApplication.properties")
@PowerMockIgnore("javax.management.*")
public class ProjectManagerApplicationTests {


    @BeforeClass
    public static void beforeTest() {
        System.out.println("开始测试..................................");
    }

    @AfterClass
    public static void afterTest() {
        System.out.println("结束测试..................................");
    }

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    @SuppressWarnings("unchecked")
    public void test(){
        stringRedisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                operations.delete("test");
                return operations.exec();
            }
        });
    }

    @Test
    public void test2(){
        //redisTemplate使用事务前必须开启事务支持
        stringRedisTemplate.setEnableTransactionSupport(true);
        stringRedisTemplate.multi();
        stringRedisTemplate.opsForValue().set("test","test");
        stringRedisTemplate.exec();
        System.out.println(stringRedisTemplate.opsForValue().get("test"));
    }

}
