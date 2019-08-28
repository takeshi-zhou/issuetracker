package cn.edu.fudan.cloneservice.mapper;

import cn.edu.fudan.cloneservice.domain.RepoMeasure;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RepoMeasureMapper {



    RepoMeasure getMeasureDataByRepoIdCommitId(@Param("repo_id") String repo_id,
                                               @Param("commit_id") String commit_id);


}
