package cn.edu.fudan.measureservice.service;

public interface MeasureDevHistoryService {

    /**
     * 根据repoId和起止时间段，查询commit info 返回给前端
     * @param repoId
     * @param beginDate
     * @param endDate
     */
    Object getDevHistoryCommitInfo(String repoId, String beginDate, String endDate);
}
