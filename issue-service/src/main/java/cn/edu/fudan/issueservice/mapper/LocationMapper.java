package cn.edu.fudan.issueservice.mapper;



import cn.edu.fudan.issueservice.domain.Location;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LocationMapper {

     void insertLocationList(List<Location> list);

     void deleteLocationByProjectId(@Param("projectId")String projectId);

     List<Location> getLocations(@Param("uuid") String rawIssueId);

}
