package cn.edu.fudan.scanservice.service;


import cn.edu.fudan.scanservice.domain.Scan;
import com.alibaba.fastjson.JSONArray;

public interface ScanService {

    void insertOneScan(Scan scan);

    void deleteScanByProjectId(String projectId) ;

    void updateOneScan(Scan scan);

    String getLatestScannedCommitId(String project_id);

    Object getTillCommitDateByProjectId(String projectId);

    JSONArray getScannedCommits(String project_id);
}
