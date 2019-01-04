package cn.edu.fudan.tagservice.mapper;

import cn.edu.fudan.tagservice.domain.IgnoreRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IgnoreRecodeMapper {

    void insertOneRecord(IgnoreRecord ignoreRecord);

    void cancelOneIgnoreRecord(@Param("user_id")String userId,@Param("level") int level,@Param("type") String type,@Param("repo_id") String repoId);

    Integer queryMinIgnoreLevelByUserId(@Param("user_id")String userId,@Param("type") String type);

    void cancelInvalidRecord(@Param("user_id") String userId,@Param("type") String type);

    IgnoreRecord queryOneRecord(@Param("user_id")String userId,@Param("level") int level,@Param("type") String type,@Param("repo_id") String repoId);
}
