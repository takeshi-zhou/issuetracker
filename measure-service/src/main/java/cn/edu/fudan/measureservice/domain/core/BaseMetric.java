package cn.edu.fudan.measureservice.domain.core;

import lombok.Getter;
import lombok.Setter;

/**
 * description: 基础度量指标
 *
 * @author fancying
 * create: 2020-06-10 23:57
 **/
@Getter
@Setter
public abstract class BaseMetric {

    private CommonInfo commonInfo;

    int addLine;
    int deleteLine;
    int totalLine;

}