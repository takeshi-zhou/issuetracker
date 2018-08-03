package cn.edu.fudan.commitservice.mapper;

import cn.edu.fudan.commitservice.domain.Commit;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CommitMapper {

     void deleteCommitByProjectId(@Param("projectId") String projectId) ;

     List<Commit> getCommitByProjectID(String project_id);

     Commit getCommitByCommitId(String commit_id);

     Date getCommitDate(String commit_id);

     Date getTillCommitDate(String repo_id);

     void updateCommitScanStatus(Commit commit);

     List<Commit> getScannedCommits(String project_id);

}
