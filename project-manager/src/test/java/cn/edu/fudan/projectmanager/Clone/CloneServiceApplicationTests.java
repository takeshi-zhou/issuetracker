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



        commitList.add("03f353e39d23c71825405acd22db94428323997b");
        commitList.add("5a262a78557009f38915416dd7ddcfb98bc42f49");
        commitList.add("363a95acf17c2452986581f76fb44af953e12a28");
        commitList.add("55d6f13c230c7aa7dc5bf0423a8941b76ddd5eb6");
        commitList.add("6e71ed2d357b637e4f346663d386278e713bb7bd");
        commitList.add("74f0fc9619708e2aaed7018a352ef491967d787f");


        ScanInitialInfo scanInitialInfo = new ScanInitialInfo(
                "4f696ccc-65ef-11e9-9ddc-f93dfaa9da61",
                commitList);
        System.out.println(JSONObject.toJSONString(scanInitialInfo));
        kafkaTemplate.send("CloneZNJ", JSONObject.toJSONString(scanInitialInfo));



    }

}
