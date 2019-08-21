package cn.edu.fudan.measureservice.service;

import cn.edu.fudan.measureservice.domain.*;
import cn.edu.fudan.measureservice.domain.Package;

import java.util.List;
import java.util.Map;

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
     * 获取一个项目在某个特定commit快照下的存在包的所有度量信息
     * @param repoId repo的唯一标识
     * @param commit  commit
     * @return 每个时间点上包的度量信息
     */
    List<Package> getPackageMeasureUnderSpecificCommit(String repoId, String commit);

    List<ActiveMeasure> getActiveMeasureChange(String repoId, String since, String until,Granularity granularity);

    /**
     * 获取一个项目在某段时间有关提交的活跃度的度量信息
     * @param repoId repo的唯一标识
     * @param since 起始时间
     * @param until 终止时间
     * @return 项目在某段时间有关提交的活跃度的度量信息
     */
    ActiveMeasure getOneActiveMeasure(String repoId,String since,String until);

    /**
     * 一个用户提交次数最多的项目排名
     * @param token 用户token
     * @param since 起始时间
     * @param until 终止时间
     * @return 提交次数最多的项目排名
     */
    List<RepoRank> getRepoRankByCommit(String token,String since,String until);

    /**
     * 一个项目的所有详细信息
     * @param repo_id repo的唯一标识
     * @param since 起始时间
     * @param until 终止时间
     * @return repo的快照所有信息
     */
    RepoAllInformations getOneRepoAllInformations(String repo_id,String since,String until);
}
