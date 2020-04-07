package cn.edu.fudan.issueservice.service;


import cn.edu.fudan.issueservice.domain.Issue;
import cn.edu.fudan.issueservice.domain.IssueParam;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface IssueService {

    void insertIssueList(List<Issue> list);

    void deleteIssueByRepoIdAndCategory(String repoId, String category);

    void deleteScanResultsByRepoIdAndCategory(String repo_id, String category);

    void batchUpdateIssue(List<Issue> list);

    Issue getIssueByID(String uuid);

    Object getIssueList(String project_id, Integer page, Integer size, String category);

    Object getFilteredIssueList(JSONObject requestParam);

    Object getSpecificIssues(IssueParam issueParam,String userToken);

    Object getDashBoardInfo(String duration, String project_id, String userToken, String category);

    Object getStatisticalResults(Integer month, String project_id, String userToken, String category);

    Object getExistIssueTypes(String category);

    Object getAliveAndEliminatedInfo(String project_id, String category);

    void startMapping(String repo_id, String pre_commit_id, String current_commit_id, String category);

    void updateIssueCount(String time);

    Object getNewTrend(Integer month, String project_id, String userToken, String category);

    void updatePriority(String issueId, String priority,String token);

    void updateStatus(String issueId, String status,String token);

    void updateIssueType(String issueId, String type, String token,String tool);

    void batchUpdateIssueListPriority(List<String> issueUuid, Integer priority);

    List<String> getNotSolvedIssueListByTypeAndRepoId(String repoId, String type);

    /**
     *
     * @param repo_id
     * @param since
     * @param until
     * @param category
     * @return 项目详情页面的issueCount每日数据
     */
    Object getRepoIssueCounts(String repo_id, String since, String until, String category);


}
