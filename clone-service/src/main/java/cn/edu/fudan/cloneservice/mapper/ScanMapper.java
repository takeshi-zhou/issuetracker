package cn.edu.fudan.cloneservice.mapper;


import cn.edu.fudan.cloneservice.domain.Scan;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author zyh
 * @date 2020/4/15
 */
@Repository
public interface ScanMapper {

    Integer getScanCountByCommitIdAndCategory(@Param("repo_id") String repo_id, @Param("commit_id")String commit_id, @Param("category")String category);

    void insertOneScan(Scan scan);

    void deleteScanByRepoIdAndCategory(@Param("repo_id") String repo_id,@Param("category")String category);

    void updateOneScan(Scan scan);

    String getLatestScannedCommitId(@Param("repo_id") String repo_id,@Param("category")String category);

    Date getLastScannedCommitTime(@Param("repo_id") String repo_id, @Param("category")String category);

    List<Scan> getScannedCommits(@Param("repo_id") String repo_id, @Param("category")String category);

    Scan  getScanByCategoryAndRepoIdAndCommitId(@Param("repo_id") String repo_id,@Param("category") String category,@Param("commit_id") String commit_id);

    List<Scan> getScanByRepoIdAndStatus(@Param("repo_id") String repoId,@Param("status")String status);
}
