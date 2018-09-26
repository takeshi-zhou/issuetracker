package cn.edu.fudan.issueservice.mapper;

import cn.edu.fudan.issueservice.domain.Issue;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface IssueMapper {

    void insertIssueList(List<Issue> list);

    void deleteIssueByRepoId(String repo_id);

    void batchUpdateIssue(List<Issue> list);

    Issue getIssueByID(String uuid);

    Integer getIssueCount(Map<String, Object> map);

    List<String> getSolvedIssueIds(@Param("repo_id") String repo_id, @Param("commit_id") String commit_id);

    List<Issue> getIssueList(Map<String, Object> map);

    List<String> getExistIssueTypes();

    List<String> getIssueIdsByRepoId(String repo_id);
}
