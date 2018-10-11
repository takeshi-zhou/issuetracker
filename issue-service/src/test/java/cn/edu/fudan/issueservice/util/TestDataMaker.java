package cn.edu.fudan.issueservice.util;

import cn.edu.fudan.issueservice.domain.Issue;

public class TestDataMaker {
    public Issue issueMaker1(){
        Issue issue = new Issue();
        issue.setUuid("iss1");
        issue.setType("OBL_UNSATISFIED_OBLIGATION_EXCEPTION_EDGE");
        issue.setStart_commit("badfd40225cb3208106ec30c403928fd55f84886");
        issue.setEnd_commit("badfd40225cb3208106ec30c403928fd55f84886");
        issue.setRaw_issue_start("10cfe678-8606-41cb-abf4-fda25292cc2b");
        issue.setRaw_issue_end("10cfe678-8606-41cb-abf4-fda25292cc2b");
        issue.setRepo_id("repo_id");
        issue.setTarget_files("DatabaseTool.java");
        return issue;
    }

    public Issue issueMaker2(){
        Issue issue = new Issue();
        issue.setUuid("iss2");
        issue.setType("OBL_UNSATISFIED_OBLIGATION_EXCEPTION_EDGE");
        issue.setStart_commit("badfd40225cb3208106ec30c403928fd55f84886");
        issue.setEnd_commit("badfd40225cb3208106ec30c403928fd55f84886");
        issue.setRaw_issue_start("c5b30c01-86bd-4a66-a828-0d78688fb005");
        issue.setRaw_issue_end("c5b30c01-86bd-4a66-a828-0d78688fb005");
        issue.setRepo_id("repo_id");
        issue.setTarget_files("DatabaseTool.java");
        return issue;
    }
}
