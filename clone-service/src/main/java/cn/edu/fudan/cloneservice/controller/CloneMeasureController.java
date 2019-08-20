package cn.edu.fudan.cloneservice.controller;

import cn.edu.fudan.cloneservice.domain.ResponseBean;
import cn.edu.fudan.cloneservice.service.CloneMeasureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by njzhan
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

    public ResponseBean getMeasureCloneData(@RequestParam("repo_id") String repo_id,
                                            @RequestParam("commit_id") String commit_id){
        try{
            return new ResponseBean(200,"success",cloneMeasureService.getRepoMeasureCloneDataByRepoIdCommitId(repo_id,commit_id));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }
    }
}
