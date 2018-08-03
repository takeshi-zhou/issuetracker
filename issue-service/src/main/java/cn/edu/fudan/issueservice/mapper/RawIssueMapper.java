package cn.edu.fudan.issueservice.mapper;

import cn.edu.fudan.issueservice.domain.RawIssue;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RawIssueMapper {


	void insertRawIssueList(List<RawIssue> list);

	void deleteRawIssueByProjectId(@Param("projectId")String projectId);

	void batchUpdateIssueId(List<RawIssue> list);

	List<RawIssue> getRawIssueByCommitID(String commit_id);

	List<RawIssue> getRawIssueByIssueId(@Param("issueId") String issueId);

}
