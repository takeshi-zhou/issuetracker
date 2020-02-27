package cn.edu.fudan.issueservice.mapper;

import cn.edu.fudan.issueservice.domain.Issue;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;


@Repository
public interface IssueMapper {

    /**
     * insertIssueList
     *
     * @param list get issue list
     */
    void insertIssueList(List<Issue> list);

    /**
     * delete issue by repo id and category
     *
     * @param repo_id get issue repo id
     * @param category get issue category
     */
    void deleteIssueByRepoIdAndCategory(@Param("repo_id")String repo_id,@Param("category")String category);

    /**
     * batch update issue
     *
     * @param list get issue list
     */
    void batchUpdateIssue(List<Issue> list);

    /**
     * get issue by id
     *
     * @param uuid get issue uuid
     * @return Issue
     */
    Issue getIssueByID(String uuid);

    /**
     * get specific issue count
     *
     * @param map get issue map
     * @return int
     */
    int getSpecificIssueCount(Map<String, Object> map);

    /**
     * get specific issues
     *
     * @param map get issue map
     * @return List<Issue>
     */
    List<Issue> getSpecificIssues(Map<String, Object> map);

    /**
     * get issue count
     *
     * @param map get issue map
     * @return Integer
     */
    Integer getIssueCount(Map<String, Object> map);

    /**
     * get issues by end commit
     *
     * @param repo_id get issue repo id
     * @param category get issue category
     * @param commit_id get issue commit id
     * @return List<Issue>
     */
    List<Issue> getIssuesByEndCommit(@Param("repo_id") String repo_id, @Param("category")String category,@Param("commit_id") String commit_id);

    /**
     * get issue list
     *
     * @param map get issue map
     * @return List<Issue>
     */
    List<Issue> getIssueList(Map<String, Object> map);

    /**
     * get exist issue types
     *
     * @param category get issue category
     * @return List<Issue>
     */
    List<String> getExistIssueTypes(@Param("category")String category);

    /**
     * get issue ids by repo id and category
     *
     * @param repo_id get issue repo id
     * @param category get issue category
     * @return List<Issue>
     */
    List<String> getIssueIdsByRepoIdAndCategory(@Param("repo_id")String repo_id,@Param("category")String category);

    /**
     * get avg eliminated time
     *
     * @param list get issue repo list
     * @param repo_id get issue repo id
     * @param category get issue category
     * @return Double
     */
    Double getAvgEliminatedTime(@Param("list")List<String> list, @Param("repo_id")String repo_id, @Param("category")String category);

    /**
     * get max dlive time
     *
     * @param list get issue repo list
     * @param repo_id get issue repo id
     * @param category get issue category
     * @return Long
     */
    Long getMaxAliveTime(@Param("list")List<String> list, @Param("repo_id")String repo_id, @Param("category")String category);

    /**
     * update one issue priority
     *
     * @param issueId get issue id
     * @param priority get issue priority
     */
    void updateOneIssuePriority(@Param("uuid")String issueId, @Param("priority") int priority);

    /**
     * get issue id and priority
     *
     * @return List<Map<String, String>>
     */
    List<Map<String, String>> getIssueIdAndPriority();

    /**
     * get max issue display id
     *
     * @param repoId get issue repo id
     * @return Integer
     */
    Integer getMaxIssueDisplayId(@Param("repo_id") String repoId);

    /**
     * get ignored count in mapped issues
     *
     * @param ignoreId get issue ignore id
     * @param list get issue ignore list
     * @return int
     */
    int getIgnoredCountInMappedIssues(@Param("ignoreId")String ignoreId, @Param("list")List<String> list);


    // just for update old data
    /**
     * update issue display id
     *
     * @param uuid get issue uuid
     * @param displayId get issue display id
     */
    void updateIssueDisplayId(@Param("uuid")String uuid, @Param("display_id") int displayId);

    /**
     * get all repo id
     *
     * @return List<String>
     */
    List<String> getAllRepoId();

    /**
     * get all issue id by repo id
     *
     * @param repoId get issue repo id
     * @return List<String>
     */
    List<String> getAllIssueIdByRepoId(@Param("repo_id") String repoId);

    /**
     * get not solved issue list by type and repo id
     *
     * @param repoId get issue repo id
     * @param type get issue type
     * @return List<String>
     */
    List<String> getNotSolvedIssueListByTypeAndRepoId(@Param("repo_id") String repoId,@Param("type")  String type);

    /**
     * get not solved issue all list by category and repo id
     *
     * @param repoId get issue repo id
     * @param type get issue type
     * @return List<Issue>
     */
    List<Issue> getNotSolvedIssueAllListByCategoryAndRepoId(@Param("repo_id") String repoId,@Param("type")  String type);

    /**
     * batch update issue list priority
     *
     * @param issueUuid get issue uuid
     * @param priority get issue priority
     */
    void batchUpdateIssueListPriority(@Param("list")List<String> issueUuid,@Param("priority") int priority);

    /**
     * get number of new issue by duration
     *
     * @param repoId get issue repo id
     * @param start get issue start
     * @param end get issue end
     * @return int
     */
    int getNumberOfNewIssueByDuration(@Param("repo_id") String repoId,@Param("start") String start,@Param("end") String end);

    /**
     * get number of eliminate issue by duration
     *
     * @param repoId get issue repo id
     * @param start get issue start
     * @param end get issue end
     * @return int
     */
    int getNumberOfEliminateIssueByDuration(@Param("repo_id") String repoId,@Param("start") String start,@Param("end") String end);

    /**
     * get commit new issue
     *
     * @param start get issue start
     * @param end get issue end
     * @param repoId get issue repo id
     * @return List<WeakHashMap<Object, Object>>
     */
    List<WeakHashMap<Object, Object>> getCommitNewIssue(@Param("start") String start, @Param("end") String end, @Param("repo_id") String repoId);

    /**
     * get sonar issue by repo id
     *
     * @param repoId get issue repo id
     * @param category get issue category
     * @return List<Issue>
     */
    List<Issue> getSonarIssueByRepoId(@Param("repo_id") String repoId,@Param("category")String category);

    /**
     * get issue by sonar issue key
     *
     * @param sonar_issue_id get sonar issue id
     * @return Issue
     */
    Issue getIssueBySonarIssueKey(@Param("sonar_issue_id")String sonar_issue_id);

    List<Issue> getIssuesByIssueIds(List<String> issueIds);

    /**
     * batch update sonar issues
     *
     * @param list get issue list
     */
    void batchUpdateSonarIssues(List<Issue> list);

    /**
     * get all commit by repo id
     *
     * @param repoId
     * @return commit id list
     */
    List<String> getCommitIds(@Param("repo_id") String repoId,@Param("since") String since,@Param("until") String until);

    /**
     * 获取指定repo 与 category 存在无效消除的 issue 列表
     * @param repoId
     * @param category
     * @return
     */
    List<Issue> getHaveNotAdoptEliminateIssuesByCategoryAndRepoId(@Param("repo_id") String repoId,@Param("category")String category);
}
