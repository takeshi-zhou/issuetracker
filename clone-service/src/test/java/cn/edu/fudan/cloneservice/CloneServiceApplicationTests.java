package cn.edu.fudan.cloneservice;



import cn.edu.fudan.cloneservice.domain.ScanInitialInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CloneServiceApplicationTests {

    @Autowired
    private KafkaTemplate kafkaTemplate;


    @Test
    @SuppressWarnings("unchecked")
    public void testCheckout(){



    }

}
