package cn.edu.fudan.measureservice.mapper;

import cn.edu.fudan.measureservice.domain.core.FileMeasure;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * description:
 *
 * @author fancying
 * create: 2020-06-10 23:43
 **/
@Repository
public interface FileMeasureMapper {

    void insertOneFileMeasure(FileMeasure fileMeasure);

    List<Map<String, Object>> getDevHistoryCommitInfo(@Param("repo_id")String repo_id, @Param("since")String since, @Param("until")String until);


}