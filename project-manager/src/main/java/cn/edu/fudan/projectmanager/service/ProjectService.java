package cn.edu.fudan.projectmanager.service;


import cn.edu.fudan.projectmanager.domain.Project;

import java.util.List;

public interface ProjectService {

    void addOneProject(String userToken, Project project);

    Object getProjectList(String userToken,String type);

    Object getProjectByRepoId(String repo_id);

    Object getProjectByAccountId(String account_id);

    Object getProjectListByKeyWord(String userToken, String keyWord,String type);

    List<String> getRepoIdsByAccountId(String account_id);

    Project getProjectByID(String projectId);

    void updateProjectStatus(Project project);

    void remove(String projectId,String type, String userToken);

    String getRepoId(String projectId);
}
