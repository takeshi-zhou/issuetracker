package cn.edu.fudan.cloneservice.mapper;

import cn.edu.fudan.cloneservice.domain.PackageScanStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface PackageScanStatusMapper {

    @Select("select * from ce_scan_status where repo_id = #{p_repo_id} and p_commit_id = #{p_commit_id}")
    PackageScanStatus selectPackageScanStatus(@Param("p_repo_id") String repo_id,
                                              @Param("p_commit_id") String commit_id);

}
