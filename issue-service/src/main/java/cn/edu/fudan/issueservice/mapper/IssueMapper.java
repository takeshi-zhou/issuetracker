package cn.edu.fudan.issueservice.mapper;

import cn.edu.fudan.issueservice.domain.Issue;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface IssueMapper {

	void insertIssueList(List<Issue> list);

	void deleteIssueByProjectId(String projectId);

	void batchUpdateIssue(List<Issue> list);

	Issue getIssueByID(String uuid);

	Integer getIssueCount(Map<String, Object> map);
	
	List<Issue> getIssueList(Map<String, Object> map);

	List<String> getExistIssueTypes();

	List<String> getIssueIdsByProjectId(String projectId);
}
