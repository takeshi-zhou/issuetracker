package cn.edu.fudan.measureservice.controller;

import cn.edu.fudan.measureservice.domain.Duration;
import cn.edu.fudan.measureservice.domain.ResponseBean;
import cn.edu.fudan.measureservice.service.MeasureService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeasureController {


    private MeasureService measureService;

    public MeasureController(MeasureService measureService) {
        this.measureService = measureService;
    }

    @GetMapping("/measure")
    public ResponseBean getMeasureData(@RequestParam("project_id")String project_id,
                                       @RequestParam("duration")Duration duration){
        try{
            return new ResponseBean(200,"success",measureService.getMeasureDataChange(project_id,duration));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }

    }

}
