package cn.edu.fudan.cloneservice.mapper;

import cn.edu.fudan.cloneservice.domain.CloneMeasure;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CloneMeasureMapper {

    CloneMeasure getCloneMeasure(@Param("repo_id") String repoId,
                                 @Param("commit_id") String commitId);

    void insertCloneMeasure(CloneMeasure cloneMeasure);

    List<CloneMeasure> getCloneMeasures(@Param("repo_id") String repoId);

    void deleteCloneMeasureByRepoId(@Param("repo_id") String repoId);
}
