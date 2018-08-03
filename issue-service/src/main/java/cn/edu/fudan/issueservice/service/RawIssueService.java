package cn.edu.fudan.issueservice.service;

import cn.edu.fudan.issueservice.domain.RawIssue;

import java.util.List;

/**
 * @author WZY
 * @version 1.0
 **/
public interface RawIssueService {

    void insertRawIssueList(List<RawIssue> list);

    void deleteRawIssueByProjectId(String projectId);

    void batchUpdateIssueId(List<RawIssue> list);

    List<RawIssue> getRawIssueByCommitID(String commit_id);

    List<RawIssue> getRawIssueByIssueId( String issueId);
}
