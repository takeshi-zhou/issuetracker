package cn.edu.fudan.measureservice.controller;

import cn.edu.fudan.measureservice.domain.ResponseBean;
import cn.edu.fudan.measureservice.service.MeasureDeveloperService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class MeasureDeveloperController {


    private MeasureDeveloperService measureDeveloperService;

    public MeasureDeveloperController(MeasureDeveloperService measureDeveloperService) {
        this.measureDeveloperService = measureDeveloperService;
    }


    //中汇需要
    @GetMapping("/measure/developer/code-change")
    @CrossOrigin
    public ResponseBean getCodeChangesByDurationAndDeveloperName(
                                                                 @RequestParam(value="developer_name", required = false)String developer_name,
                                                                 @RequestParam(value="since",required = false)String since,
                                                                 @RequestParam(value="until",required = false)String until,
                                                                 @RequestParam(value="category",required = false)String category,
                                                                 @RequestParam(value ="repo_id" ,required = false)String repoId,
                                                                 HttpServletRequest request
    ){
        try{
            String token = request.getHeader("token");
            return new ResponseBean(200,"success",measureDeveloperService.getCodeChangesByDurationAndDeveloperName(developer_name,since,until,token,category,repoId));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }
    }

    //中汇需要
    @GetMapping("/measure/developer/commit-count")
    @CrossOrigin
    public ResponseBean getCommitCountByDurationAndDeveloperName(
                                                                 @RequestParam("developer_name")String developerName,
                                                                 @RequestParam("since")String since,
                                                                 @RequestParam("until")String until,
                                                                 @RequestParam("category")String category,
                                                                 @RequestParam(value="repo_id",required=false)String repoId,
                                                                 HttpServletRequest request
    ){
        try{
            String token = request.getHeader("token");
            return new ResponseBean(200,"success",measureDeveloperService.getCommitCountByDurationAndDeveloperName(developerName,since,until,token,category,repoId));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }
    }

    //中汇需要
    @GetMapping("/measure/developer/repository-list")
    @CrossOrigin
    public ResponseBean getRepoListByDeveloperName(
                                                    @RequestParam("developer_name")String developer_name,
                                                    @RequestParam("category")String category,
                                                    HttpServletRequest request
    ){
        try{
            String token = request.getHeader("token");
            return new ResponseBean(200,"success",measureDeveloperService.getRepoListByDeveloperName(developer_name,token,category));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }
    }

    //中汇需要
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
            return new ResponseBean(200,"success",measureDeveloperService.getQualityChangesByDeveloperName(developerName,token,category,counts,projectName));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",e.getMessage());
        }
    }

    //根据时间粒度获取程序员活跃度画像
    @GetMapping("/measure/repository/developer-activeness-granularity")
    @CrossOrigin
    public ResponseBean getDeveloperActivenessByGranularity(@RequestParam("repo_id")String repo_id,
                                               @RequestParam(name = "granularity")String granularity,
                                               @RequestParam(name = "developer_name")String developer_name){

        try{
            return new ResponseBean(200,"success",measureDeveloperService.getDeveloperActivenessByGranularity(repo_id, granularity, developer_name));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",e.getMessage());
        }
    }

    //根据时间段获取程序员活跃度画像
    @GetMapping("/measure/repository/developer-activeness-duration")
    @CrossOrigin
    public ResponseBean getDeveloperActivenessByDuration(@RequestParam("repo_id")String repo_id,
                                                         @RequestParam(name = "since")String since,
                                                         @RequestParam(name = "until")String until,
                                               @RequestParam(name = "developer_name")String developer_name){

        try{
            return new ResponseBean(200,"success",measureDeveloperService.getDeveloperActivenessByDuration(repo_id, since, until, developer_name));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",e.getMessage());
        }
    }

    /**
     *
     * @param repoId
     * @param developer
     * @param beginDate
     * @param endDate
     * @param tool
     * @param request
     * @return 获取开发者雷达图度量数据
     */
    @GetMapping("/measure/portrait")
    @CrossOrigin
    public ResponseBean getPortrait(@RequestParam("repo-id")String repoId,
                                    @RequestParam("developer")String developer,
                                    @RequestParam("begin-date")String beginDate,
                                    @RequestParam("end-date")String endDate,
                                    @RequestParam("tool")String tool,
                                    HttpServletRequest request){

        try{
            String token = request.getHeader("token");
            return new ResponseBean(200,"success",measureDeveloperService.getPortrait(repoId,developer,beginDate,endDate,token,tool));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",e.getMessage());
        }
    }

}
