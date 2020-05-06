package cn.edu.fudan.cloneservice.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zyh
 * @date 2020/4/29
 */
public interface CommitMapper {

    List<String> getCommitIdList(@Param("repo_id") String repoId,
                                 @Param("developer") String developer,
                                 @Param("start") String start,
                                 @Param("end") String end);

}
