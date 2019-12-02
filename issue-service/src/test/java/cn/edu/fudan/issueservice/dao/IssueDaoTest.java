package cn.edu.fudan.issueservice.dao;

import cn.edu.fudan.issueservice.IssueServiceApplicationTests;
import cn.edu.fudan.issueservice.domain.Issue;
import cn.edu.fudan.issueservice.mapper.IssueMapper;
import cn.edu.fudan.issueservice.util.TestDataMaker;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IssueDaoTest extends IssueServiceApplicationTests {


    private IssueMapper issueMapper;

    @Autowired
    public void setIssueMapper(IssueMapper issueMapper) {
        this.issueMapper = issueMapper;
    }

    @Test
    public void batchUpdateSonarIssuesTest() {
        TestDataMaker testDataMaker = new TestDataMaker();
        List<Issue> issues = new ArrayList<>();
        issues.add(testDataMaker.issueMaker1());
        issues.add(testDataMaker.issueMaker2());
//        issueMapper.insertIssueList(issues);
        Issue issue1 = issues.get(0);
        issue1.setStatus("test");
        issue1.setEnd_commit("end1");
        issue1.setEnd_commit_date(new Date());
        issue1.setUpdate_time(new Date());


        Issue issue2 = issues.get(1);
        issue2.setEnd_commit("end1");
        issue2.setEnd_commit_date(new Date());
        issue2.setStatus("test1");
        issue2.setResolution("FIXED");
        issue2.setUpdate_time(new Date());
        issueMapper.batchUpdateSonarIssues(issues);
    }
}
