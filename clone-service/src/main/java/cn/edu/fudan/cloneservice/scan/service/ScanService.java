package cn.edu.fudan.cloneservice.scan.service;

import cn.edu.fudan.cloneservice.scan.domain.CloneRepo;

/**
 * @author zyh
 * @date 2020/5/25
 */
public interface ScanService {

    /**
     * 项目clone扫描入口
     * @param repoId repo id
     * @param startCommitId 初始 commit id
     */
    void cloneScan(String repoId, String startCommitId);

    /**
     * 删除对应repo id所属的所有clone信息
     * @param repoId
     */
    void deleteCloneScan(String repoId);

    /**
     * 获取最新的clone repo
     * @param repoId repo id
     * @return clone repo
     */
    CloneRepo getLatestCloneRepo(String repoId);
}
