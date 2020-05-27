package cn.edu.fudan.measureservice.service;

import cn.edu.fudan.measureservice.domain.*;
import cn.edu.fudan.measureservice.domain.Package;

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
     * 获取一个项目在某个commit的所有度量信息
     * @param repoId repo的唯一标识
     * @param commitId commit的唯一标志
     * @return 每个时间点上的项目级度量信息
     */
    RepoMeasure getRepoMeasureByRepoIdAndCommitId(String repoId,String commitId);


    /**
     * 删除一个项目的所有度量信息
     * @param repoId repo的唯一标识
     * @return
     */
    void deleteRepoMeasureByRepoId(String repoId);

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
     * 获取一个项目在某个特定commit快照下代码行数的变化
     * @param repo_id repo的唯一标识
     * @param commit_id commit的唯一标识
     * @return repo在某个commit的代码变化信息以及提交者的信息
     */
    CommitBase getCommitBaseInformation(String repo_id,String commit_id);

    /**
     * 获取一个项目在一段时间代码变化信息以及提交者的信息
     * @param repo_id repo的唯一标识
     * @param since 起始时间
     * @param until 终止时间
     * @return repo的一段时间代码变化信息以及提交者的信息
     */
    CommitBaseInfoDuration getCommitBaseInformationByDuration(String repo_id,String since,String until, String developer_name);


    /**
     *
     * @param repo_id
     * @param granularity
     * @param since
     * @param until
     * @param developer_name
     * @return 按照不同时间段（since、until），不同聚合粒度（granularity：天/周/月），不同开发者（developerName），获取工作量数据
     */
    List<CommitBaseInfoGranularity> getCommitBaseInfoGranularity(String repo_id, String granularity, String since, String until, String developer_name);

    /**
     * 获取一个项目在指定一段时间内提交次数
     * @param repo_id repo的唯一标识
     * @param since 起始时间
     * @param until 终止时间
     * @return repo的一段时间代码提交次数
     */
    int getCommitCountsByDuration(String repo_id,String since,String until);


    /**
     * 获取一个项目的每月提交次数
     * @param repo_id repo的唯一标识
     * @return repo的每月提交次数
     */
    List<CommitCountsMonthly> getCommitCountsMonthly(String repo_id);

    /**
     * 获取一个项目在某个特定commit快照下代码质量指数
     * @param repo_id repo的唯一标识
     * @param commit_id commit的唯一标识
     * @param category 问题类型，工具类型
     * @return 代码质量指数为代码行数/问题数
     */
    double getQuantityByCommitAndCategory(String repo_id,String commit_id,String category,String token);

    /**
     * 获取一个项目在某个特定commit快照下代码质量变化指数,包括缺陷消除质量变化指数，以及缺陷增加质量变化指数
     * @param repo_id repo的唯一标识
     * @param commit_id commit的唯一标识
     * @return repo的一段时间代码变化信息以及提交者的信息
     */
    Object getQuantityChangesByCommitAndCategory(String repo_id,String commit_id,String category,String token);

    /**
     * 获取一个项目在过去三个月的活跃度判定
     * @param repo_id repo的唯一标识
     * @return repo的活跃值
     */
    String getActivityByRepoId(String repo_id);

    /**
     * 获取在issueTracker用户下，所有项目中某个开发者在一段时间内的代码提交行数变化
     * @param developer_name 开发者的提交名字
     * @param since 提交的起始时间
     * @param until 提交的终止时间
     * @param token 用户token
     * @param category 工具类别
     * @param repoId repo唯一ID
     * @return 开发者在一段时间内代码变化信息
     */
    Object getCodeChangesByDurationAndDeveloperName(String developer_name,String since,String until,String token,String category,String repoId);

    /**
     * 获取在issueTracker用户下，所有项目中某个开发者在一段时间内的代码提交次数
     * @param developer_name 开发者的提交名字
     * @param since 提交的起始时间
     * @param until 提交的终止时间
     * @param token 用户token
     * @param category 工具类别
     * @param repoId repo唯一ID
     * @return 提交次数
     */
    int getCommitCountByDurationAndDeveloperName(String developer_name,String since,String until,String token,String category,String repoId);

    /**
     * 获取在issueTracker用户下，该用户参与过的项目列表，以及各个项目的活跃度
     * @param developer_name 开发者的提交名字
     * @param token 用户token
     * @param category 工具类别
     * @return 项目以及项目的活跃度
     */
    Object getRepoListByDeveloperName(String developer_name,String token,String category);


    /**
     * 获取在issueTracker用户下，该用户最近一定数量的commit的代码质量度量信息
     * @param developer_name 开发者的提交名字
     * @param token 用户token
     * @param category 工具类别
     * @param counts commit数量
     * @param project_name 项目名
     * @return 项目以及项目的活跃度
     */
    Object getQualityChangesByDeveloperName(String developer_name,String token,String category,int counts,String project_name );

    Object InsertData(String repoId);

    /**
     *
     * @param repo_id
     * @param since
     * @param until
     * @return 某段时间内，该项目中提交次数最多的前三名开发者的姓名以及对应的commit次数
     */
    Object getDeveloperRankByCommitCount(String repo_id, String since, String until);

    /**
     *
     * @param repo_id
     * @param since
     * @param until
     * @return 某段时间内，该项目中提交代码行数（LOC）最多的前三名开发者的姓名以及对应的LOC
     */
    Object getDeveloperRankByLoc(String repo_id, String since, String until);

    /**
     *
     * @param repo_id
     * @param since
     * @param until
     * @return 某段时间内，该项目每天的所有开发者提交commit的次数
     */
    Object getCommitCountsDaily(String repo_id, String since, String until);

    /**
     *
     * @param repo_id
     * @param since
     * @param until
     * @return 获取一段时间内，该repo所有开发者产生的LOC
     */
    Object getRepoLOCByDuration(String repo_id, String since, String until);

    /**
     *
     * @param repo_id
     * @param since
     * @param until
     * @return 某段时间内，该项目每天的所有开发者产生的LOC
     */
    Object getLOCDaily(String repo_id, String since, String until);

    /**
     *
     * @param repo_id
     * @param since
     * @param until
     * @return 某段时间内，该项目每天的所有开发者产生的LOC以及commit次数
     */
    Object getCommitCountLOCDaily(String repo_id, String since, String until);

    /**
     *
     * @param repo_id
     * @param granularity
     * @param developer_name
     * @return 根据时间粒度获取某段时间内，该项目、该开发者的addlines dellines
     */
    Object getDeveloperActivenessByGranularity(String repo_id, String granularity, String developer_name);

    /**
     *
     * @param repo_id
     * @param since
     * @param until
     * @param developer_name
     * @return 根据时间段获取该项目、该开发者的addlines dellines
     */
    Object getDeveloperActivenessByDuration(String repo_id, String since, String until, String developer_name);

    /**
     *
     * @param repo_id
     * @return 根据repoid，获取开发者姓名列表
     */
    Object getDeveloperListByRepoId(String repo_id);

    /**
     *
     * @param repoId
     * @param developer
     * @param beginDate
     * @param endDate
     * @param token
     * @param tool
     * @return 返回开发者雷达图数据
     */
    Object getPortrait(String repoId, String developer, String beginDate, String endDate, String token, String tool);

    /**
     * 根据repoId和起始commit，对项目进行度量方面的扫描，即将数据入库到repo_measure表中
     * @param repoId
     * @param startCommitId
     */
    void startMeasureScan(String repoId, String startCommitId);
}
