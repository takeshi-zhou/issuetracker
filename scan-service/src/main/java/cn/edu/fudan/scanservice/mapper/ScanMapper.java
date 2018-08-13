package cn.edu.fudan.scanservice.mapper;

import cn.edu.fudan.scanservice.domain.Scan;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ScanMapper {

     Integer getScanCountByCommitId(String commit_id);

     void insertOneScan(Scan scan);

     void deleteScanByProjectId(@Param("projectId")String projectId) ;

     void updateOneScan(Scan scan);

     String getLatestScannedCommitId(String project_id);

    Object getTillCommitDateByProjectId(@Param("projectId") String projectId);
}
