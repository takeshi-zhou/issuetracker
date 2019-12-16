package cn.edu.fudan.scanservice.topic;

import cn.edu.fudan.scanservice.ScanServiceApplicationTests;
import cn.edu.fudan.scanservice.domain.ScanMessageWithTime;
import com.alibaba.fastjson.JSONArray;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;
import java.util.List;

public class TopicTest extends ScanServiceApplicationTests {

    @Autowired
    private KafkaTemplate kafkaTemplate;



    @Test
    public void testMeasureTopic(){
        List<ScanMessageWithTime> list = new ArrayList<>();
        ScanMessageWithTime scanMessageWithTime = new ScanMessageWithTime();
        scanMessageWithTime.setCommitTime("2019-12-15 14:39:37");
        scanMessageWithTime.setCommitId("cd4010dcb7612281e63fd2fb79f8c947c6937077");
        scanMessageWithTime.setRepoId("4e21a81e-1efb-11ea-9fbe-11bdb9e75ae6");
        list.add(scanMessageWithTime);
        kafkaTemplate.send("Measure", JSONArray.toJSONString(list));
    }

}
