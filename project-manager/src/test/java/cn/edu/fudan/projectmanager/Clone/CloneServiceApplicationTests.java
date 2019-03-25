package cn.edu.fudan.projectmanager.Clone;




import io.netty.handler.codec.json.JsonObjectDecoder;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CloneServiceApplicationTests {

    @Autowired
    private KafkaTemplate kafkaTemplate;


    @Test
    @SuppressWarnings("unchecked")
    public void testCheckout()throws Exception{
        //ScanInitialInfo scanInitialInfo = new ScanInitialInfo("happy", "123", "here");
        String urlPrefix = "/home/fdse/user/issueTracker/repo";
        List<String> commitList = new ArrayList<>();
        commitList.add("922dc95453760d98b0ccb00a08239f0a5b695bf0");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("repoName", "genson");
        jsonObject.put("repoId", "76a84f5a-4ec6-11e9-af18-9dfa342942d6");
        jsonObject.put("commitList", commitList);
        jsonObject.put("repoPath", "/home/fdse/user/issueTracker/repo/github/owlike/genson-master");
        kafkaTemplate.send("Clone", jsonObject);



    }

}
