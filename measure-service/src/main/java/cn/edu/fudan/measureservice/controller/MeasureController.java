package cn.edu.fudan.measureservice.controller;

import cn.edu.fudan.measureservice.domain.Granularity;
import cn.edu.fudan.measureservice.domain.ResponseBean;
import cn.edu.fudan.measureservice.service.MeasureService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class MeasureController {


    private MeasureService measureService;

    public MeasureController(MeasureService measureService) {
        this.measureService = measureService;
    }

    @GetMapping("/measure/repository")
    @CrossOrigin
    public ResponseBean getMeasureDataByRepoId(@RequestParam("repo_id")String repo_id,
                                               @RequestParam("since")String since,
                                               @RequestParam("until")String until,
                                               @RequestParam("granularity") Granularity granularity){
        try{
            return new ResponseBean(200,"success",measureService.getRepoMeasureByRepoId(repo_id,since,until,granularity));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }
    }

    @GetMapping("/measure/module")
    @CrossOrigin
    public ResponseBean getModuleMeasure(@RequestParam("repo_id")String repo_id,
                                         @RequestParam("commit_id")String commit_id){
        try{
            return new ResponseBean(200,"success",measureService.getPackageMeasureUnderSpecificCommit(repo_id,commit_id));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }
    }

    @GetMapping("/measure/active")
    @CrossOrigin
    public ResponseBean getActiveMeasure(@RequestParam("repo_id")String repo_id,
                                         @RequestParam("since")String since,
                                         @RequestParam("until")String until){
        try{
            return new ResponseBean(200,"success",measureService.getOneActiveMeasure(repo_id, since, until));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }
    }

    @GetMapping("/measure/repo-rank")
    @CrossOrigin
    public ResponseBean getMeasureData(@RequestParam("since")String since,
                                       @RequestParam("until")String until,
                                       HttpServletRequest request){
        try{
            String userToken=request.getHeader("token");
            if(userToken==null)
                throw new Exception("need user token!");
            return new ResponseBean(200,"success",measureService.getRepoRankByCommit(userToken,since,until));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }

    }


    @GetMapping("/measure/repository/commit")
    @CrossOrigin
    public ResponseBean getCodeChangesByCommit(@RequestParam("repo_id")String repo_id,
                                               @RequestParam("commit_id")String commit_id){
        try{
            return new ResponseBean(200,"success",measureService.getCommitBaseInformation(repo_id, commit_id));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }

    }

    //获取一个repo在一段时间内的所有开发者的commit信息
    @GetMapping("/measure/repository/duration")
    @CrossOrigin
    public ResponseBean getCodeChangesByDuration(@RequestParam("repo_id")String repo_id,
                                               @RequestParam("since")String since,
                                               @RequestParam("until")String until){
        try{
            return new ResponseBean(200,"success",measureService.getCommitBaseInformationByDuration(repo_id, since, until));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }

    }

    //获取repo每月的commitBaseInfo
    @GetMapping("/measure/repository/commitBaseInfo-monthly")
    @CrossOrigin
    public ResponseBean getCodeChangesByDuration(@RequestParam("repo_id")String repo_id){

        try{
            return new ResponseBean(200,"success",measureService.getCommitBaseInfoMonthly(repo_id));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }

    }

    @GetMapping("/measure/repository/commit-counts")
    @CrossOrigin
    public ResponseBean getRepoCommitCountsByCommit(@RequestParam("repo_id")String repo_id,
                                               @RequestParam("since")String since,
                                               @RequestParam("until")String until){
        try{
            return new ResponseBean(200,"success",measureService.getCommitCountsByDuration(repo_id, since, until));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",e.getMessage());
        }

    }

    //获取一个repo的每月commit次数
    @GetMapping("/measure/repository/commit-counts-monthly")
    @CrossOrigin
    public ResponseBean getRepoCommitCountsLastyear(@RequestParam("repo_id")String repo_id)
                                                    {
        try{
            return new ResponseBean(200,"success",measureService.getCommitCountsMonthly(repo_id));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }
    }



    @GetMapping("/measure/repo-information")
    @CrossOrigin
    public ResponseBean getRepoMeasureByAnalysis(@RequestParam("repo_id")String repo_id,
                                                @RequestParam("commit_id")String commit_id
                                               ){
        try{
            return new ResponseBean(200,"success",measureService.getRepoMeasureByRepoIdAndCommitId(repo_id,commit_id));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }

    }

    @DeleteMapping("/measure/repo-information")
    @CrossOrigin
    public ResponseBean deleteRepoMeasureByRepoId(@RequestParam("repo_id")String repo_id
    ){
        try{
            measureService.deleteRepoMeasureByRepoId(repo_id);
            return new ResponseBean(200,"success",null);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }

    }

    @GetMapping("/measure/repository/quality")
    @CrossOrigin
    public ResponseBean getRepoQualityByCommit(@RequestParam("repo_id")String repo_id,
                                                 @RequestParam("commit_id")String commit_id,
                                                      @RequestParam("category")String category,
                                               HttpServletRequest request
                                                      ){
        try{
            String token = request.getHeader("token");
            return new ResponseBean(200,"success",measureService.getQuantityByCommitAndCategory(repo_id,commit_id,category,token));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }

    }


    @GetMapping("/measure/repository/quality-change")
    @CrossOrigin
    public ResponseBean getRepoQualityChangesByCommit(@RequestParam("repo_id")String repo_id,
                                                      @RequestParam("commit_id")String commit_id,
                                                      @RequestParam("category")String category,
                                                      HttpServletRequest request
    ){
        try{
            String token = request.getHeader("token");
            return new ResponseBean(200,"success",measureService.getQuantityChangesByCommitAndCategory(repo_id,commit_id,category,token));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }

    }

    @GetMapping("/measure/repository/active")
    @CrossOrigin
    public ResponseBean getRepoActivityByRepoId(@RequestParam("repo_id")String repo_id
    ){
        try{
            return new ResponseBean(200,"success",measureService.getActivityByRepoId(repo_id));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }
    }

    @GetMapping("/measure/developer/code-change")
    @CrossOrigin
    public ResponseBean getCodeChangesByDurationAndDeveloperName(
                                                                 @RequestParam("developer_name")String developer_name,
                                                                 @RequestParam("since")String since,
                                                                 @RequestParam("until")String until,
                                                                 @RequestParam("category")String category,
                                                                 HttpServletRequest request
    ){
        try{
            String token = request.getHeader("token");
            return new ResponseBean(200,"success",measureService.getCodeChangesByDurationAndDeveloperName(developer_name,since,until,token,category));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }
    }


    @GetMapping("/measure/developer/commit-count")
    @CrossOrigin
    public ResponseBean getCommitCountByDurationAndDeveloperName(
                                                                 @RequestParam("developer_name")String developer_name,
                                                                 @RequestParam("since")String since,
                                                                 @RequestParam("until")String until,
                                                                 @RequestParam("category")String category,
                                                                 HttpServletRequest request
    ){
        try{
            String token = request.getHeader("token");
            return new ResponseBean(200,"success",measureService.getCommitCountByDurationAndDeveloperName(developer_name,since,until,token,category));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }
    }

    @GetMapping("/measure/developer/repository-list")
    @CrossOrigin
    public ResponseBean getRepoListByDeveloperName(
                                                    @RequestParam("developer_name")String developer_name,
                                                    @RequestParam("category")String category,
                                                    HttpServletRequest request
    ){
        try{
            String token = request.getHeader("token");
            return new ResponseBean(200,"success",measureService.getRepoListByDeveloperName(developer_name,token,category));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }
    }

    @GetMapping("/measure/developer/quality-changes")
    @CrossOrigin
    public ResponseBean getQualityChangesByDeveloperName(
            @RequestParam("developer_name")String developerName,
            @RequestParam("category")String category,
            @RequestParam(value="counts" ,required =false,defaultValue = "0")int counts,
            @RequestParam(value="project_name" ,required =false )String projectName,
            HttpServletRequest request
    ){
        try{
            String token = request.getHeader("token");
            return new ResponseBean(200,"success",measureService.getQualityChangesByDeveloperName(developerName,token,category,counts,projectName));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",e.getMessage());
        }
    }
}
