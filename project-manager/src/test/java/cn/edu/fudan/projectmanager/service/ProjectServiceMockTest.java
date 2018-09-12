package cn.edu.fudan.projectmanager.service;


import cn.edu.fudan.projectmanager.ProjectManagerApplication;
import cn.edu.fudan.projectmanager.dao.ProjectDao;
import cn.edu.fudan.projectmanager.domain.Project;
import cn.edu.fudan.projectmanager.service.impl.ProjectServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.powermock.api.support.membermodification.MemberMatcher;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@SpringBootTest(classes = ProjectManagerApplication.class)
@TestPropertySource("classpath:testApplication.properties")
@PrepareForTest({ProjectService.class,ProjectServiceImpl.class,ProjectDao.class})
@PowerMockIgnore("javax.management.*")
public class ProjectServiceMockTest {

    @Mock
    private ProjectDao projectDao;


    @InjectMocks@Autowired
    private ProjectService projectService = new ProjectServiceImpl();

    @Test
    public void testAddUser() throws Exception {
        List<Project> list = new ArrayList<Project>();
        Project project = new Project();
        list.add(project);
        PowerMockito.when(projectDao.getProjectByAccountId("1")).thenReturn(list);
        ProjectService spy = PowerMockito.spy(projectService);
        PowerMockito.when(spy, "getAccountId", "f6fdffe48c908deb0f4c3bd36c032e72").thenReturn("1");
        List<Project> listResult = (List<Project>) spy.getProjectList("1");
        for (Project pro : listResult) {
            System.out.println(pro);
        }
    }














    @Test
    public void testAdd() throws Exception{
        List<Project> list = new ArrayList<Project>();
        Project project = new Project();
        list.add(project);
        PowerMockito.when(projectDao.getProjectByAccountId("1")).thenReturn(list);

        MemberModifier.stub(MemberMatcher.method(ProjectServiceImpl.class,"getAccountId")).toReturn("1");

        List<Project> listResult = (List<Project>)projectService.getProjectList("22222");
        for (Project pro : listResult) {
            System.out.println(pro);
        }
    }
}
