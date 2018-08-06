package cn.edu.fudan.scanservice.dao;

import cn.edu.fudan.scanservice.domain.Scan;
import cn.edu.fudan.scanservice.mapper.ScanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ScanDao {


    private ScanMapper scanMapper;

    @Autowired
    public void setScanMapper(ScanMapper scanMapper) {
        this.scanMapper = scanMapper;
    }

    public void insertOneScan(Scan scan){
        scanMapper.insertOneScan(scan);
    }

    public void deleteScanByProjectId(String projectId){
        scanMapper.deleteScanByProjectId(projectId);
    }

    public void updateOneScan(Scan scan){
        scanMapper.updateOneScan(scan);
    }

    public String getLatestScannedCommitId(String project_id){
        return scanMapper.getLatestScannedCommitId(project_id);
    }

    public boolean isScanned(String commit_id){
        Integer count=scanMapper.getScanCountByCommitId(commit_id);
        return count!=null&&count>0;
    }

}
