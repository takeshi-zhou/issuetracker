package cn.edu.fudan.cloneservice.mapper;

import cn.edu.fudan.cloneservice.domain.CloneInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
@Repository
public interface CloneInfoMapper {

     /**
      * select clone info by commit id and repo id
      *
      * @param repo_id get repo id
      * @param commit_id get commit id
      * @return List<CloneInfo>
      */
     List<CloneInfo> selectCloneInfoByCommitIdAndRepoId(@Param("repoId")  String repo_id,
                                                        @Param("commitId") String commit_id);

     /**
      * update clone info by commit id and repo id
      *
      * @param repo_id get repo id
      * @param commit_id get commit id
      * @param package_name get package name
      * @param clone_ins_num  ?
      */
     void updateCloneInfoByCommitIdAndRepoId(
             @Param("repoId") String repo_id,
             @Param("commitId") String commit_id,
             @Param("packageName") String package_name,
             @Param("cloneInsNum") int clone_ins_num
     );



}
