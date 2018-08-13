package cn.edu.fudan.scanservice.controller;

import cn.edu.fudan.scanservice.domain.ResponseBean;
import cn.edu.fudan.scanservice.domain.Scan;
import cn.edu.fudan.scanservice.service.KafkaService;
import cn.edu.fudan.scanservice.service.ScanService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scan")
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


    /**
     *  requestParam:  projectId  commitId
     *  doScan ??  scan 的区别
     * */
    @PostMapping(value = {"/doScan"})
    public Object scan(@RequestBody JSONObject requestParam){
        try{
            kafkaService.scanByRequest(requestParam);
            return new ResponseBean(200,"scan msg send success!",null);
        }catch (Exception e){
            return new ResponseBean(401,e.getMessage(),null);
        }

    }

    @PostMapping
    public Object addScan(@RequestBody Scan scan){
        try{
            scanService.insertOneScan(scan);
            return new ResponseBean(200,"scan add success!",null);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"scan add failed!",null);
        }
    }

    @DeleteMapping(value = {"/delete/{projectId}"})
    public Object deleteScans(@PathVariable("projectId")String projectId){
        try{
            scanService.deleteScanByProjectId(projectId);
            return new ResponseBean(200,"scan delete success!",null);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"scan delete failed",null);
        }
    }

    //old
    // (value = {"/update"})
    @PutMapping
    public Object updateScan(@RequestBody Scan scan){
        try{
            scanService.updateOneScan(scan);
            return new ResponseBean(200,"scan update success!",null);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"scan update failed",null);
        }
    }

    @GetMapping(value = {"/last-commit-date"})
    public Object getTillCommitDateByProjectId(@RequestParam("project-id") String projectId ){
        return scanService.getTillCommitDateByProjectId(projectId);
    }

}
