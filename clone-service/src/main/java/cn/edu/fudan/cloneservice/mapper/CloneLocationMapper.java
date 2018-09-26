package cn.edu.fudan.cloneservice.mapper;

import cn.edu.fudan.cloneservice.domain.CloneLocation;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CloneLocationMapper {

    void insertCloneLocationList(List<CloneLocation> list);

    List<CloneLocation> getCloneLocation(@Param("clone_rawIssue_id") String clone_rawIssue_id);
}
