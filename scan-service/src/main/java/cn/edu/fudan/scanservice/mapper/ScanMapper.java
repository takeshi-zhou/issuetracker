package cn.edu.fudan.scanservice.mapper;

import cn.edu.fudan.scanservice.domain.Scan;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ScanMapper {

    Integer getScanCountByCommitIdAndCategory(@Param("repo_id") String repo_id,@Param("commit_id")String commit_id,@Param("category")String category);

    void insertOneScan(Scan scan);

    void deleteScanByRepoIdAndCategory(@Param("repo_id") String repo_id,@Param("category")String category);

    void updateOneScan(Scan scan);

    String getLatestScannedCommitId(@Param("repo_id") String repo_id,@Param("category")String category);

    Date getLastScannedCommitTime(@Param("repo_id") String repo_id,@Param("category")String category);

    List<Scan> getScannedCommits(@Param("repo_id") String repo_id,@Param("category")String category);
}
