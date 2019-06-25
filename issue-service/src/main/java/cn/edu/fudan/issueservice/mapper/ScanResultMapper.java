package cn.edu.fudan.issueservice.mapper;

import cn.edu.fudan.issueservice.domain.IssueCountDeveloper;
import cn.edu.fudan.issueservice.domain.IssueCountPo;
import cn.edu.fudan.issueservice.domain.ScanResult;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScanResultMapper {

    void addOneScanResult(ScanResult scanResult);

    void deleteScanResultsByRepoIdAndCategory(@Param("repo_id")String repo_id,@Param("category") String category);

    List<IssueCountPo> getScanResultsGroupByDay(@Param("list") List<String> repoIds, @Param("category") String category, @Param("start") String start, @Param("end") String end);

    List<IssueCountPo> getScanResultsEachCommit(@Param("repo_id")String repo_id,@Param("category") String category, @Param("start") String start, @Param("end") String end);

    List<IssueCountDeveloper> getScanResultsEachDeveloper(@Param("repo_id")String repo_id, @Param("category") String category, @Param("start") String start, @Param("end") String end);

    IssueCountPo getMergedScanResult(@Param("list") List<String> repoIds, @Param("category") String category, @Param("start") String start, @Param("end") String end);
}
