package cn.edu.fudan.tagservice.mapper;

import cn.edu.fudan.tagservice.domain.IgnoreRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IgnoreRecordMapper {

    /**
     * insert one record
     *
     * @param ignoreRecord get ignore record
     */
    void insertOneRecord(IgnoreRecord ignoreRecord);

    /**
     * cancel one ignore record
     *
     * @param userId get record user id
     * @param level get record level
     * @param type get record type
     * @param repoId get record repo id
     */
    void cancelOneIgnoreRecord(@Param("user_id")String userId,@Param("level") int level,@Param("type") String type,@Param("repo_id") String repoId);

    /**
     * query min ignore level by user id
     *
     * @param userId get record user id
     * @param type get record type
     * @return Integer
     */
    Integer queryMinIgnoreLevelByUserId(@Param("user_id")String userId,@Param("type") String type);

    /**
     * cancel invalid record
     *
     * @param userId get record user id
     * @param type get record type
     */
    void cancelInvalidRecord(@Param("user_id") String userId,@Param("type") String type);

    /**
     * query one record
     *
     * @param userId get record user id
     * @param level get record level
     * @param type get record type
     * @param repoId get record repo id
     * @return IgnoreRecord
     */
    IgnoreRecord queryOneRecord(@Param("user_id")String userId,@Param("level") int level,@Param("type") String type,@Param("repo_id") String repoId);

    /**
     * get ignore record list
     *
     * @param userId get record user id
     * @return  List<IgnoreRecord>
     */
    List<IgnoreRecord> getIgnoreRecordList(@Param("user_id")String userId);

    /**
     * get ignore type list by repo id
     *
     * @param repoId get record repo id
     * @return List<String>
     */
    List<String> getIgnoreTypeListByRepoId(@Param("repo_id")String repoId);

    /**
     * delete ignore record When repo remove
     *
     * @param repoId get record user repo id
     * @param accountId get record account id
     */
    void deleteIgnoreRecordWhenRepoRemove(@Param("repo_id") String repoId, @Param("user_id") String accountId);
}
