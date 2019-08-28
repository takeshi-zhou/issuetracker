package cn.edu.fudan.cloneservice.dao;

import cn.edu.fudan.cloneservice.domain.Commit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by njzhan
 * <p>
 * Date :2019-08-27
 * <p>
 * Description :
 * <p>
 * Version :1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CommitDaoTest {
    @Autowired
    CommitDao commitDao;

    @Test
    public void getScannedCommmit() {
        String repo_id = "14209e64-9197-11e9-be7f-7f225df07fc5";
        String since = "2014-01-01";
        String until  = "2019-01-01";
        List<Commit> lc =  commitDao.getScannedCommmit(repo_id, since, until);
        System.out.println(lc.get(0).getCommit_time());
        System.out.println(lc.get(lc.size() - 1).getCommit_time());
    }
}