package cn.edu.fudan.projectmanager.dao;

import cn.edu.fudan.projectmanager.ProjectManagerApplicationTests;
import cn.edu.fudan.projectmanager.domain.Project;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;

import static org.junit.Assert.*;

public class ProjectDaoTest extends ProjectManagerApplicationTests {

    @Autowired
    ProjectDao projectDao ;

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

    @Test
    public void addOneProject() {
        projectDao.addOneProject(project);

    }

    @Test
    public void getProjectByAccountId() {
        List<Project> list = projectDao.getProjectByAccountId("1");
        for(Project project : list){
            System.out.println(project.getName() + "  " + project.getUrl());
        }
    }

    @Test
    public void getProjectByID() {
        Project project1 = projectDao.getProjectByID("031b57c3-006d-4892-a8ea-51e4fae52263");
        System.out.println(project1.getAccount_id() + "  " + project1.getUrl());

    }

    @Test
    public void hasBeenAdded() {
        Boolean state1 = projectDao.hasBeenAdded("1","https://github.com/mockito/mockito");
        System.out.println(state1);

        Boolean state2 = projectDao.hasBeenAdded("2","https://github.com/mockito/mockito");
        System.out.println(state2);

    }

    @Test
    public void updateProjectStatus() {
        projectDao.updateProjectStatus(project);
    }

    @Test
    public void remove() {
        projectDao.remove("a585c7d8-e8a9-47c9-878d-761f8bfaaf62");
    }

    @Test
    public void getProjectNameById() {
        String projectName = projectDao.getProjectNameById("a585c7d8-e8a9-47c9-878d-761f8bfaaf62");
        System.out.println(projectName);
    }

    @Test
    public void getRepoPath() {
        String repoPath = projectDao.getRepoPath("a585c7d8-e8a9-47c9-878d-761f8bfaaf62");
        System.out.println(repoPath);
    }

    @Test
    public void getRepoId() {
        String repoId = projectDao.getRepoId("a585c7d8-e8a9-47c9-878d-761f8bfaaf62");
        System.out.println(repoId);
    }
}