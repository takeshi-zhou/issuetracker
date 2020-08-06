package cn.edu.fudan.cloneservice.dao;

import cn.edu.fudan.cloneservice.domain.Scan;
import cn.edu.fudan.cloneservice.mapper.ScanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zyh
 * @date 2020/4/15
 */
@Repository
public class ScanDao {

    private ScanMapper scanMapper;

    @Autowired
    public void setScanMapper(ScanMapper scanMapper) {
        this.scanMapper = scanMapper;
    }

    public void insertOneScan(Scan scan) {
        scanMapper.insertOneScan(scan);
    }

    public void deleteScanByRepoIdAndCategory(String repoId,String category) {
        scanMapper.deleteScanByRepoIdAndCategory(repoId,category);
    }

    public void updateOneScan(Scan scan) {
        scanMapper.updateOneScan(scan);
    }

    public String getLatestScannedCommitId(String repo_id,String category) {
        return scanMapper.getLatestScannedCommitId(repo_id,category);
    }

    public Date getLastScannedCommitTime(String repo_id, String category){
        return scanMapper.getLastScannedCommitTime(repo_id, category);
    }

    public boolean isScanned(String repoId,String commit_id,String category) {
        Integer count = scanMapper.getScanCountByCommitIdAndCategory(repoId,commit_id,category);
        return count != null && count > 0;
    }

    public List<String> getScannedCommits(String repo_id, String category) {
        List<Scan> scans = scanMapper.getScannedCommits(repo_id,category);
        List<String> list = new ArrayList<>();
        for (Scan scan : scans) {
            list.add(scan.getCommit_id());
        }
        return list;
    }

    public List<Scan> getScans(String repo_id,String category) {
        return scanMapper.getScannedCommits(repo_id,category);
    }

    public Scan  getScanByCategoryAndRepoIdAndCommitId(String repo_id,String category,String commit_id){
        return scanMapper.getScanByCategoryAndRepoIdAndCommitId(repo_id,category,commit_id);
    }

    public List<Scan> getScanByRepoIdAndStatus(String repoId,String status) {
        return scanMapper.getScanByRepoIdAndStatus(repoId,status);
    }
}
