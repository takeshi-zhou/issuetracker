package cn.edu.fudan.issueservice.controller;

import cn.edu.fudan.issueservice.domain.Issue;
import cn.edu.fudan.issueservice.domain.IssueParam;
import cn.edu.fudan.issueservice.domain.ResponseBean;
import cn.edu.fudan.issueservice.service.IssueService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@EnableAutoConfiguration
public class IssueController {


    private IssueService issueService;

    @Autowired
    public void setIssueService(IssueService issueService) {
        this.issueService = issueService;
    }


    @GetMapping(value = {"/issue"})
    public Object getIssues(@RequestParam("project_id") String project_id,
                            @RequestParam("page") Integer page,
                            @RequestParam("size") Integer size,
                            @RequestParam("category")String category) {
        return issueService.getIssueList(project_id, page, size,category);
    }

    @GetMapping(value={"issue/one-issue"})
    public Object getSpecificIssue(@RequestParam("issue_id") String issue_id){
        return issueService.getIssueByID(issue_id);
    }

    @GetMapping(value = {"/issue/issue-types"})
    public Object getExistIssueTypes(@RequestParam(name = "category",defaultValue = "bug")String category) {
        return issueService.getExistIssueTypes(category);
    }

    //参数超过三个不宜采用requestParameter、post请求（参数封装成一个类自动解析）、get+requestBody
    @PostMapping(value = {"/issue/filter"})
    public Object filterIssues(@RequestBody JSONObject requestParam) {
        return issueService.getFilteredIssueList(requestParam);
    }

    @GetMapping(value = {"/issue/dashboard"})
    public Object getDashBoardInfo(@RequestParam("duration") String duration,
                                   @RequestParam(name = "project_id", required = false) String project_id,
                                   @RequestParam(name="category",required = false,defaultValue = "bug")String category,
                                   HttpServletRequest request) {
        String userToken = request.getHeader("token");
        return issueService.getDashBoardInfo(duration, project_id, userToken,category);
    }

    @GetMapping(value = {"/issue/project-statistical-info"})
    public Object getAvgEliminatedTimeAndMaxAliveTime(@RequestParam(name = "project_id") String project_id,
                                                      @RequestParam(name="category",defaultValue = "bug")String category){
        return issueService.getAliveAndEliminatedInfo(project_id, category);
    }

    @GetMapping(value = {"/issue/statistical-results"})
    public Object getStatisticalResults(@RequestParam("month") Integer month,
                                        @RequestParam(name = "project_id", required = false) String project_id,
                                        @RequestParam(name="category",required = false,defaultValue = "bug")String category,
                                        HttpServletRequest request) {
        String userToken = request.getHeader("token");
        return issueService.getStatisticalResults(month, project_id, userToken,category);
    }

    @GetMapping(value = {"/issue/statistical-results-fix"})
    public Object getNewTrend(@RequestParam("month") Integer month,
                              @RequestParam(name = "project_id") String project_id,
                              @RequestParam(name="category",defaultValue = "bug")String category,
                              HttpServletRequest request) {
        String userToken = request.getHeader("token");
        try{
             return issueService.getNewTrend(month, project_id, userToken,category);
        }catch (Exception e){
            return new ResponseBean(500,e.getMessage(),null);
        }
    }

    @PostMapping("/issue/specific-issues")
    public Object getSpecificIssues(@RequestBody IssueParam issueParam,HttpServletRequest request){
        String userToken = request.getHeader("token");
        return issueService.getSpecificIssues(issueParam,userToken);
    }

    // 修改 Issue 的 优先级（priority）
    @PutMapping(value = "/issue/priority/{issue-id}")
    public Object updatePriority(@PathVariable("issue-id")String issueId, @RequestParam ("priority")String priority) {
        try {
            issueService.updatePriority(issueId,priority);
            return new ResponseBean(200, "issues update priority success!", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseBean(401, "issues update priority failed!", null);
        }
    }


    /**
     * 下面都是供其他服务调用的内部接口
     * */
    @PostMapping(value = {"/inner/issue"})
    public Object addIssues(@RequestBody List<Issue> issueList) {
        try {
            issueService.insertIssueList(issueList);
            return new ResponseBean(200, "issues add success!", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseBean(401, "issues add failed!", null);
        }
    }

    @DeleteMapping(value = {"/inner/issue/{category}/{repoId}"})
    public Object deleteIssues(@PathVariable("category")String category,@PathVariable("repoId") String repoId) {
        try {
            issueService.deleteIssueByRepoIdAndCategory(repoId,category);
            return new ResponseBean(200, "issues delete success!", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseBean(401, "issues delete failed!", null);
        }
    }

    @PutMapping(value = {"/inner/issue"})
    public Object updateIssues(@RequestBody List<Issue> issueList) {
        try {
            issueService.batchUpdateIssue(issueList);
            return new ResponseBean(200, "issues update success!", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseBean(401, "issues update failed!", null);
        }
    }

    @PostMapping(value = {"/inner/issue/mapping"})
    public Object mapping(@RequestBody JSONObject requestParam) {
        try {
            String repo_id = requestParam.getString("repo_id");
            String pre_commit_id = requestParam.getString("pre_commit_id");
            String current_commit_id = requestParam.getString("current_commit_id");
            String category=requestParam.getString("category");
            issueService.startMapping(repo_id, pre_commit_id, current_commit_id,category);
            return new ResponseBean(200, "issues mapping success!", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseBean(401, "issues mapping failed!", null);
        }
    }

    @GetMapping(value = "/inner/issue/dashboard")
    public Object updateIssueCount(@RequestParam ("time") String time){
        try {
            issueService.updateIssueCount(time);
            return new ResponseBean(200, "update success!", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseBean(401, " update failed!", null);
        }
    }

    @DeleteMapping(value = "/inner/issue/scan-results/{category}/{repo-id}")
    public Object deleteScanResults(@PathVariable("repo-id")String repoId,@PathVariable("category")String category){
        try {
            issueService.deleteScanResultsByRepoIdAndCategory(repoId, category);
            return new ResponseBean(200, "delete success!", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseBean(401, " delete failed!", null);
        }
    }

    @PostMapping(value = "/inner/issue/priority")
    @SuppressWarnings("unchecked")
    public Object batchUpdateIssueListPriority(@RequestBody JSONObject requestParam) {
        try {
            List<String> issueUuid = requestParam.getObject( "list",List.class);
            issueService.batchUpdateIssueListPriority(issueUuid, requestParam.getInteger("priority"));
            return new ResponseBean(200, "success!", null);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseBean(401, "failed!", null);
        }
    }

    @GetMapping(value = "/inner/issue/uuid")
    public List<String> getIssueListByTypeAndRepoId(@RequestParam("repo-id") String repoId,@RequestParam("type") String type) {
        return issueService.getNotSolvedIssueListByTypeAndRepoId(repoId, type);
    }

    @GetMapping(value = "/inner/issue/counts")
    public Object getIssueTypeCountsByCategoryAndRepoId(@RequestParam("repo-id") String repoId,@RequestParam("category") String category) {
        return issueService.getNotSolvedIssueCountByCategoryAndRepoId(repoId, category);
    }
}
