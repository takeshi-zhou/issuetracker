package cn.edu.fudan.measureservice.mapper;

import cn.edu.fudan.measureservice.domain.RepoMeasure;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RepoMeasureMapper {

    int sameMeasureOfOneCommit(@Param("repo_id")String repo_id,@Param("commit_id")String commit_id);

    void insertOneRepoMeasure(RepoMeasure repoMeasure);

    RepoMeasure getLatestMeasureData(@Param("repo_id")String repo_id);

    RepoMeasure getFirstMeasureDataAfterDuration(@Param("repo_id")String repo_id,@Param("time_line") Date time_line);

    List<RepoMeasure> getRepoMeasureBetween(@Param("repo_id")String repo_id,@Param("since")String since,@Param("until")String util);
}
