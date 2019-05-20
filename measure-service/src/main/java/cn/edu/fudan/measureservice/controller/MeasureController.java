package cn.edu.fudan.measureservice.controller;

import cn.edu.fudan.measureservice.domain.Duration;
import cn.edu.fudan.measureservice.domain.Granularity;
import cn.edu.fudan.measureservice.domain.ResponseBean;
import cn.edu.fudan.measureservice.service.MeasureService;
import org.apache.ibatis.annotations.Param;
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
    public ResponseBean getMeasureDataByRepoId(@Param("repo_id")String repo_id,
                                               @Param("since")String since,
                                               @Param("until")String until,
                                               @Param("duration") Granularity granularity){
        try{
            return new ResponseBean(200,"success",measureService.getRepoMeasureByRepoId(repo_id,since,until,granularity));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }
    }

    @GetMapping("/measure/modules")
    @CrossOrigin
    public ResponseBean getModules(@Param("repo_id")String repo_id){
        try{
            return new ResponseBean(200,"success",measureService.getModulesOfRepo(repo_id));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }
    }

    @GetMapping("/measure/active")
    @CrossOrigin
    public ResponseBean getActiveMeasure(@Param("repo_id")String repo_id,
                                         @Param("since")String since,
                                         @Param("until")String until){
        try{
            return new ResponseBean(200,"success",measureService.getOneActiveMeasure(repo_id, since, until));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }
    }

    @GetMapping("/measure/repo-rank")
    @CrossOrigin
    public ResponseBean getMeasureData(@Param("since")String since,
                                       @Param("until")String until,
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
}
