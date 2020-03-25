package cn.edu.fudan.scanservice.component;

import cn.edu.fudan.scanservice.ScanServiceApplicationTests;
import cn.edu.fudan.scanservice.component.rest.RestInterfaceManager;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

@PrepareForTest({ RestTemplate.class})
public class RestInterfaceManagerTest extends ScanServiceApplicationTests {

    @Autowired
    @InjectMocks
    RestInterfaceManager restInterfaceManager;

    @Test
    public void getSonarIssueResults() {
        JSONObject object = restInterfaceManager.getSonarIssueResults("IssueTracker-Master-zhonghui20191012",null,100,false);

        System.out.println(object);
    }


    @Test
    public void getCommitsOfRepoByConditions() {
        JSONObject result = restInterfaceManager.getCommitsOfRepoByConditions("f6d6201c-6514-11e9-9ddc-f93dfaa9da61",1,1,false);
        System.out.println("success");
    }


    @Test
    public void getCommitsOfRepo() {
        JSONObject result = restInterfaceManager.getCommitsOfRepo("fe5ed888-6a82-11e9-ad13-e9d58090e871",1,1);
        System.out.println("success");
    }


    @Test
    public void getRepoPath() {
        String result = restInterfaceManager.getRepoPath("bc62ad1a-14d7-11ea-9635-3d7444b06c34","aad48b73e22f8fe4b8222a5686a2b39344f1db55");
        System.out.println("result");
    }

    @Test
    public void startCodeTracker() {
        boolean result = restInterfaceManager.startCodeTracker("7e35f5ae-544e-11ea-9837-31458decacbe","master","33f48e3437f2c098a057ab617bc6c522a618eca7");
        System.out.println("result");
    }

    @Test
    public void getCodeTrackerStatus() {
        String result = restInterfaceManager.getCodeTrackerStatus("7e35f5ae-544e-11ea-9837-31458decacbe","master");
        System.out.println("result");
    }

    @Test
    public void updateCodeTracker() {
        boolean result = restInterfaceManager.updateCodeTracker("7e35f5ae-544e-11ea-9837-31458decacbe","master");
        System.out.println("result");
    }


    @Test
    public void getCommitTime() {
        JSONObject result = restInterfaceManager.getCommitTime("3bfcbcda-57f1-11ea-b10e-1f495a510de6","873da28080dc364b2ef5f70f3880b0bb6751316d");
        System.out.println("result");
    }
}
