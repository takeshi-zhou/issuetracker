package cn.edu.fudan.eventservice;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
@PowerMockIgnore("javax.management.*")
@SpringBootTest(classes = EventServiceApplication.class)
public class EventServiceApplicationTests {

    @Test
    public void contextLoads() {
    }

}
