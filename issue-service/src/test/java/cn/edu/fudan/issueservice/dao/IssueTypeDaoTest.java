package cn.edu.fudan.issueservice.dao;

import cn.edu.fudan.issueservice.IssueServiceApplicationTests;
import cn.edu.fudan.issueservice.domain.IssueType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class IssueTypeDaoTest extends IssueServiceApplicationTests {

    @Autowired
    IssueTypeDao issueTypeDao;

    @Test
    public void getIssueTypeByTypeName() {
        IssueType issueType = issueTypeDao.getIssueTypeByTypeName("NP_CLOSING_NULL");
        System.out.println(issueType);
    }
}