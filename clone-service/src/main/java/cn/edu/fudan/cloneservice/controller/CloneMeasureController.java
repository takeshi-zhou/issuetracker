package cn.edu.fudan.cloneservice.controller;

import cn.edu.fudan.cloneservice.dao.LocationDao;
import cn.edu.fudan.cloneservice.domain.Location;
import cn.edu.fudan.cloneservice.domain.ResponseBean;
import cn.edu.fudan.cloneservice.service.CloneMeasureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

/**
 * Created by njzhan
 *
 * <p>
 * Date :2019-08-19
 * <p>
 * Description :
 * <p>
 * Version :1.0
 */
@CrossOrigin
@RestController
@EnableAutoConfiguration
public class CloneMeasureController {
    @Autowired
    CloneMeasureService cloneMeasureService;

    @Autowired
    LocationDao location;

    @GetMapping(value = {"/cloneMeasure/insertMeasureClone"})
    public ResponseBean insertMeasureClone(@RequestParam("repo_id") String repoId, @RequestParam("commit_id") String commitId){

        try{
            return new ResponseBean(200,"success",cloneMeasureService.insertCloneMeasure(repoId, commitId));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }
    }

    @GetMapping(value = {"/cloneMeasure/getMeasureClone"})
    public ResponseBean getMeasureCloneData(@RequestParam("repo_id") String repoId,
                                            @RequestParam("developer") String developer,
                                            @RequestParam("start") String start,
                                            @RequestParam("end") String end){
        try{
            return new ResponseBean(200,"success",cloneMeasureService.getCloneMeasure(repoId,developer,start,end));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }
    }

    @GetMapping(value = {"/cloneMeasure/newCloneLines1"})
    public Object test(@RequestParam("repo_id") String repoId,
                       @RequestParam("commit_id") String commitId){
        return location.getLocationsByCommitIdAndRepoId(commitId, repoId);
    }

    @DeleteMapping(value = {"/cloneMeasure/{repoId}"})
    public Object deleteScans(@PathVariable("repoId") String repoId) {
        try {
            cloneMeasureService.deleteCloneMeasureByRepoId(repoId);
            return new ResponseBean(200, "clone measure delete success!", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseBean(401, "clone measure delete failed", null);
        }
    }

    @GetMapping(value = {"/cloneMeasure/scan"})
    public ResponseBean scan(@RequestParam("repoId") String repoId,
                             @RequestParam("startCommitId") String commitId
                                            ){
        try{
            cloneMeasureService.scanCloneMeasure(repoId,commitId);
            return new ResponseBean(200,"success",null);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }
    }

//
//    @GetMapping(value = {"/clonemeasure/repository"})
//    public ResponseBean getMeasureCloneData(@RequestParam("repo_id") String repo_id,
//                                            @RequestParam("commit_id") String commit_id){
//        try{
//            return new ResponseBean(200,"success",cloneMeasureService.getRepoMeasureCloneDataByRepoIdCommitId(repo_id,commit_id));
//        }catch (Exception e){
//            e.printStackTrace();
//            return new ResponseBean(401,"failed",null);
//        }
//    }
//    @GetMapping(value = {"/clonemeasure/repositoryratio"})
//    public ResponseBean getMeasureCloneRatio(
//            @RequestParam("repo_id") String repo_id,
//            @RequestParam("commit_id") String commit_id
//    ){
//        try{
//            return new ResponseBean(200,"success",cloneMeasureService.getRepoCloneRatioByRepoIdCommitId(repo_id,commit_id));
//        }catch (Exception e){
//            e.printStackTrace();
//            return new ResponseBean(401,"failed",null);
//        }
//    }
//
//
//    @GetMapping(value = {"/clonemeasure/repo-monthly"})
//    public ResponseBean getMeasureCloneMonthly(
//            @RequestParam("repo_id") String repo_id
//    ){
//        try{
//            return new ResponseBean(200,"success",cloneMeasureService.getRepoCloneInfoByRepoId(repo_id));
//        }catch (Exception e){
//            e.printStackTrace();
//            return new ResponseBean(401,"failed",null);
//        }
//    }



}
