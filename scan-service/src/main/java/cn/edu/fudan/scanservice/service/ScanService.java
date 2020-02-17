package cn.edu.fudan.scanservice.service;


import cn.edu.fudan.scanservice.domain.Scan;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

public interface ScanService {

    void insertOneScan(Scan scan);

    void deleteScanByRepoIdAndCategory(String repoId,String category);

    void updateOneScan(Scan scan);

    Object getCommits(String project_id, Integer page, Integer size, Boolean is_whole,String category);

    Object getScannedCommits(String repo_id,String category);

    String getLatestScannedCommitId(String repo_id,String category);

    String getNextScannedCommitID(String repo_id,String category,String commitId);

    String getPreviousScannedCommitID(String repo_id,String category,String commitId);

    Scan getScanByCategoryAndRepoIdAndCommitId(String repo_id,String category,String commit_id);

    /**
     * 通过当前的commit id ，获取parent commit扫描成功的commit id
     * @param repoId
     * @param category
     * @param commitId
     * @return
     */
    List<String> getPreScannedCommitByCurrentCommit( String repoId, String category, String commitId);

    /**
     * 通过当前的commit id ，获取最新一条扫描失败的commit id以及开发者名字
     * @param repoId
     * @param category
     * @param commitId
     * @return
     */
    String getLatestScanFailedCommitIdAndDeveloper( String repoId, String category, String commitId);
}
