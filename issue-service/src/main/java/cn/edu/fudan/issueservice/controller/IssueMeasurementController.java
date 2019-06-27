/**
 * @description: 代码度量--Issue数量相关controller
 * @author: fancying
 * @create: 2019-04-08 16:55
 **/
package cn.edu.fudan.issueservice.controller;

import cn.edu.fudan.issueservice.domain.ResponseBean;
import cn.edu.fudan.issueservice.service.IssueMeasureInfoService;
import cn.edu.fudan.issueservice.service.IssueRankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    // spaceType : developer project file package
    //detail : developerId fileName packageName
    @GetMapping(value = {"/measurement/remainingIssue/{repoId}/{commit}"})
    public Object getNumberOfRemainingIssue(@PathVariable("repoId") String repoId, @PathVariable("commit") String commit,
                                            @RequestParam("spaceType") String spaceType,@RequestParam(value = "detail", required = false) String detail) {
        return issueMeasureInfoService.numberOfRemainingIssue(repoId, commit, spaceType, detail);
    }

    @GetMapping(value = {"/measurement/newIssue"})
    public Object getNumberOfNewIssue(@RequestParam("duration") String duration,
                                      @RequestParam("spaceType") String spaceType,@RequestParam("detail") String detail) {

        return issueMeasureInfoService.numberOfNewIssue(duration, spaceType, detail);
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



}