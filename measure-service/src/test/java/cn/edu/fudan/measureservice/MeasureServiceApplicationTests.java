package cn.edu.fudan.measureservice;

import cn.edu.fudan.measureservice.domain.Measure;
import cn.edu.fudan.measureservice.handler.JavaNCSSResultHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MeasureServiceApplicationTests {

    @Autowired
    private JavaNCSSResultHandler handler;

    @Test
    public void contextLoads() {
        Measure measure=handler.handle("directory.xml","directory");
        System.out.println(measure);
    }

}
