package cn.edu.fudan.issueservice.dao;

import cn.edu.fudan.issueservice.domain.RawIssue;
import cn.edu.fudan.issueservice.mapper.RawIssueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author WZY
 * @version 1.0
 **/
@Repository
public class RawIssueDao {

    private RawIssueMapper rawIssueMapper;

    @Autowired
    public void setRawIssueMapper(RawIssueMapper rawIssueMapper) {
        this.rawIssueMapper = rawIssueMapper;
    }

    public void insertRawIssueList(List<RawIssue> list) {
        rawIssueMapper.insertRawIssueList(list);
    }

    public void deleteRawIssueByRepoIdAndCategory(String repoId,String category) {
        rawIssueMapper.deleteRawIssueByRepoIdAndCategory(repoId,category);
    }

    public void batchUpdateIssueId(List<RawIssue> list) {
        rawIssueMapper.batchUpdateIssueId(list);
    }

    public Integer getIssueCountBeforeSpecificTime(String account_id, String specificTime) {
        return rawIssueMapper.getIssueCountBeforeSpecificTime(account_id, specificTime);
    }

    public List<RawIssue> getRawIssueByCommitIDAndCategory(String repo_id,String category,String commit_id) {
        return rawIssueMapper.getRawIssueByCommitIDAndCategory(repo_id,category, commit_id);
    }

    public List<RawIssue> getRawIssueByIssueId(String issueId) {
        return rawIssueMapper.getRawIssueByIssueId(issueId);
    }

    public List<String> getTypesByCommit(String category,String commit_id){
        return rawIssueMapper.getTypesByCommit(category, commit_id);
    }

    public List<RawIssue> getRawIssueByCommitIDAndFile(String repo_id,String commit_id,String category,String file){
        return rawIssueMapper.getRawIssueByCommitIDAndFile(repo_id,commit_id, category, file);
    }

    public Integer getNumberOfRemainingIssue(String repoId, String commit) {
        return rawIssueMapper.getNumberOfRemainingIssue(repoId, commit);
    }

    public Integer getNumberOfRemainingIssueBaseFile(String repoId, String commit, String fileName) {
        return rawIssueMapper.getNumberOfRemainingIssueBaseFile(repoId, commit, fileName);
    }
}
