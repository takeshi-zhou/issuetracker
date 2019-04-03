package cn.edu.fudan.cloneservice.mapper;

import cn.edu.fudan.cloneservice.domain.CloneInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
@Repository
public interface CloneInfoMapper {
     List<CloneInfo> selectCloneInfoByCommitIdAndRepoId(@Param("repoId")  String repo_id,
                                                        @Param("commitId") String commit_id);
     void updateCloneInfoByCommitIdAndRepoId(
             @Param("repoId") String repo_id,
             @Param("commitId") String commit_id,
             @Param("packageName") String package_name,
             @Param("cloneInsNum") int clone_ins_num
     );


}
