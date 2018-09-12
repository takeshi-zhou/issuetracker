package cn.edu.fudan.projectmanager.service;

import cn.edu.fudan.projectmanager.ProjectManagerApplication;
import cn.edu.fudan.projectmanager.ProjectManagerApplicationTests;
import cn.edu.fudan.projectmanager.dao.ProjectDao;
import cn.edu.fudan.projectmanager.domain.Project;
import cn.edu.fudan.projectmanager.service.impl.ProjectServiceImpl;
import cn.edu.fudan.projectmanager.tool.MockTestConnection;
import cn.edu.fudan.projectmanager.tool.TestDataMaker;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@SpringBootTest(classes = ProjectManagerApplication.class)
@TestPropertySource("classpath:testApplication.properties")
@PrepareForTest({ProjectService.class,ProjectServiceImpl.class,ProjectDao.class})
@PowerMockIgnore("javax.management.*")
public class ProjectServiceTest {

    Project project;
    Project projectUpdate;
    TestDataMaker testDataMaker;

    @Mock
    private ProjectDao projectDao;

    @Autowired
    @InjectMocks
    private ProjectService projectService = new ProjectServiceImpl();


    @Before
    public void setup() throws Exception {
        testDataMaker = new TestDataMaker();
        project = testDataMaker.projectMakerPro2();
        projectUpdate = testDataMaker.projectMakerPro1();
        MemberModifier.field(ProjectServiceImpl .class, "projectDao").set(projectService ,projectDao );
        System.out.println("finish mocking");
    }



    /*
        关于kafka发送消息下载不知道如何检验
     */
    @Test
    public void addOneProject() throws Exception{
        String accountId = "1";
        doNothing().when(projectDao).addOneProject(project);
        MemberModifier.stub(MemberMatcher.method(ProjectServiceImpl.class,"getAccountId")).toReturn(accountId);
        //当url地址为null时
        try{
            String url = null;
            projectService.addOneProject("1",url);
        }catch(RuntimeException e){
            assertEquals(e.getMessage(),"please input the project url!");
        }

        try{
            String url = "https://github.co";
            projectService.addOneProject("1",url);
        }catch(RuntimeException e){
            assertEquals(e.getMessage(),"invalid url!");
        }

        try{
            String url = "https://github.com/TheAlgorit";
            projectService.addOneProject("1",url);
        }catch(RuntimeException e){
            assertEquals(e.getMessage(),"invalid url!");
        }

        try{
            String url = "https://github.com/TheAlgorithms/Java";
            projectService.addOneProject("1",url);
        }catch(RuntimeException e){
            assertEquals(e.getMessage(),"failed,this project is not maven project!");
        }

        try{
            String url = "hhttps://github.com/spotify/docker-maven-plugin";
            PowerMockito.when(projectDao.hasBeenAdded(accountId,url)).thenThrow( new RuntimeException("The project has been added!"));
            projectService.addOneProject("1",url);
        }catch(RuntimeException e){
            assertEquals(e.getMessage(),"The project has been added!");
        }




    }


    @Test
    public void getProjectList() {
        List<Project> list = new ArrayList<Project>();
        list.add(project);
        list.add(projectUpdate);
        PowerMockito.when(projectDao.getProjectByAccountId("1")).thenReturn(list);

        MemberModifier.stub(MemberMatcher.method(ProjectServiceImpl.class,"getAccountId")).toReturn("1");

        List<Project> listResult = (List<Project>)projectService.getProjectList("22222");
        for (Project pro : listResult) {
            System.out.println(pro);
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