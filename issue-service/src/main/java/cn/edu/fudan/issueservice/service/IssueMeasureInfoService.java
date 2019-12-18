/**
 * @description: 代码度量信息
 * @author: fancying
 * @create: 2019-04-01 22:11
 **/
package cn.edu.fudan.issueservice.service;


import cn.edu.fudan.issueservice.domain.IssueCountDeveloper;
import cn.edu.fudan.issueservice.domain.IssueCountMeasure;
import cn.edu.fudan.issueservice.domain.IssueCountPo;
import cn.edu.fudan.issueservice.domain.statistics.CodeQualityResponse;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface IssueMeasureInfoService {

    //时间粒度  快照（commit）
    //空间粒度  开发者，文件，包，项目
    int numberOfRemainingIssue(String repoId, String commit, String spaceType, String detail);

    //时间粒度  日，周，月
    //空间粒度  开发者，项目
    int numberOfNewIssue(String duration, String spaceType, String detail);

    //时间粒度  快照（commit）
    int numberOfNewIssueByCommit(String repoId,String commitId,String spaceType,String category);

    //时间粒度  日，周，月
    //空间粒度  开发者，项目
    int numberOfEliminateIssue(String duration, String spaceType, String detail);

    //时间粒度  快照（commit）
    int numberOfEliminateIssueByCommit(String repoId,String commitId,String spaceType,String category);

    List<IssueCountPo> getIssueCountEachCommit(String repoId,String category,String since,String until);

    IssueCountMeasure getIssueCountMeasureByRepo(String repoId,String category,String since,String until);

    List<IssueCountDeveloper> getIssueCountMeasureByDeveloper(String repoId,String category,String since,String until);

    Object getNotSolvedIssueCountByCategoryAndRepoId(String repoId, String category,String commitId);

    /**
     *  根据条件获取返回的代码质量
     * @param developer   开发者
     * @param timeGranularity 时间粒度
     * @param since       从什么时候开始统计
     * @param until       统计到什么时候
     * @param repoId      repo的唯一id
     * @param tool        什么工具的分析结果
     * @param page        返回第几页的结果
     * @param ps          每页结果的大小
     * @return  返回符合条件的代码质量
     */
    Object getQualityChangesByCondition(String developer, String timeGranularity, String since, String until, String repoId, String tool, int page, int ps);
}