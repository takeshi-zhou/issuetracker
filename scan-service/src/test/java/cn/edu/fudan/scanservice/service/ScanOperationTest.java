package cn.edu.fudan.scanservice.service;

import cn.edu.fudan.scanservice.component.RestInterfaceManager;
import cn.edu.fudan.scanservice.dao.ScanDao;
import cn.edu.fudan.scanservice.domain.Scan;
import cn.edu.fudan.scanservice.domain.ScanInitialInfo;
import cn.edu.fudan.scanservice.tools.ScanOperationAdapter;
import cn.edu.fudan.scanservice.util.TestDataMaker;
import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@PrepareForTest({ScanOperation.class, ScanOperationAdapter.class, RestTemplate.class, ScanDao.class})
public class ScanOperationTest {

    @Value("${commit.service.path}")
    private String commitServicePath;
    @Value("${repository.service.path}")
    private String repoServicePath;

    @Mock
    @Autowired
    private ScanDao scanDao;

    @Mock
    private RestInterfaceManager restInterfaceManager;


    @Autowired
    @InjectMocks
    private ScanOperation scanOperation = new ScanOperationAdapter();

    private Scan scan;
    private TestDataMaker testDataMaker;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        MemberModifier.field(ScanOperationAdapter.class,"restInterfaceManager").set(scanOperation,restInterfaceManager);
        testDataMaker = new TestDataMaker();
        scan = testDataMaker.scanMakerSc1();

    }

    @Test
    public void isScanned() {
        String commitId = "comm";
        Mockito.when(scanDao.isScanned(commitId)).thenReturn(true);
        Boolean result = scanOperation.isScanned(commitId);
        Assert.assertEquals(true,result);
    }

    @Test
    public void checkOut() {
        /*
            预设 数据
         */
        String repoId = "repo1";
        String commitId = "comm";
        JSONObject response = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("status","Successful");
        response.put("data",data);

        /*
            当commit check out 返回值未null
         */
        Mockito.when(restInterfaceManager.checkOut(repoId,commitId)).thenReturn(null);
        Boolean result = scanOperation.checkOut(repoId,commitId);
        Assert.assertEquals(false,result);

        /*
            当commit check out 返回值结果为successful
         */
        Mockito.when(restInterfaceManager.checkOut(repoId,commitId)).thenReturn(response);
        result = scanOperation.checkOut(repoId,commitId);
        Assert.assertEquals(true,result);
    }

    @Test
    public void initialScan() {
        /*
            预设 数据
         */
        String repoId = "repo1";
        String commitId = "comm";
        String category = "bug";
        String repoName = "repo";
        String repoPath = "path";
        Date commit_time = new Date(System.currentTimeMillis());
        JSONObject repoResponse = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("repo_name","repo");
        data.put("local_addr",repoPath);
        repoResponse.put("data",data);
        JSONObject commitResponse = new JSONObject();
        JSONObject commitData = new JSONObject();
        commitData.put("commit_time",commit_time);
        commitResponse.put("data",commitData);

        /*
            mock数据
         */
        Mockito.when(restInterfaceManager.getRepoById(repoId)).thenReturn(repoResponse);
        Mockito.when(restInterfaceManager.getCommitTime(commitId)).thenReturn(commitResponse);

        /*
            验证
         */
        ScanInitialInfo result = scanOperation.initialScan(repoId,commitId,category);
        Assert.assertEquals(repoName,result.getRepoName());
        Assert.assertEquals(repoId,result.getRepoId());
        Assert.assertEquals(repoPath,result.getRepoPath());
        Assert.assertEquals("doing...",result.getScan().getStatus());
    }

    /*
        未实现
     */
    @Test
    @Ignore
    public void doScan() {
    }

    /*
        mock后对验证的意义不大
     */
    @Test
    @Ignore
    public void mapping() {
    }

    @Test
    public void updateScan() {
        String repoId = "repo1";
        String repoParh = "path";
        String repoName = "repo";
        ScanInitialInfo scanInitialInfo = new ScanInitialInfo(scan,repoName,repoId,repoParh);

        Mockito.doNothing().when(scanDao).insertOneScan(scan);
        Boolean result = scanOperation.updateScan(scanInitialInfo);
        Assert.assertEquals("done",scan.getStatus());
        Assert.assertEquals(true,result);
        Mockito.verify(scanDao,Mockito.times(1)).insertOneScan(scan);
    }
}