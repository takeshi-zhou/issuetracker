package cn.edu.fudan.measureservice.service;

import cn.edu.fudan.measureservice.domain.Duration;

public interface MeasureService {

    Object getMeasureDataChange(String userToken, Duration duration);
}
