package cn.edu.fudan.cloneservice.mapper;

import cn.edu.fudan.cloneservice.domain.Commit;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RepoCommitMapper {
    List<Commit> selectCommitByRepoId(@Param("repoId")  String repo_id);

    List<Commit> selectScannedCommitByRepoIdAndDuration(
            @Param("repo_id") String repo_id,
            @Param("since") String since,
            @Param("until") String until
    );

}
