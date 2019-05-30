package cn.edu.fudan.cloneservice.mapper;

import cn.edu.fudan.cloneservice.domain.Commit;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RepoCommitMapper {
    List<Commit> selectCommitByRepoId(@Param("repoId")  String repo_id);

}
