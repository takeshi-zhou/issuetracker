package cn.edu.fudan.projectmanager.mapper;

import cn.edu.fudan.projectmanager.domain.Project;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProjectMapper {

    void addOneProject(Project project);

    List<Project> getProjectByAccountId(@Param("account_id") String account_id);

    List<Project> getProjectList(@Param("account_id") String account_id,@Param("type") String type);

    //jeff
    List<Project> getProjectListByModule(@Param("account_id") String account_id,@Param("type") String type,@Param("module") String module);

    List<Project> getProjectByKeyWordAndAccountId(@Param("account_id") String account_id, @Param("keyWord") String keyWord,@Param("type") String type);

    List<Project> getProjectByRepoId(@Param("repo_id") String repo_id);

    Project getProjectByID(String uuid);

    Project getProjectByURLTypeAndAccountIdBranch(@Param("account_id") String account_id, @Param("url") String url, @Param("type") String type,@Param("branch") String branch) ;

    List<Project> getProjectsByURLAndTypeBranch(@Param("url") String url, @Param("type") String type ,@Param("branch") String branch);

    void updateProjectStatus(Project project);

    void updateProjectFirstAutoScan(@Param("repo_id") String repo_id,@Param("type") String type);

    void remove(@Param("projectId") String projectId);

    String getRepoId(@Param("projectId") String projectId);

    List<String> getRepoIdsByAccountIdAndType(@Param("account_id") String account_id,@Param("type") String type);

    List<String> getProjectIdsByRepoIdAndType(@Param("repo_id") String repo_id,@Param("type") String type);

    int getProjectCountWithThisRepoIdAndType(@Param("repo_id") String repo_id,@Param("type") String type);

    Project getProjectByRepoIdAndCategory( @Param("account_id") String account_id, @Param("repo_id") String repo_id,@Param("category") String category);

    List<Project> getProjectsByCondition( @Param("account_id") String account_id, @Param("type") String category,@Param("name") String name,@Param("module") String module);
}
