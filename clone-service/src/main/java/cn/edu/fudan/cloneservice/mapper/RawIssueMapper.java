package cn.edu.fudan.cloneservice.mapper;

import cn.edu.fudan.cloneservice.domain.RawIssue;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@Repository
public interface RawIssueMapper {

    RawIssue getRawIssueByUUID(@Param("uuid") String uuid);

    RawIssue getRawIssueByRepoIdCommitIdIssueId(
            @Param("repo_id") String repo_id,
            @Param("commit_id") String commit_id,
            @Param("issue_id") String issue_id
    );


}
