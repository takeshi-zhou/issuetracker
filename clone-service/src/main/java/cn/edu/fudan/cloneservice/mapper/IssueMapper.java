package cn.edu.fudan.cloneservice.mapper;

import cn.edu.fudan.cloneservice.domain.Issue;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

/**
 * Created by njzhan
 * <p>
 * Date :2019-08-26
 * <p>
 * Description :
 * <p>
 * Version :1.0
 */
public interface IssueMapper {
    List<Issue> getIssueByDuration(@Param("repo_id") String repo_id,
                                   @Param("start") String start,
                                   @Param("end") String end);

//    Issue getOneIssueByDuration(@Param("repo_id") String repo_id,
//                                   @Param("start") String start,
//                                   @Param("end") String end);
}
