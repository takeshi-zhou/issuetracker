package cn.edu.fudan.scanservice.component;

import cn.edu.fudan.scanservice.ScanServiceApplicationTests;
import cn.edu.fudan.scanservice.service.ScanService;
import cn.edu.fudan.scanservice.service.impl.ScanServiceImpl;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.doNothing;

@PrepareForTest({ RestTemplate.class})
public class RestInterfaceManagerTest extends ScanServiceApplicationTests {

    @Autowired
    @InjectMocks
    RestInterfaceManager restInterfaceManager;

    @Test
    public void getSonarIssueResults() {
        restInterfaceManager.getSonarIssueResults("IssueTracker-Master-zhonghui20191012",null,100,false);
    }
}
