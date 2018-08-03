package cn.edu.fudan.commitservice.dao;

import cn.edu.fudan.commitservice.domain.Commit;
import cn.edu.fudan.commitservice.mapper.CommitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class CommitDao {


    private CommitMapper commitMapper;

    @Autowired
    public void setCommitMapper(CommitMapper commitMapper) {
        this.commitMapper = commitMapper;
    }

    public List<Commit> getCommitByProjectID(String project_id){
        return commitMapper.getCommitByProjectID(project_id);
    }

    public Commit getCommitByCommitId(String commit_id){
        return commitMapper.getCommitByCommitId(commit_id);
    }

    public void updateCommitScanStatus(Commit commit){
        commitMapper.updateCommitScanStatus(commit);
    }

    public void deleteCommitByProjectId(String projectId) {
        commitMapper.deleteCommitByProjectId(projectId);
    }

    public Date getCommitDate(String commit_id){
        return commitMapper.getCommitDate(commit_id);
    }

    public Date getTillCommitDate(String repo_id){
        return commitMapper.getTillCommitDate(repo_id);
    }

    public List<Commit> getScannedCommits(String project_id){
        return commitMapper.getScannedCommits(project_id);
    }

}
