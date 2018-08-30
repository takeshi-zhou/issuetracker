package cn.edu.fudan.projectmanager.service;

import cn.edu.fudan.projectmanager.ProjectManagerApplicationTests;
import cn.edu.fudan.projectmanager.domain.Project;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

public class ProjectServiceTest extends ProjectManagerApplicationTests {

    @Autowired
    ProjectService projectService;

    Project project;

    @Before
    public void setup() throws Exception {

        project = new Project();
        project.setUuid("a585c7d8-e8a9-47c9-878d-761f8bfaaf62");
        project.setName("Java");
        project.setLanguage("Java");
        project.setUrl("https://github.com/DuGuQiuBai/Java");
        project.setVcs_type("git");
        project.setAccount_id("1");
        project.setDownload_status("Downloaded");
        project.setScan_status("Not Scanned");
        project.setDescription("27天成为Java大神");
        project.setRepo_id("227a91de-a522-11e8-8fa0-d067e5ea858d");


    }
    /*
        addOneProject方法的测试需要account-service项目启动
     */
    @Test
    @Transactional
    public void addOneProject() {
        projectService.addOneProject("ec15d79e36e14dd258cfff3d48b73d35","https://github.com/DuGuQiuBai/Java");
    }

    /*
        addOneProject方法的测试需要account-service项目启动
     */
    @Test
    public void getProjectList() {
        List<Project> list = (List<Project>)projectService.getProjectList("ec15d79e36e14dd258cfff3d48b73d35");
        for(Project project : list){
            System.out.println(project.getName() + "  " + project.getUrl());
        }

    }

    @Test
    public void getProjectByID() {
        Project project1 = projectService.getProjectByID("a585c7d8-e8a9-47c9-878d-761f8bfaaf62");
        System.out.println(project1.getAccount_id() + "  " + project1.getUrl());
    }

    @Test
    @Transactional
    public void updateProjectStatus() {
        projectService.updateProjectStatus(project);
    }

    /*
        remove方法的测试需要启动rawIssue.service，issue.service，account.service，scan.service
     */
    @Test
    @Transactional
    public void remove() {
        projectService.remove("a585c7d8-e8a9-47c9-878d-761f8bfaaf62");
    }

    @Test
    public void getProjectNameById() {
        String projectName = projectService.getProjectNameById("a585c7d8-e8a9-47c9-878d-761f8bfaaf62");
        System.out.println(projectName);
    }

    @Test
    public void getRepoPath() {
        String repoPath = projectService.getRepoPath("a585c7d8-e8a9-47c9-878d-761f8bfaaf62");
        System.out.println(repoPath);
    }

    @Test
    public void getRepoId() {
        String repoId = projectService.getRepoId("a585c7d8-e8a9-47c9-878d-761f8bfaaf62");
        System.out.println(repoId);
    }

    @Test
    public void getProjectListByKeyWord() {
        List<Project> projects = (List<Project>) projectService.getProjectListByKeyWord("ec15d79e36e14dd258cfff3d48b73d35", "x");
        System.out.println(projects);
        for (Project project : projects) {
            System.out.println(project.getUuid());

        }
    }
}