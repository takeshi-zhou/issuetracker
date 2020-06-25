package cn.edu.fudan.measureservice.service;

import cn.edu.fudan.measureservice.domain.dto.RepoResourceDTO;

public interface MeasureScanService {

    /**
     * 根据repoId和起始commit，对项目进行度量方面的扫描，即将数据入库
     * @param repoId
     * @param branch
     * @param beginCommit
     * @param toolName
     */
    void scanByJavancss(String repoId, String branch, String beginCommit, String toolName);

    /**
     * 根据repoId，获取项目的扫描状态
     * @param repoId
     * @return
     */
    Object getScanStatus(String repoId);


    Boolean scan(RepoResourceDTO repoResource, String branch, String beginCommit, String toolName);
}
