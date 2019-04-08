package cn.edu.fudan.cloneservice.mapper;

import cn.edu.fudan.cloneservice.domain.PackageInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PackageNameMapper {
//    List<String> selectPackageNameSetByRepoIdAndCommitId(
//            @Param("p_repo_id") String repo_id,
//            @Param("p_commit_id") String commit_id );
    void insertPackageNameSetByRepoIdAndCommitId(PackageInfo packageInfo);
    String selectPackageNameSetByRepoIdAndCommitId(@Param("p_repo_id") String repo_id,
                                                  @Param("p_commit_id") String commit_id );

}
