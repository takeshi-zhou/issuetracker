package cn.edu.fudan.scanservice.controller;

import cn.edu.fudan.scanservice.domain.ResponseBean;
import cn.edu.fudan.scanservice.domain.Scan;
import cn.edu.fudan.scanservice.service.KafkaService;
import cn.edu.fudan.scanservice.service.ScanService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

@RestController
@EnableAutoConfiguration
public class ScanController {

    private ScanService scanService;

    @Autowired
    public void setScanService(ScanService scanService) {
        this.scanService = scanService;
    }

    private KafkaService kafkaService;

    @Autowired
    public void setKafkaService(KafkaService kafkaService) {
        this.kafkaService = kafkaService;
    }


    @PostMapping(value = {"/scan"})
    @CrossOrigin
    public Object scan(@RequestBody JSONObject requestParam) {
        try {
            kafkaService.scanByRequest(requestParam);
            return new ResponseBean(200, "scan msg send success!", null);
        } catch (Exception e) {
            return new ResponseBean(401, e.getMessage(), null);
        }
    }

    @GetMapping(value = {"/scan/commits"})
    @CrossOrigin
    public Object getCommits(@RequestParam(name = "project_id") String project_id,
                             @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                             @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
                             @RequestParam(name = "is_whole", required = false, defaultValue = "false") Boolean is_whole,
                             @RequestParam(name="category")String category) {
        return scanService.getCommits(project_id, page, size, is_whole,category);
    }

    @GetMapping(value = {"/scan/next-scanned-commits"})
    public Object getNextScannedCommit(@RequestParam("repo_id") String repo_id,
                                       @RequestParam("category")String category,
                                       @RequestParam("commit_id")String commit_id){
        try{
            return new ResponseBean(200,"success",scanService.getNextScannedCommitID(repo_id, category, commit_id));
        }catch (Exception e){
            return new ResponseBean(500,e.getMessage(),null);
        }
    }

    @GetMapping(value = {"/scan/pre-scanned-commits"})
    public Object getPreScannedCommit(@RequestParam("repo_id") String repo_id,
                                       @RequestParam("category")String category,
                                       @RequestParam("commit_id")String commit_id){
        try{
            return new ResponseBean(200,"success",scanService.getPreviousScannedCommitID(repo_id, category, commit_id));
        }catch (Exception e){
            return new ResponseBean(500,e.getMessage(),null);
        }
    }


    @GetMapping(value = {"/scan/test-auto-scan"})
    public Object startAutoScan(@RequestParam("repo_id") String repo_id,
                                      @RequestParam("category")String category,
                                      @RequestParam("commit_id")String commit_id){
        try{
            return new ResponseBean(200,"success",kafkaService.startAutoScan(repo_id, commit_id));
        }catch (Exception e){
            return new ResponseBean(500,e.getMessage(),null);
        }
    }

}