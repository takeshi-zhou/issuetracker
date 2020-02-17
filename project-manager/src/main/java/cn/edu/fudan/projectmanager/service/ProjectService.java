package cn.edu.fudan.projectmanager.service;


import cn.edu.fudan.projectmanager.domain.Project;
import cn.edu.fudan.projectmanager.domain.RepoBasicInfo;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface ProjectService {

    void addOneProject(String userToken, JSONObject projectInfo);

    boolean addProjectList(String userToken, List<JSONObject> projectInfo);

    Object getProjectList(String userToken,String type);

    //jeff
    Object getProjectListByModule(String userToken, String type, String module);

    Object getProjectByRepoId(String repo_id);

    Object getProjectByAccountId(String account_id);

    Object getProjectListByKeyWord(String userToken, String keyWord,String type);

    List<String> getRepoIdsByAccountIdAndType(String account_id,String type);

    Project getProjectByID(String projectId);

    void updateProjectStatus(Project project);

    void remove(String projectId,String type, String userToken);

    String getRepoId(String projectId);

    Object existProjectWithThisRepoIdAndType(String repoId,String type,boolean isFirst);

    void updateProjectFirstAutoScan(String repoId,String type);

    Project getProjectByRepoIdAndCategory(String userToken,String repoId,String category);

    /**
     * 根据条件筛选出相应的project列表
     * @param userToken
     * @param category
     * @param name
     * @param module
     * @return 返回符合条件的project列表
     */
    List<Project> getProjectsByCondition(String userToken,String category,String name,String module);
}
