package cn.edu.fudan.scanservice.service;


import cn.edu.fudan.scanservice.domain.Scan;

public interface ScanService {

    void insertOneScan(Scan scan);

    void deleteScanByProjectId(String projectId) ;

    void updateOneScan(Scan scan);

    String getLatestScannedCommitId(String project_id);

    Object getTillCommitDateByProjectId(String projectId);
}
