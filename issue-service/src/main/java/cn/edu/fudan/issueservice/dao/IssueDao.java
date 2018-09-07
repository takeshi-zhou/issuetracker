package cn.edu.fudan.issueservice.dao;

import cn.edu.fudan.issueservice.domain.Issue;
import cn.edu.fudan.issueservice.mapper.IssueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public class IssueDao {

	private IssueMapper issueMapper;

	@Autowired
	public void setIssueMapper(IssueMapper issueMapper) {
		this.issueMapper = issueMapper;
	}

	public void insertIssueList(List<Issue> list){
		issueMapper.insertIssueList(list);
	}

	public void deleteIssueByProjectId(String projectId){
		issueMapper.deleteIssueByProjectId(projectId);
	}

	public void batchUpdateIssue(List<Issue> list){
		issueMapper.batchUpdateIssue(list);
	}

	public Issue getIssueByID(String uuid){
		return issueMapper.getIssueByID(uuid);
	}

    public 	Integer getIssueCount(Map<String, Object> map){
		return issueMapper.getIssueCount(map);
	}

	public List<Issue> getIssueList(Map<String, Object> map){
		return issueMapper.getIssueList(map);
	}

	public List<String> getExistIssueTypes(){
		return issueMapper.getExistIssueTypes();
	}

	public List<String> getIssueIdsByProjectId(String projectId){
		return issueMapper.getIssueIdsByProjectId(projectId);
	}

}
