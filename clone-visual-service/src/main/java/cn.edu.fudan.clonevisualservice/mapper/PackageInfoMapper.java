package cn.edu.fudan.clonevisualservice.mapper;


import cn.edu.fudan.clonevisualservice.domain.PackageInfo;
import org.apache.ibatis.annotations.Param;
import org.junit.Test;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageInfoMapper {

    String selectTest(@Param("repoId") String repo_id);


}
