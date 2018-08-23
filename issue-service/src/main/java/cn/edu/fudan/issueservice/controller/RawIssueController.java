package cn.edu.fudan.issueservice.controller;

import cn.edu.fudan.issueservice.domain.RawIssue;
import cn.edu.fudan.issueservice.domain.ResponseBean;
import cn.edu.fudan.issueservice.service.RawIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RawIssueController {


    private RawIssueService rawIssueService;

    @Autowired
    public void setRawIssueService(RawIssueService rawIssueService) {
        this.rawIssueService = rawIssueService;
    }

    @GetMapping(value = {"/raw-issue"})
    public Object getRawIssueList(@RequestParam("issue_id")String issue_id) {
        return rawIssueService.getRawIssueByIssueId(issue_id);
    }

    @GetMapping(value={"/raw-issue/code"})
    public Object getLocationList(@RequestParam("raw_issue_id")String raw_issue_id){
        //需要添加全部的code内容
        return rawIssueService.getLocationsByRawIssueId(raw_issue_id);
    }

    //下面的接口都是其他服务调用的内部接口

    @PostMapping(value = {"/inner/raw-issue"})
    public Object addRawIssues(@RequestBody List<RawIssue> list){
        try{
            rawIssueService.insertRawIssueList(list);
            return new ResponseBean(200,"rawIssue add success!",null);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"rawIssue add failed!",null);
        }
    }

    @DeleteMapping(value={"/inner/raw-issue/{projectId}"})
    public Object deleteRawIssue(@PathVariable("projectId")String projectId){
        try{
            rawIssueService.deleteRawIssueByProjectId(projectId);
            return new ResponseBean(200,"rawIssue delete success!",null);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"rawIssue delete failed!",null);
        }
    }

    @PutMapping(value={"/inner/raw-issue"})
    public Object updateRawIssues(@RequestBody List<RawIssue> issueList){
        try{
            rawIssueService.batchUpdateIssueId(issueList);
            return new ResponseBean(200,"rawIssue update success!",null);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"rawIssue update failed!",null);
        }
    }

    @GetMapping(value = {"/inner/raw-issue/list-by-commit"})
    public Object getRawIssues(@RequestParam("commit_id")String commit_id){
        return rawIssueService.getRawIssueByCommitID(commit_id);
    }
}
