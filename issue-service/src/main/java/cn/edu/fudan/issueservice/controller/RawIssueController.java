package cn.edu.fudan.issueservice.controller;

import cn.edu.fudan.issueservice.domain.RawIssue;
import cn.edu.fudan.issueservice.domain.ResponseBean;
import cn.edu.fudan.issueservice.service.RawIssueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@EnableAutoConfiguration
public class RawIssueController {

    private RawIssueService rawIssueService;

    @Autowired
    public void setRawIssueService(RawIssueService rawIssueService) {
        this.rawIssueService = rawIssueService;
    }

    @GetMapping(value = {"/raw-issue"})
    public Object getRawIssueList(@RequestParam("issue_id") String issue_id) {
        return rawIssueService.getRawIssueByIssueId(issue_id);
    }

    @GetMapping(value = {"/raw-issueList"})
    public Object getOnePageRawIssueList(
                        @RequestParam("issue_id") String issue_id ,
                        @RequestParam("page") Integer page,
                        @RequestParam("size") Integer size
                        ) {
        return rawIssueService.getRawIssueList(issue_id,page,size);
    }

    @GetMapping(value = {"/raw-issue/code"})
    public Object getCode(@RequestParam("project_id") String project_id,
                          @RequestParam("commit_id") String commit_id,
                          @RequestParam("file_path") String file_path) {
        //需要添加全部的code内容
        return rawIssueService.getCode(project_id, commit_id, file_path);
    }

    //下面的接口都是其他服务调用的内部接口

    @PostMapping(value = {"/inner/raw-issue"})
    public Object addRawIssues(@RequestBody List<RawIssue> list) {
        try {
            rawIssueService.insertRawIssueList(list);
            return new ResponseBean(200, "rawIssue add success!", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseBean(401, "rawIssue add failed!", null);
        }
    }

    @DeleteMapping(value = {"/inner/raw-issue/{category}/{repoId}"})
    public Object deleteRawIssue(@PathVariable("category")String category ,@PathVariable("repoId") String repoId) {
        try {
            rawIssueService.deleteRawIssueByRepoIdAndCategory(repoId,category);
            return new ResponseBean(200, "rawIssue delete success!", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseBean(401, "rawIssue delete failed!", null);
        }
    }

    @PutMapping(value = {"/inner/raw-issue"})
    public Object updateRawIssues(@RequestBody List<RawIssue> issueList) {
        try {
            rawIssueService.batchUpdateIssueId(issueList);
            return new ResponseBean(200, "rawIssue update success!", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseBean(401, "rawIssue update failed!", null);
        }
    }

    @GetMapping(value = {"/inner/raw-issue/list-by-commit"})
    public Object getRawIssues(@RequestParam("repo_id")String repo_id,
                               @RequestParam("commit_id") String commit_id,
                               @RequestParam("category")String category) {
        return rawIssueService.getRawIssueByCommitIDAndCategory(repo_id,commit_id,category);
    }

    @GetMapping(value = "/inner/raw-issue/locations")
    @CrossOrigin
    public Object getRawIssueByCommitAndFile(@RequestParam("repo_id")String repo_id,
                                             @RequestParam("commit_id") String commit_id,
                                             @RequestParam("category")String category,
                                             @RequestParam("file_path")String file_path){
        return rawIssueService.getRawIssueByCommitAndFile(repo_id,commit_id, category, file_path);
    }
}
