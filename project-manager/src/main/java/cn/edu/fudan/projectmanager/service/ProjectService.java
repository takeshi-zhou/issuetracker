package cn.edu.fudan.projectmanager.service;


import cn.edu.fudan.projectmanager.domain.Project;

public interface ProjectService {

    void addOneProject(String userToken, Project project);

    Object getProjectList(String userToken,String type);

    Object getProjectByRepoId(String repo_id);

    Object getProjectIdList(String account_id);

    Object getProjectListByKeyWord(String userToken, String keyWord);

    Project getProjectByID(String projectId);

    void updateProjectStatus(Project project);

    void remove(String projectId, String userToken);

    String getRepoId(String projectId);
}
