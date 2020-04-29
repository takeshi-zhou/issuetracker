package cn.edu.fudan.cloneservice.mapper;


import cn.edu.fudan.cloneservice.domain.CloneLocation;
import cn.edu.fudan.cloneservice.domain.Location;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LocationMapper {
    List<Location> getLocations(@Param("uuid") String rawIssueId);


    List<Location> getLocationsByCommitIdAndRepoId(@Param("repo_id") String repoId,
                                                   @Param("commit_id") String commitId);

    List<CloneLocation> getCloneLocations(@Param("repoId") String repoId,
                                          @Param("commitId") String commitId);
}
