package cn.edu.fudan.measureservice.handler;

import cn.edu.fudan.measureservice.domain.Measure;

public interface ResultHandler {

    Measure handle(String resultFileName,String level);
}
