package cn.edu.fudan.measureservice.controller;

import cn.edu.fudan.measureservice.domain.ResponseBean;
import cn.edu.fudan.measureservice.service.MeasureScanService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * description:
 *
 * @author fancying
 * create: 2020-06-11 10:41
 **/
@Slf4j
@RestController
public class MeasureScanController {


    private MeasureScanService measureScanService;

    /**
     * description 接收请求开始扫描 servicePath + toolName + "/scan", jsonObject, JSONObject.class 接收scan服务的请求进行扫描
     * @param jsonObject : repoId branch beginCommit
     */
    @PostMapping(value = {"/measure/{toolName}"})
    public ResponseBean scan(@PathVariable("toolName") String toolName, @RequestBody JSONObject jsonObject) {
        String repoId = "repoId";
        String branch = "branch";
        String beginCommit = "beginCommit";
        repoId = jsonObject.getString(repoId);
        branch = jsonObject.getString(branch);
        beginCommit = jsonObject.getString(beginCommit);
        // TODO 调用 tool scan 流程
        try {
//            RepoResourceDTO repoResourceDTO = RepoResourceDTO.builder ().repoId (repoId).build ();
//            issueScanService.prepareForScan (repoResourceDTO, branch, beginCommit, toolName);
            // measure service scan
            measureScanService.scan(repoId, branch, beginCommit);
            return ResponseBean.builder().code(200).build();
        }catch (Exception e) {
            log.error("invoke tool:[{}] failed! message is {}", toolName, e.getMessage());
            return ResponseBean.builder().code(500).data(e.getMessage()).build();
        }
    }


    @Autowired
    public void setMeasureScanService(MeasureScanService measureScanService) {
        this.measureScanService = measureScanService;
    }


}