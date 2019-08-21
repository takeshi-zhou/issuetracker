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


    @GetMapping("/measure/repo-information")
    @CrossOrigin
    public ResponseBean getRepoAllInformations(@RequestParam("repo_id")String repo_id,
                                                @RequestParam("since")String since,
                                                @RequestParam("until")String until
                                               ){
        try{

            return new ResponseBean(200,"success",measureService.getOneRepoAllInformations(repo_id,since,until));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }

    }
}
