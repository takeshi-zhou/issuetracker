package cn.edu.fudan.commitservice.service.impl;

import cn.edu.fudan.commitservice.dao.CommitDao;
import cn.edu.fudan.commitservice.domain.Commit;
import cn.edu.fudan.commitservice.service.CommitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CommitServiceImpl implements CommitService {


    private CommitDao commitDao;

    @Autowired
    public void setCommitDao(CommitDao commitDao) {
        this.commitDao = commitDao;
    }

    @Override
    public List<Commit> getCommitList(String project_id) {
        return commitDao.getCommitByProjectID(project_id);
    }

    @Override
    public Commit getCommitByCommitId(String commit_id) {
        return commitDao.getCommitByCommitId(commit_id);
    }

    @Override
    public void updateCommitScanStatus(Commit commit) {
        commitDao.updateCommitScanStatus(commit);
    }

    @Override
    public void deleteCommitByProjectId(String projectId) {
        commitDao.deleteCommitByProjectId(projectId);
    }

    @Override
    public Date getCommitDate(String commit_id) {
        return commitDao.getCommitDate(commit_id);
    }

    @Override
    public Date getTillCommitDate(String repo_id) {
        return commitDao.getTillCommitDate(repo_id);
    }

    @Override
    public List<Commit> getScannedCommits(String project_id) {
        return commitDao.getScannedCommits(project_id);
    }


}
