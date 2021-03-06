package cn.edu.fudan.issueservice.dao;

import cn.edu.fudan.issueservice.domain.RawIssue;
import cn.edu.fudan.issueservice.mapper.RawIssueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

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

    public RawIssue getRawIssueById(String rawIssueId) {
        return rawIssueMapper.getRawIssueById(rawIssueId);
    }

    public void deleteRawIssueByRepoIdAndCategory(String repoId,String category) {
        rawIssueMapper.deleteRawIssueByRepoIdAndCategory(repoId,category);
    }

    public void batchUpdateIssueIdAndStatus(List<RawIssue> list) {
        rawIssueMapper.batchUpdateIssueIdAndStatus(list);
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

    public List<WeakHashMap<String,String>> getRankOfFileBaseIssueQuantity(String repoId, String commitId) {
        return rawIssueMapper.getRankOfFileBaseIssueQuantity(repoId, commitId);
    }

    public List<WeakHashMap<String,String>> getRankOfFileBaseDensity(String repoId, String commitId) {
        return rawIssueMapper.getRankOfFileBaseDensity(repoId, commitId);
    }

    public Map<String, Integer> getRepoAndIssueNum(Map repoCommit) {
        return null;
    }

    public int getNumberOfRemainingIssueBasePackage(String repoId, String commit, String packageName) {
        return rawIssueMapper.getNumberOfRemainingIssueBasePackage(repoId, commit, packageName);
    }

    public int getNumberOfRawIssuesByIssueIdAndStatus(String issueId,List status) {
        return rawIssueMapper.getNumberOfRawIssuesByIssueIdAndStatus(issueId,status);
    }

    public List<RawIssue> getRawIssueListByIssueId(Map<String, Object> map) {
        return rawIssueMapper.getRawIssueListByIssueId(map);
    }

    public List<String> getRawIssueIdByCommitId(String repoId, String commit, String category){
        return rawIssueMapper.getRawIssueIdByCommitId(repoId,commit,category);
    }

    public String getLatestScannedCommitId(String repoId, String category){
        return rawIssueMapper.getLatestScannedCommitId(repoId,category);
    }

    /**
     * 根据issue uuid 获取 location发生变化的rawIssue列表
     * @param issueId
     * @return
     */
    public List<RawIssue> getChangedRawIssues(String issueId) {
        return rawIssueMapper.getChangedRawIssues(issueId);
    }


    /**
     * 获取rawIssue 表中指定commit的前一条commit id
     * @param repoId
     * @param category
     * @param currentCommitId
     * @return
     */
    public String getPreCommitIdByCurrentCommitId(String repoId,String category,String currentCommitId){
        String commitTime = rawIssueMapper.getRawIssueCommitTimeByRepoIdAndCategory(repoId,currentCommitId,category);
        return rawIssueMapper.getCommitIdWhichBeforeDesignatedTime(repoId,commitTime,category);
    }
}
