package cn.edu.fudan.measureservice;

import cn.edu.fudan.measureservice.component.RestInterfaceManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MeasureServiceApplicationTests {

    @Autowired
    private RestInterfaceManager restInterfaceManager;

    @Test
    public void contextLoads() {
        System.out.println(restInterfaceManager.getRepoPath("29a3b12e-653f-11e9-9ddc-f93dfaa9da61",""));
    }

}
