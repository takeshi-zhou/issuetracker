package cn.edu.fudan.commitservice.controller;

import cn.edu.fudan.commitservice.domain.Commit;
import cn.edu.fudan.commitservice.domain.ResponseBean;
import cn.edu.fudan.commitservice.service.CommitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author WZY
 * @version 1.0
 **/
@RestController
@RequestMapping("/Commit")
public class CommitController {

    private CommitService commitService;

    @Autowired
    public void setCommitService(CommitService commitService) {
        this.commitService = commitService;
    }

    @GetMapping(value = "/commitList")
    public Object getCommitList(@RequestParam("project_id")String project_id) {
        return commitService.getCommitList(project_id);
    }

    @GetMapping(value = {"/isScanned/{commit_id}"})
    public Object commitIsScanned(@PathVariable("commit_id")String commit_id){
        return commitService.getCommitByCommitId(commit_id).getIs_scanned();
    }

    @GetMapping(value={"/scannedCommits/{project_id}"})
    public Object scannedCommits(@PathVariable("project_id")String project_id){
        return commitService.getScannedCommits(project_id);
    }

    @GetMapping(value = "/commitDate/{commit_id}")
    public Object getCommitDate(@PathVariable("commit_id")String commit_id){
        return commitService.getCommitDate(commit_id);
    }

    @GetMapping(value = "/tillCommitDate/{repo_id}")
    public Object getTillCommitDate(@PathVariable("repo_id")String repo_id){
        return commitService.getTillCommitDate(repo_id);
    }

    @PostMapping("/update")
    public Object updateCommit(@RequestBody Commit commit){
        try{
            commitService.updateCommitScanStatus(commit);
            return new ResponseBean(200,"update success!",null);
        }catch (Exception e){
            return new ResponseBean(401,"update failed!",null);
        }
    }

    @DeleteMapping("/delete/{projectId}")
    public Object deleteCommit(@PathVariable("projectId")String projectId) {
        try{
            commitService.deleteCommitByProjectId(projectId);
            return new ResponseBean(200,"delete success!",null);
        }catch (Exception e){
            return new ResponseBean(401,"delete failed",null);
        }
    }


}
