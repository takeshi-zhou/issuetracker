package cn.edu.fudan.issueservice.mapper;

import cn.edu.fudan.issueservice.domain.Issue;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;


@Repository
public interface IssueMapper {

    void insertIssueList(List<Issue> list);

    void deleteIssueByRepoIdAndCategory(@Param("repo_id")String repo_id,@Param("category")String category);

    void batchUpdateIssue(List<Issue> list);

    Issue getIssueByID(String uuid);

    int getSpecificIssueCount(Map<String, Object> map);

    List<Issue> getSpecificIssues(Map<String, Object> map);

    Integer getIssueCount(Map<String, Object> map);

    List<Issue> getIssuesByEndCommit(@Param("repo_id") String repo_id, @Param("category")String category,@Param("commit_id") String commit_id);

    List<Issue> getIssueList(Map<String, Object> map);

    List<String> getExistIssueTypes(@Param("category")String category);

    List<String> getIssueIdsByRepoIdAndCategory(@Param("repo_id")String repo_id,@Param("category")String category);

    Double getAvgEliminatedTime(@Param("list")List<String> list, @Param("repo_id")String repo_id, @Param("category")String category);

    Long getMaxAliveTime(@Param("list")List<String> list, @Param("repo_id")String repo_id, @Param("category")String category);

    void updateOneIssuePriority(@Param("uuid")String issueId, @Param("priority") int priority);

    List<Map<String, String>> getIssueIdAndPriority();

    Integer getMaxIssueDisplayId(@Param("repo_id") String repoId);

    int getIgnoredCountInMappedIssues(@Param("ignoreId")String ignoreId, @Param("list")List<String> list);

    // just for update old data
    void updateIssueDisplayId(@Param("uuid")String uuid, @Param("display_id") int displayId);
    List<String> getAllRepoId();
    List<String> getAllIssueIdByRepoId(@Param("repo_id") String repoId);


    List<String> getNotSolvedIssueListByTypeAndRepoId(@Param("repo_id") String repoId,@Param("type")  String type);

    List<Issue> getNotSolvedIssueAllListByCategoryAndRepoId(@Param("repo_id") String repoId,@Param("type")  String type);

    void batchUpdateIssueListPriority(@Param("list")List<String> issueUuid,@Param("priority") int priority);

    int getNumberOfNewIssueByDuration(@Param("repo_id") String repoId,@Param("start") String start,@Param("end") String end);

    int getNumberOfEliminateIssueByDuration(@Param("repo_id") String repoId,@Param("start") String start,@Param("end") String end);

    List<WeakHashMap<Object, Object>> getCommitNewIssue(@Param("start") String start, @Param("end") String end, @Param("repo_id") String repoId);

    List<Issue> getSonarIssueByRepoId(@Param("repo_id") String repoId,@Param("category")String category);

    Issue getIssueBySonarIssueKey(@Param("sonar_issue_id")String sonar_issue_id);

    void batchUpdateSonarIssues(List<Issue> list);
}
