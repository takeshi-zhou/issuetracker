package cn.edu.fudan.cloneservice.mapper;

import cn.edu.fudan.cloneservice.bean.CloneInstanceInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CloneInstanceInfoMapper {

    /**
     * select info by commit id ad repo id
     *
     * @param repo_id get user repo id
     * @param commit_id get user commit id
     * @return List<CloneInstanceInfo>
     */
    List<CloneInstanceInfo> selectInfoByCommitIdAndRepoId(@Param("repoId")  String repo_id,
                                                               @Param("commitId") String commit_id);

}

