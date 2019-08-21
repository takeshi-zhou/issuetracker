package cn.edu.fudan.cloneservice.service.impl;

import cn.edu.fudan.cloneservice.service.CloneMeasureService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * Created by njzhan
 * <p>
 * Date :2019-08-20
 * <p>
 * Description :
 * <p>
 * Version :1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CloneMeasureServiceImplTest {
    String repo_id = "4f696ccc-65ef-11e9-9ddc-f93dfaa9da61";
    String commit_id = "03f353e39d23c71825405acd22db94428323997b";

    @Autowired
    CloneMeasureService cloneMeasureService;
    @Test
    public void getRepoMeasureCloneDataByRepoIdCommitId() {
    }

    @Test
    public void getDeveloperMeasureCloneDataByRepoIdCommitId() {
        cloneMeasureService.getDeveloperMeasureCloneDataByRepoIdCommitId(repo_id, commit_id, "happy");
    }
}