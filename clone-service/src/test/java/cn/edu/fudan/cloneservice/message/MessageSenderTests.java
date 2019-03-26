package cn.edu.fudan.cloneservice.message;

import cn.edu.fudan.cloneservice.domain.ScanInitialInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
@SpringBootTest
public class MessageSenderTests {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Test
    @SuppressWarnings("unchecked")
    public void testSendMessage(){
        ScanInitialInfo scanInitialInfo = new ScanInitialInfo("happy", "123", "here");

        kafkaTemplate.send("Clone", scanInitialInfo.toString());

    }
}
