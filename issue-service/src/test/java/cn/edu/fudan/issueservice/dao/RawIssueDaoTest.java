package cn.edu.fudan.issueservice.dao;

import cn.edu.fudan.issueservice.IssueServiceApplicationTests;
import cn.edu.fudan.issueservice.domain.RawIssue;
import cn.edu.fudan.issueservice.mapper.RawIssueMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RawIssueDaoTest  extends IssueServiceApplicationTests {

    private RawIssueMapper rawIssueMapper;

    @Autowired
    public void setIssueMapper(RawIssueMapper rawIssueMapper) {
        this.rawIssueMapper = rawIssueMapper;
    }



    @Test
    public void getChangedRawIssues() {
        String issueId = "bdec3773-fc79-4d7c-ba86-b6fbcc33b81e";
        List<RawIssue> issues = rawIssueMapper.getChangedRawIssues(issueId);
        System.out.println(1+1);
    }
}
