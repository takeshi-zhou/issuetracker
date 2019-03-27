package cn.edu.fudan.cloneservice.mapper;

import cn.edu.fudan.cloneservice.domain.PackageScanStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PackageScanStatusMapper {


    String selectPackageScanStatusByRepoIdAndCommitId(@Param("p_repo_id") String repo_id,
                                              @Param("p_commit_id") String commit_id);
    String selectAllStatus();




}
