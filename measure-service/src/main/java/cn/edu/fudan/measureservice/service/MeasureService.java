package cn.edu.fudan.measureservice.service;

import cn.edu.fudan.measureservice.domain.ActiveMeasure;
import cn.edu.fudan.measureservice.domain.Duration;
import cn.edu.fudan.measureservice.domain.Granularity;
import cn.edu.fudan.measureservice.domain.RepoMeasure;

import java.util.List;

public interface MeasureService {

    /**
     * 获取一个用户所有项目在某个时间段前后度量值的变化
     * @param userToken 用户token
     * @param duration 时间段的单位day或month
     * @return 度量值的变化
     */
    Object getMeasureDataChange(String userToken, Duration duration);

    /**
     * 获取一个项目在某个时间段特定时间单位的项目级别的所有度量信息
     * @param repoId repo的唯一标识
     * @param since 起始时间
     * @param until 终止时间
     * @param granularity 时间段的单位day,week,month
     * @return 每个时间点上的项目级度量信息
     */
    List<RepoMeasure> getRepoMeasureByRepoId(String repoId,String since,String until,Granularity granularity);

    /**
     * 获取一个项目的所有模块的名字
     * @param repoId repo的唯一标识
     * @return 项目的所有模块的名字
     */
    List<String> getModulesOfRepo(String repoId);

    List<ActiveMeasure> getActiveMeasureChange(String repoId, String since, String until,Granularity granularity);

    /**
     * 获取一个项目在某段时间有关提交的活跃度的度量信息
     * @param repoId repo的唯一标识
     * @param since 起始时间
     * @param until 终止时间
     * @return 项目在某段时间有关提交的活跃度的度量信息
     */
    ActiveMeasure getOneActiveMeasure(String repoId,String since,String until);
}
