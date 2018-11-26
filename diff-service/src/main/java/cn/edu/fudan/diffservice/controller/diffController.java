package cn.edu.fudan.diffservice.controller;

import cn.edu.fudan.diffservice.entity.DiffParam;
import cn.edu.fudan.diffservice.entity.RepoParam;
import cn.edu.fudan.diffservice.entity.ResponseBean;
import cn.edu.fudan.diffservice.service.DiffService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/diffservice")
public class diffController {

    private DiffService diffService;

    @Autowired
    public void setDiffService(DiffService diffService){
        this.diffService = diffService;
    }

    @PostMapping("/twofilediff")
    @CrossOrigin
    public Object twoFileDiff(@RequestBody DiffParam diffParam) {
//        diffService.getCode(diffParam.getRepoid(),diffParam.getCommit1(),diffParam.getFilePath1(),
//                diffParam.getCommit2(),diffParam.getFilePath2());
        try {
                JSONObject toWeb = new JSONObject();
                toWeb = diffService.diffTwoFile("/home/fdse/user/issueTracker/diffpath/",diffParam.getRepoid(),diffParam.getCommit1(),diffParam.getFilePath1());
                return new ResponseBean(200, "diff service success!", toWeb);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseBean(401, "diff service failed!", null);
            }
    }

    @PostMapping("/onefilediff")
    @CrossOrigin
    public Object repoDiff(@RequestBody RepoParam repoParam){

        try {
            diffService.getRepoDiff(repoParam.getRepoId(),repoParam.getCommitId(),
                    "/home/fdse/issueTracker/diff_repo");
            return new ResponseBean(200, "getRepoDiff success!", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseBean(401, "getRepoDiff failed!", null);
        }
    }
}
