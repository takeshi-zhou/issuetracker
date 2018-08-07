package cn.edu.fudan.projectmanager.dao;

import cn.edu.fudan.projectmanager.domain.Project;
import cn.edu.fudan.projectmanager.mapper.ProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProjectDao {


	private ProjectMapper projectMapper;

	@Autowired
	public void setProjectMapper(ProjectMapper projectMapper) {
		this.projectMapper = projectMapper;
	}

	public void addOneProject(Project project){
		projectMapper.addOneProject(project);
	}
	
	public List<Project> getProjectByAccountId(String accountId) {
		return projectMapper.getProjectByAccountId(accountId);
	}

    public Project getProjectByID(String projectId){
		return projectMapper.getProjectByID(projectId);
	}

	public boolean hasBeenAdded(String account_id,String url){
		return projectMapper.getProjectByURLAndAccountId(account_id,url)!=null;
	}

	public void updateProjectStatus(Project project){
		projectMapper.updateProjectStatus(project);
	}

	public void remove(String projectId) {

		projectMapper.remove(projectId) ;
	}

	public String getProjectNameById(String projectId) {

		return projectMapper.getProjectNameById(projectId);
	}

	public String getRepoPath(String project_id){
		return projectMapper.getRepoPath(project_id);
	}
}
