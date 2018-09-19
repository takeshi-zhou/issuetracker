package cn.edu.fudan.projectmanager.mapper;

import cn.edu.fudan.projectmanager.domain.Project;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProjectMapper {

	 void addOneProject(Project project);

	 List<Project> getProjectByAccountId(@Param("account_id") String account_id);

	 List<Project> getProjectByKeyWordAndAccountId(@Param("account_id")String account_id,@Param("keyWord")String keyWord);

	 Project getProjectByID(String uuid);

	 Project getProjectByURLTypeAndAccountId(@Param("account_id") String account_id,@Param("url")String url,@Param("type")String type);

	 void updateProjectStatus(Project project);

	 void remove(@Param("projectId") String projectId);

	 String getProjectNameById(@Param("projectId") String projectId);

	 String getRepoPath(String project_id);

	 String getRepoId(@Param("projectId") String projectId);
}
