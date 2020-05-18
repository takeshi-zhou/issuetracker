package cn.edu.fudan.measureservice.portrait;

import java.util.Date;

/**
 * description: 指标基类
 *
 * @author fancying
 * create: 2020-05-18 21:20
 **/
public abstract class BaseMetrics {

    private String repoUuid;
    private String commitId;

    private Date start;
    private Date end;

}