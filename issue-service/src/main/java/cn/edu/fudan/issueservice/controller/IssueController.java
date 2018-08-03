package cn.edu.fudan.issueservice.controller;

import cn.edu.fudan.issueservice.domain.Issue;
import cn.edu.fudan.issueservice.domain.ResponseBean;
import cn.edu.fudan.issueservice.service.IssueService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Issue")
public class IssueController {


    private IssueService issueService;

    @Autowired
    public void setIssueService(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping(value={"/issueList"})
    public Object getIssues(@RequestBody Map<String,Object>requestParam){
    	return issueService.getIssueList(requestParam);
    }

    @PostMapping(value = {"/addIssues"})
    public Object addIssues(@RequestBody List<Issue> issueList){
        try{
          issueService.insertIssueList(issueList);
          return new ResponseBean(200,"issues add success!",null);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"issues add failed!",null);
        }
    }

    @DeleteMapping(value = {"/delete/{projectId}"})
    public Object deleteIssues(@PathVariable("projectId")String projectId){
        try{
            issueService.deleteIssueByProjectId(projectId);
            return new ResponseBean(200,"issues delete success!",null);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"issues delete failed!",null);
        }
    }

    @PostMapping(value={"/updateIsues"})
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
    
    
}
