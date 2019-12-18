package cn.edu.fudan.measureservice;

import cn.edu.fudan.measureservice.component.RestInterfaceManager;
import cn.edu.fudan.measureservice.domain.test.Commit;
import cn.edu.fudan.measureservice.mapper.RepoMeasureMapper;
import cn.edu.fudan.measureservice.service.MeasureService;
import cn.edu.fudan.measureservice.service.MeasureServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@SpringBootTest(classes = MeasureServiceApplication.class)
@TestPropertySource("classpath:application.properties")
@PowerMockIgnore({"javax.crypto.*","javax.management.*"})
public class MeasureServiceApplicationTests {

    @Autowired
    private RestInterfaceManager restInterfaceManager;

    @Autowired
    private RepoMeasureMapper repoMeasureMapper;

    @Autowired
    private MeasureServiceImpl measureService;

    @Test
    public void contextLoads() {
        System.out.println(restInterfaceManager.getRepoPath("29a3b12e-653f-11e9-9ddc-f93dfaa9da61",""));
    }



    @Test
    public void insertRepoMeasures() {
        String repoId = "4e21a81e-1efb-11ea-9fbe-11bdb9e75ae6";
        List<String> commitIds = new ArrayList<>();
        List<String> commitTimes = new ArrayList<>();
        List<String> developerNames = new ArrayList<>();
        List<String> developerEmails = new ArrayList<>();

        List<Commit> commits = repoMeasureMapper.getCommits(repoId);

        for(Commit commit: commits){
            measureService.saveMeasureData(commit.getRepo_id(),commit.getCommit_id(),commit.getCommit_time(),commit.getDeveloper(),commit.getDeveloper_email());
        }
        System.out.println(111);
    }



}
