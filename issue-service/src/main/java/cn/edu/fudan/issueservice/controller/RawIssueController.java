package cn.edu.fudan.issueservice.controller;

import cn.edu.fudan.issueservice.domain.RawIssue;
import cn.edu.fudan.issueservice.domain.ResponseBean;
import cn.edu.fudan.issueservice.service.RawIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/raw-issue")
public class RawIssueController {


    private RawIssueService rawIssueService;

    @Autowired
    public void setRawIssueService(RawIssueService rawIssueService) {
        this.rawIssueService = rawIssueService;
    }

    @PostMapping
    public Object addRawIssues(@RequestBody List<RawIssue> list){
        try{
            rawIssueService.insertRawIssueList(list);
            return new ResponseBean(200,"rawIssue add success!",null);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"rawIssue add failed!",null);
        }
    }

    @DeleteMapping(value={"/{projectId}"})
    public Object deleteRawIssue(@PathVariable("projectId")String projectId){
        try{
            rawIssueService.deleteRawIssueByProjectId(projectId);
            return new ResponseBean(200,"rawIssue delete success!",null);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"rawIssue delete failed!",null);
        }
    }

    @PutMapping
    public Object updateRawIssues(@RequestBody List<RawIssue> issueList){
        try{
            rawIssueService.batchUpdateIssueId(issueList);
            return new ResponseBean(200,"rawIssue update success!",null);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"rawIssue update failed!",null);
        }
    }

    @GetMapping(value = {"/list-by-issue"})
    public Object getRawIssueList(@RequestParam("issue_id")String issue_id) {
        return rawIssueService.getRawIssueByIssueId(issue_id);
    }

    // need or not
    @GetMapping(value = {"/list-by-commit"})
    public Object getRawIssues(@RequestParam("commit_id")String commit_id){
        return rawIssueService.getRawIssueByCommitID(commit_id);
    }
}
