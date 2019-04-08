package cn.edu.fudan.clonevisualservice.mapper;


import cn.edu.fudan.clonevisualservice.domain.PackageInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageInfoMapper {
//    List<String> selectPackageNameSetByRepoIdAndCommitId(
//            @Param("p_repo_id") String repo_id,
//            @Param("p_commit_id") String commit_id );
    void insertPackageNameSetByRepoIdAndCommitId(PackageInfo packageInfo);

}
