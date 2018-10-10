package cn.edu.fudan.accountservice;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@SpringBootTest(classes = AccountServiceApplication.class)
@TestPropertySource("classpath:testApplication.properties")
@PowerMockIgnore("javax.management.*")
public class AccountServiceApplicationTests {

    @BeforeClass
    public static void beforeTest() {
        System.out.println("开始测试..................................");
    }


    @AfterClass
    public static void afterTest() {
        System.out.println("结束测试..................................");
    }
}
