package cn.edu.fudan.measureservice.controller;

import cn.edu.fudan.measureservice.domain.Granularity;
import cn.edu.fudan.measureservice.domain.ResponseBean;
import cn.edu.fudan.measureservice.service.MeasureRepoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class MeasureRepoController {


    private MeasureRepoService measureRepoService;

    public MeasureRepoController(MeasureRepoService measureRepoService) {
        this.measureRepoService = measureRepoService;
    }

    @GetMapping("/measure/repository")
    @CrossOrigin
    public ResponseBean getMeasureDataByRepoId(@RequestParam("repo_id")String repo_id,
                                               @RequestParam(name="since",required = false)String since,
                                               @RequestParam("until")String until,
                                               @RequestParam("granularity") Granularity granularity){
        try{
            return new ResponseBean(200,"success",measureRepoService.getRepoMeasureByRepoId(repo_id,since,until,granularity));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }
    }

    /**
     * 属于中汇需求
     */
    @GetMapping("/measure/repository/commit")
    @CrossOrigin
    public ResponseBean getCodeChangesByCommit(@RequestParam("repo_id")String repo_id,
                                               @RequestParam("commit_id")String commit_id){
        try{
            return new ResponseBean(200,"success",measureRepoService.getCommitBaseInformation(repo_id, commit_id));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }

    }

    /**
     *     获取一个repo在一段时间内的某个开发者的commit信息，如果不指定开发者参数，则返回所有开发者commit信息
     */
    @GetMapping("/measure/repository/duration")
    @CrossOrigin
    public ResponseBean getCommitBaseInformationByDuration(@RequestParam("repo_id")String repo_id,
                                               @RequestParam("since")String since,
                                               @RequestParam("until")String until,
                                               @RequestParam(name = "developer_name",required = false)String developer_name
                                                 ){
        try{
            return new ResponseBean(200,"success",measureRepoService.getCommitBaseInformationByDuration(repo_id, since, until, developer_name));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }

    }

    /**
     *     按照不同时间段（since、until），不同聚合粒度（granularity：天/周/月），不同开发者（developerName），获取工作量数据
     */
    @GetMapping("/measure/repository/commitBaseInfo-granularity")
    @CrossOrigin
    public ResponseBean getCommitBaseInfoGranularity(@RequestParam("repo_id")String repo_id,
                                                 @RequestParam(name = "granularity",required = false, defaultValue = "week")String granularity,
                                                 @RequestParam(name = "since",required = false )String since,
                                                 @RequestParam(name = "until",required = false )String until,
                                                 @RequestParam(name = "developer_name",required = false, defaultValue = "" )String developer_name){

        try{
            return new ResponseBean(200,"success",measureRepoService.getCommitBaseInfoGranularity(repo_id, granularity, since, until, developer_name));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",e.getMessage());
        }
    }

    /**
     * 属于中汇需求
     */
    @GetMapping("/measure/repository/commit-counts")
    @CrossOrigin
    public ResponseBean getRepoCommitCountsByCommit(@RequestParam("repo_id")String repo_id,
                                               @RequestParam("since")String since,
                                               @RequestParam("until")String until){
        try{
            return new ResponseBean(200,"success",measureRepoService.getCommitCountsByDuration(repo_id, since, until));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",e.getMessage());
        }
    }

    /**
     * 属于中汇需求
     */
    @GetMapping("/measure/repo-information")
    @CrossOrigin
    public ResponseBean getRepoMeasureByAnalysis(@RequestParam("repo_id")String repo_id,
                                                @RequestParam("commit_id")String commit_id
                                               ){
        try{
            return new ResponseBean(200,"success",measureRepoService.getRepoMeasureByRepoIdAndCommitId(repo_id,commit_id));
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
            measureRepoService.deleteRepoMeasureByRepoId(repo_id);
            return new ResponseBean(200,"success",null);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }

    }

    /**
     * 属于中汇需求
     */
    @GetMapping("/measure/repository/quality")
    @CrossOrigin
    public ResponseBean getRepoQualityByCommit(@RequestParam("repo_id")String repo_id,
                                                 @RequestParam("commit_id")String commit_id,
                                                      @RequestParam("category")String category,
                                               HttpServletRequest request
                                                      ){
        try{
            String token = request.getHeader("token");
            return new ResponseBean(200,"success",measureRepoService.getQuantityByCommitAndCategory(repo_id,commit_id,category,token));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }

    }

    /**
     * 属于中汇需求
     */
    @GetMapping("/measure/repository/quality-change")
    @CrossOrigin
    public ResponseBean getRepoQualityChangesByCommit(@RequestParam("repo_id")String repo_id,
                                                      @RequestParam("commit_id")String commit_id,
                                                      @RequestParam("category")String category,
                                                      HttpServletRequest request
    ){
        try{
            String token = request.getHeader("token");
            return new ResponseBean(200,"success",measureRepoService.getQuantityChangesByCommitAndCategory(repo_id,commit_id,category,token));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }

    }

    /**
     * 属于中汇需求
     */
    @GetMapping("/measure/repository/active")
    @CrossOrigin
    public ResponseBean getRepoActivityByRepoId(@RequestParam("repo_id")String repo_id
    ){
        try{
            return new ResponseBean(200,"success",measureRepoService.getActivityByRepoId(repo_id));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }
    }


    /**
     *     根据repo_id和since、until获取某个时间段内commit次数最多的5位开发者姓名以及对应的commit次数
     */
    @GetMapping("/measure/developer-rank/commit-count")
    @CrossOrigin
    public ResponseBean getDeveloperRankByCommitCount(
            @RequestParam("repo_id")String repo_id,
            @RequestParam("since")String since,
            @RequestParam("until")String until){
        try{
            return new ResponseBean(200,"success",measureRepoService.getDeveloperRankByCommitCount(repo_id, since, until));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",e.getMessage());
        }
    }

    /**
     *     根据repo_id和since、until获取某个时间段内,该项目中提交代码行数（LOC）最多的前5名开发者的姓名以及对应的LOC
     */
    @GetMapping("/measure/developer-rank/loc")
    @CrossOrigin
    public ResponseBean getDeveloperRankByLoc(
            @RequestParam("repo_id")String repo_id,
            @RequestParam("since")String since,
            @RequestParam("until")String until){
        try{
            return new ResponseBean(200,"success",measureRepoService.getDeveloperRankByLoc(repo_id, since, until));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",e.getMessage());
        }
    }

    /**
     *     获取某段时间内，每天的所有开发者commit的提交次数
     */
    @GetMapping("/measure/repository/commit-count-daily")
    @CrossOrigin
    public ResponseBean getCommitCountsDaily(
            @RequestParam("repo_id")String repo_id,
            @RequestParam("since")String since,
            @RequestParam("until")String until){
        try{
            return new ResponseBean(200,"success",measureRepoService.getCommitCountsDaily(repo_id, since, until));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",e.getMessage());
        }
    }

    /**
     *     获取某段时间内的，该repo的所有开发者产生的LOC
     */
    @GetMapping("/measure/repository/LOC")
    @CrossOrigin
    public ResponseBean getRepoLOCByDuration(@RequestParam("repo_id")String repo_id,
                                                    @RequestParam("since")String since,
                                                    @RequestParam("until")String until){
        try{
            return new ResponseBean(200,"success",measureRepoService.getRepoLOCByDuration(repo_id, since, until));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",e.getMessage());
        }
    }

    /**
     *     获取某段时间内，每天的所有开发者产生的LOC
     */
    @GetMapping("/measure/repository/LOC-daily")
    @CrossOrigin
    public ResponseBean getLOCDaily(
            @RequestParam("repo_id")String repo_id,
            @RequestParam("since")String since,
            @RequestParam("until")String until){
        try{
            return new ResponseBean(200,"success",measureRepoService.getLOCDaily(repo_id, since, until));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",e.getMessage());
        }
    }

    /**
     *     获取某段时间内，每天的所有开发者产生的LOC
     */
    @GetMapping("/measure/repository/commit-count&LOC-daily")
    @CrossOrigin
    public ResponseBean getCommitCountLOCDaily(
            @RequestParam("repo_id")String repo_id,
            @RequestParam("since")String since,
            @RequestParam("until")String until){
        try{
            return new ResponseBean(200,"success",measureRepoService.getCommitCountLOCDaily(repo_id, since, until));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",e.getMessage());
        }
    }

    @ApiOperation(value = "开发者列表", notes = "根据repoId获取开发者列表", httpMethod = "GET")
    @GetMapping("/measure/repository/developer-list")
    @CrossOrigin
    public ResponseBean getDeveloperListByRepoId(@RequestParam("repo_id")String repo_id){

        try{
            return new ResponseBean(200,"success",measureRepoService.getDeveloperListByRepoId(repo_id));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",e.getMessage());
        }
    }

}
