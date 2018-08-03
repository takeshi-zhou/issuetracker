package cn.edu.fudan.issueservice.service.impl;

import cn.edu.fudan.issueservice.dao.RawIssueDao;
import cn.edu.fudan.issueservice.domain.RawIssue;
import cn.edu.fudan.issueservice.service.RawIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author WZY
 * @version 1.0
 **/
@Service
public class RawIssueServiceImpl implements RawIssueService {

    private RawIssueDao rawIssueDao;

    @Autowired
    public void setRawIssueDao(RawIssueDao rawIssueDao) {
        this.rawIssueDao = rawIssueDao;
    }

    @Override
    public void insertRawIssueList(List<RawIssue> list) {
        rawIssueDao.insertRawIssueList(list);
    }

    @Override
    public void deleteRawIssueByProjectId(String projectId) {
        rawIssueDao.deleteRawIssueByProjectId(projectId);
    }

    @Override
    public void batchUpdateIssueId(List<RawIssue> list) {
        rawIssueDao.batchUpdateIssueId(list);
    }

    @Override
    public List<RawIssue> getRawIssueByCommitID(String commit_id) {
        return rawIssueDao.getRawIssueByCommitID(commit_id);
    }

    @Override
    public List<RawIssue> getRawIssueByIssueId(String issueId) {
        return rawIssueDao.getRawIssueByIssueId(issueId);
    }
}
