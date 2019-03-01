package cn.edu.fudan.measureservice.controller;

import cn.edu.fudan.measureservice.analyzer.MeasureAnalyzer;
import cn.edu.fudan.measureservice.domain.ResponseBean;
import cn.edu.fudan.measureservice.handler.ResultHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeasureController {

    private MeasureAnalyzer measureAnalyzer;
    private ResultHandler resultHandler;

    public MeasureController(MeasureAnalyzer measureAnalyzer,
                             ResultHandler resultHandler) {
        this.measureAnalyzer = measureAnalyzer;
        this.resultHandler = resultHandler;
    }

    @GetMapping("/measure")
    public ResponseBean getMeasureData(@RequestParam("path")String path){
        try{
            return new ResponseBean(200,"success",measureAnalyzer.analyze(path,resultHandler));
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"failed",null);
        }

    }

}
