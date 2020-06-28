package cn.edu.fudan.measureservice.portrait;

import lombok.Getter;
import lombok.Setter;

/**
 * description: 指标基类 后续需要改一个名字 这里体现的是行数
 *
 * @author fancying
 * create: 2020-05-18 21:20
 **/
@Getter
@Setter
public abstract class BaseMetrics {

    int addLine;
    int deleteLine;
    int totalLine;

}