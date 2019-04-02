package cn.edu.fudan.measureservice.mapper;

import cn.edu.fudan.measureservice.domain.RepoMeasure;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoMeasureMapper {

    void insertOneRepoMeasure(RepoMeasure repoMeasure);
}
