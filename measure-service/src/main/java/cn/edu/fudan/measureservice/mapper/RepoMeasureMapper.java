package cn.edu.fudan.measureservice.mapper;

import cn.edu.fudan.measureservice.domain.RepoMeasure;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface RepoMeasureMapper {

    void insertOneRepoMeasure(RepoMeasure repoMeasure);

    RepoMeasure getLatestMeasureData(@Param("repo_id")String repo_id);

    RepoMeasure getFirstMeasureDataAfterDuration(@Param("repo_id")String repo_id,@Param("time_line") Date time_line);
}
