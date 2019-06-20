package cn.edu.fudan.issueservice.service;

import cn.edu.fudan.issueservice.domain.Location;
import cn.edu.fudan.issueservice.domain.RawIssue;

import java.util.List;

/**
 * @author WZY
 * @version 1.0
 **/
public interface RawIssueService {

    void insertRawIssueList(List<RawIssue> list);

    void deleteRawIssueByRepoIdAndCategory(String repoId,String category);

    void batchUpdateIssueId(List<RawIssue> list);

    List<RawIssue> getRawIssueByCommitIDAndCategory(String repo_id,String commit_id,String category);

    List<RawIssue> getRawIssueByIssueId(String issueId);

    Object getCode(String project_id, String commit_id, String file_path);

    List<Location> getLocationsByRawIssueId(String raw_issue_id);

    List<RawIssue> getRawIssueByCommitAndFile(String repo_id,String commit_id,String category,String filePath);

    Object getRawIssueList(String issue_id,Integer page,Integer size);
}
