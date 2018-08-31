package cn.edu.fudan.projectmanager.service;


import cn.edu.fudan.projectmanager.domain.Project;

import java.util.List;

public interface ProjectService {

     void addOneProject(String userToken,String url);

     Object getProjectList(String userToken);

     Object getProjectIdList(String account_id);

     Object getProjectListByKeyWord(String userToken,String keyWord);

     Project getProjectByID(String projectId);

     void updateProjectStatus(Project project);

	 void remove(String projectId);

    String getProjectNameById(String projectId);

    String getRepoPath(String project_id);

    String getRepoId(String projectId);
}
