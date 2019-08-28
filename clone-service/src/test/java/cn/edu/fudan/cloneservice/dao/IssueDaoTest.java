package cn.edu.fudan.cloneservice.dao;

import cn.edu.fudan.cloneservice.domain.Issue;
import cn.edu.fudan.cloneservice.mapper.IssueMapper;
import org.hamcrest.core.Is;
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
 * Date :2019-08-26
 * <p>
 * Description :
 * <p>
 * Version :1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class IssueDaoTest {
    @Autowired
    private IssueDao issueDao;


    @Test
    public void testGetIssueList(){
        String repo_id = "14209e64-9197-11e9-be7f-7f225df07fc5";
        String start = "2017-01-31";
        String end = "2019-01-18";
        List<Issue> li = issueDao.getIssueByDuration(repo_id, start, end);
//        Issue issue = issueDao.getOneIssueByDuration(repo_id, start, end);
        System.out.println(li.size());
//        System.out.println();
    }
}