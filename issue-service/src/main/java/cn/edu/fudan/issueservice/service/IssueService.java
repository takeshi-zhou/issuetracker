package cn.edu.fudan.issueservice.service;


import cn.edu.fudan.issueservice.domain.Issue;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface IssueService {

     void insertIssueList(List<Issue> list);

     void deleteIssueByProjectId(String projectId);

     void batchUpdateIssue(List<Issue> list);

     Issue getIssueByID(String uuid);

     Object getIssueList(String project_id,Integer page,Integer size);

     Object getFilteredIssueList(JSONObject requestParam);

     Object getDashBoardInfo(String duration,String project_id,String userToken);

     Object getStatisticalResults(Integer month, String project_id, String userToken);

     void startMapping(String project_id, String pre_commit_id, String current_commit_id);

}
