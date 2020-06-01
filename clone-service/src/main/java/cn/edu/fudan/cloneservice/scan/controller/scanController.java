package cn.edu.fudan.cloneservice.scan.controller;

import cn.edu.fudan.cloneservice.scan.service.ScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author zyh
 * @date 2020/5/27
 */
@RestController
@EnableAutoConfiguration
public class scanController {

    @Autowired
    private ScanService scanService;

    @GetMapping("/cloneScan")
    public void cloneScan(@RequestParam("repo_id") String repoId,
                     @RequestParam("commit_id") String commitId){
        scanService.cloneScan(repoId, commitId);
    }

    @DeleteMapping("/cloneScan/{repoId}")
    public void deleteCloneScan(@PathVariable("repoId") String repoId){
        scanService.deleteCloneScan(repoId);
    }

}
