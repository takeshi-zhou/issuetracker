package cn.edu.fudan.projectmanager;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@SpringBootTest(classes = ProjectManagerApplication.class)
@TestPropertySource("classpath:application-test.properties")
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

}
