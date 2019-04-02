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
        public String getRepoName() {
            return repoName;
        }

        public void setRepoName(String repoName) {
            this.repoName = repoName;
        }

        public String getRepoId() {
            return repoId;
        }

        public void setRepoId(String repoId) {
            this.repoId = repoId;
        }

        public String getRepoPath() {
            return repoPath;
        }

        public void setRepoPath(String repoPath) {
            this.repoPath = repoPath;
        }

        public List<String> getCommitList() {
            return commitList;
        }

        public void setCommitList(List<String> commitList) {
            this.commitList = commitList;
        }

        private String repoName;
        private String repoId;
        private String repoPath;
        private List<String> commitList;
        public ScanInitialInfo(String repoName, String repoId, String repoPath, List<String> commitList) {

//        this.scan = scan;
            this.repoName = repoName;
            this.repoId = repoId;
            this.repoPath = repoPath;
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
        commitList.add("556b393e1f68462722faa4564daea792e2607cb1");

        ScanInitialInfo scanInitialInfo = new ScanInitialInfo("genson",
                "76a84f5a-4ec6-11e9-af18-9dfa342942d6",
                "D:\\repo\\github\\genson" ,
                commitList);
        System.out.println(JSONObject.toJSONString(scanInitialInfo));
        kafkaTemplate.send("CloneZNJ", JSONObject.toJSONString(scanInitialInfo));



    }

}
