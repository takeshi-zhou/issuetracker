package cn.edu.fudan.issueservice.service;

import cn.edu.fudan.issueservice.IssueServiceApplicationTests;
import cn.edu.fudan.issueservice.domain.Issue;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class IssueServiceTest extends IssueServiceApplicationTests {

    @Autowired
    IssueService issueService;

    List<Issue> list ;

    Issue issue1;

    Issue issue2;

    Map<String, Object> map;

    @Before
    public void setup() throws Exception {
        issue1 = new Issue();
        issue1.setUuid("334");
        issue1.setType("OBL_UNSATISFIED_OBLIGATION_EXCEPTION_EDGE");
        issue1.setStart_commit("badfd40225cb3208106ec30c403928fd55f84886");
        issue1.setEnd_commit("badfd40225cb3208106ec30c403928fd55f84886");
        issue1.setRaw_issue_start("10cfe678-8606-41cb-abf4-fda25292cc2b");
        issue1.setRaw_issue_end("10cfe678-8606-41cb-abf4-fda25292cc2b");
        issue1.setProject_id("222");
        issue1.setTarget_files("DatabaseTool.java");

        issue2 = new Issue();
        issue2.setUuid("333");
        issue2.setType("OBL_UNSATISFIED_OBLIGATION_EXCEPTION_EDGE");
        issue2.setStart_commit("badfd40225cb3208106ec30c403928fd55f84886");
        issue2.setEnd_commit("badfd40225cb3208106ec30c403928fd55f84886");
        issue2.setRaw_issue_start("c5b30c01-86bd-4a66-a828-0d78688fb005");
        issue2.setRaw_issue_end("c5b30c01-86bd-4a66-a828-0d78688fb005");
        issue2.setProject_id("222");
        issue2.setTarget_files("DatabaseTool.java");

        list = new ArrayList<>();
        list.add(issue1);
        list.add(issue2);


        map = new HashMap<String, Object>();
        map.put("project_id","222");
        map.put("type","OBL_UNSATISFIED_OBLIGATION_EXCEPTION_EDGE");
        map.put("start",0);
        map.put("size",2);
        map.put("page",1);

    }

    @Test
    @Transactional
    public void insertIssueList() {
        issueService.insertIssueList(list);
    }

    @Test
    @Transactional
    public void deleteIssueByProjectId() {
        issueService.deleteIssueByProjectId("222");
    }

    @Test
    @Transactional
    public void batchUpdateIssue() {
        issueService.batchUpdateIssue(list);
    }

    @Test
    public void getIssueByID() {
        Issue issue =  issueService.getIssueByID("333");
        System.out.println(issue.getUuid());
    }

    @Test
    public void getIssueList() {
        Map<String,Object> result = (Map<String,Object>)issueService.getIssueList("9151ecba-e749-4a14-b6e3-f3a1388139ec",1,2);
        System.out.println(result.toString());
    }

    /*
        等scan单元测试之后补充
     */
    @Test
    public void startMapping() {

    }
}