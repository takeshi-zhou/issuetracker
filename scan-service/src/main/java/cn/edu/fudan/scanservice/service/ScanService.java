package cn.edu.fudan.scanservice.service;


import cn.edu.fudan.scanservice.domain.Scan;
import com.alibaba.fastjson.JSONArray;

public interface ScanService {

    void insertOneScan(Scan scan);

    void deleteScanByRepoId(String repoId);

    void updateOneScan(Scan scan);

    Object getCommits(String project_id, Integer page, Integer size, Boolean is_whole);

    Object getScannedCommits(String repo_id);
}
