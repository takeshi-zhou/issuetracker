package cn.edu.fudan.measureservice.controller;

import cn.edu.fudan.measureservice.domain.CommitWithTime;
import cn.edu.fudan.measureservice.domain.Duration;
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

    @GetMapping("/measure/trend")
    @CrossOrigin
    public ResponseBean getMeasureDataByRepoId(@Param("repo_id")String repo_id,@Param("duration")Duration duration){
        try{
            return new ResponseBean(200,"success",measureService.getRepoMeasureByRepoId(repo_id,duration));
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

    @GetMapping("/measure")
    @CrossOrigin
    public ResponseBean getMeasureData(@RequestParam("duration")Duration duration, HttpServletRequest request){
        try{
            String userToken=request.getHeader("token");
            if(userToken==null)
                throw new Exception("need user token!");
            return new ResponseBean(200,"success",measureService.getMeasureDataChange(userToken,duration));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }

    }

    @PostMapping("/measure")
    @CrossOrigin
    public ResponseBean saveMeasureData(@RequestBody CommitWithTime commits){
         measureService.saveMeasureData(commits.getRepoId(),commits.getCommitId(),commits.getCommitTime());
         return new ResponseBean(200,"success","");
    }

}
