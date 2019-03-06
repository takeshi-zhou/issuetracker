package cn.edu.fudan.measureservice.analyzer;

import cn.edu.fudan.measureservice.domain.Measure;
import cn.edu.fudan.measureservice.handler.ResultHandler;

public interface MeasureAnalyzer {

    Measure analyze(String targetPath, String level,ResultHandler resultHandler);
}
