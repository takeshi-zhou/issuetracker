package cn.edu.fudan.measureservice.mapper;

import cn.edu.fudan.measureservice.domain.Package;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageMeasureMapper {

    void insertPackageMeasureDataList(List<Package> list);

    int samePackageMeasureExist(@Param("repo_id")String repo_id,@Param("commit_id")String commit_id,@Param("name")String name);
}
