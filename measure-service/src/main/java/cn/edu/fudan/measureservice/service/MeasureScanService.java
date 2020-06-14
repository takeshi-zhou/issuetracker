package cn.edu.fudan.measureservice.service;

public interface MeasureScanService {

    /**
     * 根据repoId和起始commit，对项目进行度量方面的扫描，即将数据入库
     * @param repoId
     * @param branch
     * @param beginCommit
     */
    void scan(String repoId, String branch, String beginCommit);
}
