package cn.edu.fudan.cloneservice.service.impl;

import cn.edu.fudan.cloneservice.service.CloneMeasureService;
import org.eclipse.jgit.lib.PersonIdent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

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
    String repo_id = "14209e64-9197-11e9-be7f-7f225df07fc5";
    String commit_id = "b927741b28aa9eb4f919df2e0b6a8dafb99f67ea";

    @Autowired
    CloneMeasureService cloneMeasureService;
    @Test
    public void getRepoMeasureCloneDataByRepoIdCommitId() {

    }

    @Test
    public void getDeveloperMeasureCloneDataByRepoIdCommitId() {
        cloneMeasureService.getDeveloperMeasureCloneDataByRepoIdCommitId(repo_id, commit_id, "happy");
    }

    @Test
    public void getDeveloperList(){
        Set<PersonIdent> set = cloneMeasureService.getDeveloperListByRepoId(repo_id, commit_id);
        for (PersonIdent personIdent: set){
            System.out.println(personIdent.getName());
        }
    }
}