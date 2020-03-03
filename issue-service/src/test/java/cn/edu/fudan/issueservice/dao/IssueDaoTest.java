package cn.edu.fudan.issueservice.dao;

import cn.edu.fudan.issueservice.IssueServiceApplicationTests;
import cn.edu.fudan.issueservice.domain.Issue;
import cn.edu.fudan.issueservice.domain.Scanner;
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

    @Test
    public void getSonarIssueByRepoId() {
        String repoId="75e8622c-6fef-11e9-b723-0f92b2ad63bf";
        String category = Scanner.SONAR.getType();
        List<Issue> issues = issueMapper.getSonarIssueByRepoId(repoId, category);
        System.out.println(1+1);
    }

    @Test
    public void getIssuesByIssueIds() {
        List<String> issueIds = new ArrayList<>();
        issueIds.add("000fb38a-1df0-4837-95c4-522febd6f22a");
        issueIds.add("001283d7-9e47-4818-aaf7-c1d5be6f0257");
        String category = Scanner.SONAR.getType();
        List<Issue> issues = issueMapper.getIssuesByIssueIds(issueIds);
        System.out.println(1+1);
    }

    @Test
    public void getHaveNotAdoptEliminateIssuesByCategoryAndRepoId() {

        List<Issue> issues = issueMapper.getHaveNotAdoptEliminateIssuesByCategoryAndRepoId("294b6600-56b0-11ea-807d-2b3ae82c4553","bug");
        System.out.println(1+1);
    }

}
