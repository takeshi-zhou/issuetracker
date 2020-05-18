package cn.edu.fudan.scanservice.controller;

import cn.edu.fudan.scanservice.component.rest.RestInterfaceManager;
import cn.edu.fudan.scanservice.domain.ResponseBean;
import cn.edu.fudan.scanservice.domain.Scan;
import cn.edu.fudan.scanservice.service.KafkaService;
import cn.edu.fudan.scanservice.service.ScanService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

/**
 * @author WZY
 * @version 1.0
 **/
@RestController
@EnableAutoConfiguration
public class InnerController {

    private ScanService scanService;


    @Autowired
    public void setScanService(ScanService scanService) {
        this.scanService = scanService;
    }



    //下面都是供其它服务调用的内部接口

    /**
     * 根据条件删除相应的scan
     * @param category
     * @param repoId
     * @return
     */
    @DeleteMapping(value = {"/inner/scan/{category}/{repoId}"})
    public Object deleteScans(@PathVariable("category")String category,@PathVariable("repoId") String repoId) {
        try {
            scanService.deleteScanByRepoIdAndCategory(repoId,category);
            return new ResponseBean(200, "scan delete success!", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseBean(401, "scan delete failed", null);
        }
    }

    /**
     *  更新一条scan信息
     * @param scan
     * @return
     */
    @PutMapping(value = {"/inner/scan"})
    public Object updateScan(@RequestBody Scan scan) {
        try {
            scanService.updateOneScan(scan);
            return new ResponseBean(200, "scan update success!", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseBean(401, "scan update failed", null);
        }
    }

    /**
     *  插入一条scan信息
     * @param scan
     * @return
     */
    @PostMapping(value = {"/inner/scan"})
    public Object addOneScan(@RequestBody Scan scan){
        try {
            scanService.insertOneScan(scan);
            return new ResponseBean(200, "scan add success!", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseBean(401, "scan add failed", null);
        }
    }

    /**
     * 获取已扫描的commit列表
     * @param repo_id
     * @param category
     * @return
     */
    @GetMapping(value = {"/inner/scan/commits"})
    public Object getScannedCommits(@RequestParam("repo_id") String repo_id,@RequestParam("category")String category) {
        return scanService.getScannedCommits(repo_id,category);
    }

    /**
     * 获取最后一条扫描的commit id
     * @param repo_id
     * @param category
     * @return
     */
    @GetMapping(value = "inner/scan/last-commit")
    public String getLatestScannedCommitId(@RequestParam("repo_id") String repo_id,@RequestParam("category")String category){
        return scanService.getLatestScannedCommitId(repo_id, category);
    }

    /**
     * 通过 Category  RepoId CommitId 获取相应的scan信息
     * @param repo_id
     * @param category
     * @param commit_id
     * @return
     */
    @GetMapping(value = "/inner/scan/commit")
    public Object getScanByCategoryAndRepoIdAndCommitId(@RequestParam("repo_id") String repo_id,@RequestParam("category")String category,@RequestParam("commit_id")String commit_id){
        return scanService.getScanByCategoryAndRepoIdAndCommitId(repo_id, category,commit_id);
    }


    /**
     * 通过当前的commit id ，获取前一条扫描成功的commit id
     * @param repoId
     * @param category
     * @param commitId
     * @return
     */
    @GetMapping(value = "/inner/scan/pre-scanned-commit")
    public Object getPreScannedCommitByCurrentCommit(@RequestParam("repo_id") String repoId,@RequestParam("category")String category,@RequestParam("commit_id")String commitId){
        return scanService.getPreScannedCommitByCurrentCommit(repoId, category,commitId);
    }

    /**
     * 通过当前的commit id ，获取最近的一条未扫描或未扫描成功的commit id
     * @param repoId
     * @param category
     * @param commitId
     * @return
     */
    @GetMapping(value = "/inner/scan/pre-failed-commit")
    public Object getLatestScanFailedCommitId(@RequestParam("repo_id") String repoId,@RequestParam("category")String category,@RequestParam("commit_id")String commitId){
        return scanService.getLatestScanFailedCommitIdAndDeveloper(repoId, category,commitId);
    }


    /**
     * 根据 repo id 与 status 获取相应的scan列表
     * @param repoId
     * @param status
     * @return
     */
    @GetMapping(value = "/inner/scan/get-by-status")
    public Object getScanByStatus(@RequestParam("repo_id") String repoId,@RequestParam("status")String status){
        return scanService.getScanByRepoIdAndStatusAndCategory(repoId, status,null);
    }
}
