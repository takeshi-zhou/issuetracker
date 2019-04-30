package cn.edu.fudan.measureservice.mapper;

import cn.edu.fudan.measureservice.domain.Package;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageMeasureMapper {

    void insertPackageMeasureDataList(List<Package> list);
}
