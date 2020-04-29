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

    @GetMapping(value = {"/cloneMeasure/newCloneLines"})
    public ResponseBean getMeasureClone(
            @RequestParam("repo_id") String repoId,
            @RequestParam("commit_id") String commitId
    ){
        try{
            return new ResponseBean(200,"success",cloneMeasureService.getCloneMeasure(repoId, commitId));
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
