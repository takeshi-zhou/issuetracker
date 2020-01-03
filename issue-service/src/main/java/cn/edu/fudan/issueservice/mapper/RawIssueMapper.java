package cn.edu.fudan.issueservice.mapper;

import cn.edu.fudan.issueservice.domain.RawIssue;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@Repository
public interface RawIssueMapper {


    void insertRawIssueList(List<RawIssue> list);

    void deleteRawIssueByRepoIdAndCategory(@Param("repo_id") String repo_id,@Param("category")String category);

    void batchUpdateIssueId(List<RawIssue> list);

    Integer getIssueCountBeforeSpecificTime(@Param("account_id") String account_id, @Param("specificTime") String specificTime);

    List<RawIssue> getRawIssueByCommitIDAndCategory(@Param("repo_id") String repo_id,
                                                    @Param("category") String category,
                                                    @Param("commit_id") String commit_id);

    List<RawIssue> getRawIssueByCommitIDAndFile(@Param("repo_id") String repo_id,
                                                @Param("commit_id")String commit_id,
                                                @Param("category") String category,
                                                @Param("file")String file);

    List<RawIssue> getRawIssueByIssueId(@Param("issueId") String issueId);

    List<String> getTypesByCommit(@Param("category")String category,@Param("commit_id")String commit_id);

    Integer getNumberOfRemainingIssueBaseFile(@Param("repo_id") String repoId,@Param("commit_id") String commit,@Param("file_name") String fileName);

    Integer getNumberOfRemainingIssue(@Param("repo_id") String repoId, @Param("commit_id") String commit);

    List<WeakHashMap<String, String>> getRankOfFileBaseIssueQuantity(@Param("repo_id")String repoId, @Param("commit_id")String commitId);

    List<WeakHashMap<String, String>> getRankOfFileBaseDensity(@Param("repo_id")String repoId, @Param("commit_id")String commitId);

    Integer getNumberOfRemainingIssueBasePackage(@Param("repo_id")String repoId, @Param("commit_id")String commitId,@Param("package_name") String packageName);

    Integer getNumberOfRawIssuesByIssueId(@Param("issueId") String issueId);

    List<RawIssue> getRawIssueListByIssueId(Map<String, Object> map);

    List<RawIssue> getChangedRawIssues(@Param("issueId") String issueId);

    String getRawIssueCommitTimeByRepoIdAndCategory(@Param("repo_id") String repoId,
                                                @Param("commit_id")String commitId,
                                                @Param("category") String category
                                                );


    String getCommitIdWhichBeforeDesignatedTime(@Param("repo_id") String repoId,
                                                    @Param("commit_time")String commitTime,
                                                    @Param("category") String category
    );
}
