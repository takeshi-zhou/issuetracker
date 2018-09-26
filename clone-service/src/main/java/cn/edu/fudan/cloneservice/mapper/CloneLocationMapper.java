package cn.edu.fudan.cloneservice.mapper;

import cn.edu.fudan.cloneservice.domain.CloneLocation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CloneLocationMapper {

    void insertCloneLocationList(List<CloneLocation> list);
}
