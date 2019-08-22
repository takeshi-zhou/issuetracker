package cn.edu.fudan.measureservice;

import cn.edu.fudan.measureservice.component.RestInterfaceManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@SpringBootTest(classes = MeasureServiceApplication.class)
@TestPropertySource("classpath:application-test.properties")
@PowerMockIgnore({"javax.crypto.*","javax.management.*"})
public class MeasureServiceApplicationTests {

    @Autowired
    private RestInterfaceManager restInterfaceManager;

    @Test
    public void contextLoads() {
        System.out.println(restInterfaceManager.getRepoPath("29a3b12e-653f-11e9-9ddc-f93dfaa9da61",""));
    }

}
