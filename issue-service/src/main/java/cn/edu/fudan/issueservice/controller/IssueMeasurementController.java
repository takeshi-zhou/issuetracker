/**
 * @description: 代码度量--Issue数量相关controller
 * @author: fancying
 * @create: 2019-04-08 16:55
 **/
package cn.edu.fudan.issueservice.controller;

import cn.edu.fudan.issueservice.domain.ResponseBean;
import cn.edu.fudan.issueservice.service.IssueMeasureInfoService;
import cn.edu.fudan.issueservice.service.IssueRankService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

@RestController
@EnableAutoConfiguration
public class IssueMeasurementController {

    private IssueMeasureInfoService issueMeasureInfoService;
    private IssueRankService issueRankService;

    @Autowired
    public void setIssueMeasureInfoService(IssueMeasureInfoService issueMeasureInfoService) {
        this.issueMeasureInfoService = issueMeasureInfoService;
    }

    @Autowired
    public void setIssueRankService(IssueRankService issueRankService) {
        this.issueRankService = issueRankService;
    }

    /**
     * quantity
     * */


    @GetMapping(value = {"/measurement/code-quality"})
    public Object getCodeQuality(@RequestParam(value = "developer",required = false) String developer,
                                 @RequestParam(value = "timeGranularity", required = false) String timeGranularity,
                                 @RequestParam(value = "since",required = false) String since,
                                 @RequestParam(value = "until",required = false) String until,
                                 @RequestParam(value = "repoId") String repoId,
                                 @RequestParam(value = "tool",required = false) String tool,
                                 @RequestParam(value = "page",required = false,defaultValue = "0") int page,
                                 @RequestParam(value = "ps",required = false,defaultValue = "0") int ps) {
        try {
            return new ResponseBean(200, "success!", issueMeasureInfoService.getQualityChangesByCondition(developer,timeGranularity,since,until,repoId,tool,page,ps) );
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseBean(401, "failed!", e.getMessage());
        }
    }






    // spaceType : developer project file package
    //detail : developerId fileName packageName
    @GetMapping(value = {"/measurement/remainingIssue/{repoId}/{commit}"})
    public Object getNumberOfRemainingIssue(@PathVariable("repoId") String repoId, @PathVariable("commit") String commit,
                                            @RequestParam("spaceType") String spaceType,@RequestParam(value = "detail", required = false) String detail) {
        return issueMeasureInfoService.numberOfRemainingIssue(repoId, commit, spaceType, detail);
    }

    @GetMapping(value = {"/measurement/newIssue/{repoId}/{commit}"})
    public Object getNumberOfNewIssueByCommit(@PathVariable("repoId") String repoId,@PathVariable("commit") String commit,
                                            @RequestParam("spaceType") String spaceType,@RequestParam("category") String category) {
        return issueMeasureInfoService.numberOfNewIssueByCommit(repoId, commit, spaceType,category);
    }


    @GetMapping(value = {"/measurement/newIssue"})
    public Object getNumberOfNewIssue(@RequestParam("duration") String duration,
                                      @RequestParam("spaceType") String spaceType,@RequestParam("detail") String detail) {

        return issueMeasureInfoService.numberOfNewIssue(duration, spaceType, detail);
    }

    @GetMapping(value = {"/measurement/eliminateIssue/{repoId}/{commit}"})
    public Object getNumberOfEliminateIssueByCommit(@PathVariable("repoId") String repoId,@PathVariable("commit") String commit,
                                              @RequestParam("spaceType") String spaceType,@RequestParam("category") String category) {
        return issueMeasureInfoService.numberOfEliminateIssueByCommit(repoId, commit, spaceType,category);
    }

    @GetMapping(value = {"/measurement/eliminateIssue"})
    public Object getNumberOfEliminateIssue(@RequestParam("duration") String duration,
                @RequestParam("spaceType") String spaceType,@RequestParam("detail") String detail) {

        return issueMeasureInfoService.numberOfEliminateIssue(duration, spaceType, detail);
    }

    @GetMapping(value = {"/measurement/issue/commit"})
    public ResponseBean getIssueCountEachCommit(@RequestParam("repo_id")String repo_id,
                                                      @RequestParam("category")String category,
                                                      @RequestParam("since")String since,
                                                      @RequestParam("until")String until){
        try {
            return new ResponseBean(200, "success!", issueMeasureInfoService.getIssueCountEachCommit(repo_id, category, since, until));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseBean(401, "failed!", Collections.emptyList());
        }
    }

    @GetMapping(value = {"/measurement/issue/repository"})
    public ResponseBean getIssueCountMeasureByRepo(@RequestParam("repo_id")String repo_id,
                                                        @RequestParam("category")String category,
                                                        @RequestParam("since")String since,
                                                        @RequestParam("until")String until){
        try {
            return new ResponseBean(200, "success!", issueMeasureInfoService.getIssueCountMeasureByRepo(repo_id, category, since, until));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseBean(401, "failed!", null);
        }
    }

    @GetMapping(value = {"/measurement/issue/developer"})
    public ResponseBean getIssueCountMeasureByDeveloper(@RequestParam("repo_id")String repo_id,
                                                        @RequestParam("category")String category,
                                                        @RequestParam("since")String since,
                                                        @RequestParam("until")String until){
        try {
            return new ResponseBean(200, "success!", issueMeasureInfoService.getIssueCountMeasureByDeveloper(repo_id, category, since, until));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseBean(401, "failed!", Collections.emptyList());
        }
    }




    /**
     *  rank
     * */
    @GetMapping(value = {"/measurement/rankOfFileBaseDeveloperQuantity/{repoId}"})
    public Object getRankOfFileBaseDeveloperQuantity(@PathVariable("repoId") String repoId, @RequestParam("duration") String duration,
                                                     @RequestParam("spaceType") String spaceType,@RequestParam("detail") String detail) {

        return issueRankService.rankOfFileBaseDeveloperQuantity(repoId, duration, spaceType, detail);
    }

    @GetMapping(value = {"/measurement/rankOfFileBaseIssueQuantity/{repoId}"})
    public Object getRankOfFileBaseIssueQuantity(@PathVariable("repoId") String repoId, @RequestParam("commitId") String commitId) {

        return issueRankService.rankOfFileBaseIssueQuantity(repoId, commitId);
    }

    // 平均每行产生多少个bug
    @GetMapping(value = {"/measurement/rankOfFileBaseDensity/{repoId}"})
    public Object getRankOfFileBaseDensity(@PathVariable("repoId") String repoId, @RequestParam("commitId") String commitId) {

        return issueRankService.rankOfFileBaseDensity(repoId, commitId);
    }

    @GetMapping(value = {"/measurement/rankOfDeveloper/{repoId}"})
    public Object getRankOfDeveloper(@PathVariable("repoId") String repoId,
                                     @RequestParam("since") String since,
                                     @RequestParam("until")String until) {

        return issueRankService.rankOfDeveloper(repoId, since,until);
    }

    @GetMapping(value = {"/measurement/rankOfRepoBaseDensity"})
    public Object getRankOfRepoBaseDensity(HttpServletRequest request) {
        String userToken = request.getHeader("token");
        return issueRankService.rankOfRepoBaseDensity(userToken);
    }


    @GetMapping(value = "/measurement/issueTypeCounts")
    public Object getIssueTypeCountsByCategoryAndRepoId(@RequestParam("repoId") String repoId,@RequestParam("category") String category,@RequestParam(value="commitId",required = false) String commitId) {
        return issueMeasureInfoService.getNotSolvedIssueCountByCategoryAndRepoId(repoId, category,commitId);
    }


}