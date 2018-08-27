package cn.edu.fudan.issueservice.service;


import cn.edu.fudan.issueservice.domain.Issue;

import java.util.List;
import java.util.Map;

public interface IssueService {

     void insertIssueList(List<Issue> list);

     void deleteIssueByProjectId(String projectId);

     void batchUpdateIssue(List<Issue> list);

     Issue getIssueByID(String uuid);

     Object getIssueList(Map<String, Object> map);

     Object getDashBoardInfo(String duration,String userToken);

     void startMapping(String project_id, String pre_commit_id, String current_commit_id);

}
