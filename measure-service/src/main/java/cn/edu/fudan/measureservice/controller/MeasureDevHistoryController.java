package cn.edu.fudan.measureservice.controller;

import cn.edu.fudan.measureservice.domain.ResponseBean;
import cn.edu.fudan.measureservice.service.MeasureDevHistoryService;
import cn.edu.fudan.measureservice.service.MeasureScanService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * description:
 *
 * @author fancying
 * create: 2020-06-11 10:41
 **/
@Slf4j
@RestController
public class MeasureDevHistoryController {


    private MeasureDevHistoryService measureDevHistoryService;

    @GetMapping("/measure/development-history/commit-info")
    @CrossOrigin
    public ResponseBean getDevHistoryCommitInfo(@RequestParam("repo_id")String repoId,
                                                @RequestParam("begin_date")String beginDate,
                                                @RequestParam("end_date")String endDate){

        try{
            return new ResponseBean(200,"success",measureDevHistoryService.getDevHistoryCommitInfo(repoId,beginDate,endDate));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",e.getMessage());
        }
    }


    @Autowired
    public void setMeasureScanService(MeasureDevHistoryService measureDevHistoryService) {
        this.measureDevHistoryService = measureDevHistoryService;
    }


}