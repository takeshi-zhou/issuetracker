package cn.edu.fudan.scanservice.dao;

import cn.edu.fudan.scanservice.domain.Scan;
import cn.edu.fudan.scanservice.mapper.ScanMapper;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

    public JSONArray getScannedCommits(String project_id) {
        List<Scan> scans = scanMapper.getScannedCommits(project_id);
        JSONArray jsonArray = new JSONArray();

        for (Scan scan : scans) {
            jsonArray.add(scan);
        }
        return jsonArray;
    }
}
