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
        restInterfaceManager.getSonarIssueResults("IssueTracker-Master-zhonghui20191012",null,100,false);
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
}
