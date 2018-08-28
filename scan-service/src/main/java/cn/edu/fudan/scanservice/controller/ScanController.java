package cn.edu.fudan.scanservice.controller;

import cn.edu.fudan.scanservice.domain.ResponseBean;
import cn.edu.fudan.scanservice.domain.Scan;
import cn.edu.fudan.scanservice.service.KafkaService;
import cn.edu.fudan.scanservice.service.ScanService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
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


    @PostMapping(value={"/scan"})
    @CrossOrigin
    public Object scan(@RequestBody JSONObject requestParam){
        try{
            kafkaService.scanByRequest(requestParam);
            return new ResponseBean(200,"scan msg send success!",null);
        }catch (Exception e){
            return new ResponseBean(401,e.getMessage(),null);
        }

    }

    @GetMapping(value = {"/scan/commits"})
    @CrossOrigin
    public Object getCommits(@RequestParam(name="project_id",required = true) String project_id ,
                                @RequestParam(name="page",required = false,defaultValue = "1")Integer page,
                                @RequestParam(name="size",required = false,defaultValue = "10")Integer size,
                                @RequestParam(name="is_whole",required = false,defaultValue = "false") Boolean is_whole){
        return scanService.getCommits(project_id,page,size,is_whole);
    }

    //下面都是供其它服务调用的内部接口

    @DeleteMapping(value = {"/inner/scan/{projectId}"})
    public Object deleteScans(@PathVariable("projectId")String projectId){
        try{
            scanService.deleteScanByProjectId(projectId);
            return new ResponseBean(200,"scan delete success!",null);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"scan delete failed",null);
        }
    }

    @PutMapping(value={"/inner/scan"})
    public Object updateScan(@RequestBody Scan scan){
        try{
            scanService.updateOneScan(scan);
            return new ResponseBean(200,"scan update success!",null);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"scan update failed",null);
        }
    }

    @GetMapping(value = {"/inner/scan/last-commit-date"})
    public Object getTillCommitDateByProjectId(@RequestParam("project-id") String projectId ){
        return scanService.getTillCommitDateByProjectId(projectId);
    }


    @GetMapping(value = {"/inner/scan/commits"})
    public Object getScannedCommits(@RequestParam("project_id") String project_id){
        return scanService.getScannedCommits(project_id);
    }

}