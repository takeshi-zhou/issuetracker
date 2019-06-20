/**
 * @description: 代码度量信息
 * @author: fancying
 * @create: 2019-04-01 22:11
 **/
package cn.edu.fudan.issueservice.service;


import cn.edu.fudan.issueservice.domain.IssueCountDeveloper;
import cn.edu.fudan.issueservice.domain.IssueCountMeasure;
import cn.edu.fudan.issueservice.domain.IssueCountPo;

import java.util.List;

public interface IssueMeasureInfoService {

    //时间粒度  快照（commit）
    //空间粒度  开发者，文件，包，项目
    int numberOfRemainingIssue(String repoId, String commit, String spaceType, String detail);

    //时间粒度  日，周，月
    //空间粒度  开发者，项目
    int numberOfNewIssue(String duration, String spaceType, String detail);

    //时间粒度  日，周，月
    //空间粒度  开发者，项目
    int numberOfEliminateIssue(String duration, String spaceType, String detail);

    List<IssueCountPo> getIssueCountEachCommit(String repoId,String category,String since,String until);

    IssueCountMeasure getIssueCountMeasureByRepo(String repoId,String category,String since,String until);

    List<IssueCountDeveloper> getIssueCountMeasureByDeveloper(String repoId,String category,String since,String until);
}