package cn.edu.fudan.issueservice.controller;

import cn.edu.fudan.issueservice.domain.Issue;
import cn.edu.fudan.issueservice.domain.ResponseBean;
import cn.edu.fudan.issueservice.service.IssueService;
import cn.edu.fudan.issueservice.service.RawIssueService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/issue")
public class IssueController {


    private IssueService issueService;

    private RawIssueService rawIssueService;

    @Autowired
    public void setRawIssueService(RawIssueService rawIssueService) {
        this.rawIssueService = rawIssueService;
    }

    @Autowired
    public void setIssueService(IssueService issueService) {
        this.issueService = issueService;
    }

    /**
     *  project_id
     *  page
     *  size
     * */
    @GetMapping
    public Object getIssues(@RequestParam("project-id") Object project_id,
                            @RequestParam("page") Object page,
                            @RequestParam("size") Object size){
        Map<String,Object>requestParam = new HashMap<>();
        requestParam.put("project_id",project_id);
        requestParam.put("page",page);
        requestParam.put("size",size);
    	return issueService.getIssueList(requestParam);
    }

    @PostMapping
    public Object addIssues(@RequestBody List<Issue> issueList){
        try{
          issueService.insertIssueList(issueList);
          return new ResponseBean(200,"issues add success!",null);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"issues add failed!",null);
        }
    }

    @DeleteMapping(value = {"/{projectId}"})
    public Object deleteIssues(@PathVariable("projectId")String projectId){
        try{
            issueService.deleteIssueByProjectId(projectId);
            return new ResponseBean(200,"issues delete success!",null);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"issues delete failed!",null);
        }
    }

    @PutMapping
    public Object updateIssues(@RequestBody List<Issue> issueList){
        try{
            issueService.batchUpdateIssue(issueList);
            return new ResponseBean(200,"issues update success!",null);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"issues update failed!",null);
        }
    }

    @PostMapping(value = {"/mapping"})
    public Object mapping(@RequestBody JSONObject requestParam){
        try{
            String project_id=requestParam.getString("project_id");
            String pre_commit_id=requestParam.getString("pre_commit_id");
            String current_commit_id=requestParam.getString("current_commit_id");
            issueService.startMapping(project_id,pre_commit_id,current_commit_id);
            return new ResponseBean(200,"issues mapping success!",null);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"issues mapping failed!",null);
        }
    }

    @GetMapping(value = {"/raw-issue"})
    public Object getRawIssueList(@RequestParam("issue_id")String issue_id) {
        return rawIssueService.getRawIssueByIssueId(issue_id);
    }
    
}
