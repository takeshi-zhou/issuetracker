package cn.edu.fudan.projectmanager.Clone;




import com.alibaba.fastjson.JSONObject;
import io.netty.handler.codec.json.JsonObjectDecoder;

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
    public class ScanInitialInfo {

        private String repoId;
        private List<String> commitList;

        public ScanInitialInfo(String repoId, List<String> commitList) {
            this.repoId = repoId;
            this.commitList = commitList;
        }

        public String getRepoId() {
            return repoId;
        }

        public void setRepoId(String repoId) {
            this.repoId = repoId;
        }

        public List<String> getCommitList() {
            return commitList;
        }

        public void setCommitList(List<String> commitList) {
            this.commitList = commitList;
        }
    }

    @Autowired
    private KafkaTemplate kafkaTemplate;


    @Test
    @SuppressWarnings("unchecked")
    public void testPackageScan()throws Exception{
        //ScanInitialInfo scanInitialInfo = new ScanInitialInfo("happy", "123", "here");
        String urlPrefix = "/home/fdse/user/issueTracker/repo";
        List<String> commitList = new ArrayList<>();
        commitList.add("7a51fb3a2d5d22ae430702a9e358ff65e912a740");
        commitList.add("50289e92d4d2f205745e0e7c6f862d5568259051");
        commitList.add("62fcf3710b68a439c8c457be01854169f206ea45");
        commitList.add("e0707ecd8a55e1b792f2b992cc29e7b35819bfd9");
        commitList.add("2a8f0fc60c94b795d9061687b21552ce8b97f72c");

        ScanInitialInfo scanInitialInfo = new ScanInitialInfo(
                "76a84f5a-4ec6-11e9-af18-9dfa342942d6",
                commitList);
        System.out.println(JSONObject.toJSONString(scanInitialInfo));
        kafkaTemplate.send("CloneZNJ", JSONObject.toJSONString(scanInitialInfo));



    }

}
