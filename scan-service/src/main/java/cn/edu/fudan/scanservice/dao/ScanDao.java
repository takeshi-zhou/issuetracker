package cn.edu.fudan.scanservice.dao;

import cn.edu.fudan.scanservice.domain.Scan;
import cn.edu.fudan.scanservice.mapper.ScanMapper;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public Object getTillCommitDateByProjectId(String projectId) {
        return scanMapper.getTillCommitDateByProjectId(projectId);
    }

    public List<String> getScannedCommits(String project_id) {
        List<Scan> scans = scanMapper.getScannedCommits(project_id);
        List<String> list = new ArrayList<>();
        for (Scan scan : scans) {
            list.add(scan.getCommit_id());
        }
        return list;
    }

    public List<Scan> getScans(String project_id){
        return scanMapper.getScannedCommits(project_id);
    }
}
