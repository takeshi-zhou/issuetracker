package cn.edu.fudan.cloneservice;


import cn.edu.fudan.cloneservice.service.impl.CloneScanServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CloneServiceApplicationTests {

    @Value("${resultFileHome}")
    private String resultFileHome;

    @Autowired
    private CloneScanServiceImpl cloneScanService;

    @Test
    public void contextLoads() {
        cloneScanService.analyzeResultFile("test","test",resultFileHome+"B_merge.csv.xml");
    }

}
