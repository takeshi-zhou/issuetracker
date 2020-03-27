package cn.edu.fudan.projectmanager.service;

import cn.edu.fudan.projectmanager.ProjectManagerApplicationTests;
import cn.edu.fudan.projectmanager.component.RestInterfaceManager;
import cn.edu.fudan.projectmanager.dao.ProjectDao;
import cn.edu.fudan.projectmanager.domain.Project;
import cn.edu.fudan.projectmanager.service.impl.ProjectServiceImpl;
import cn.edu.fudan.projectmanager.tool.TestDataMaker;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@PrepareForTest({ProjectService.class, ProjectServiceImpl.class, ProjectDao.class, StringRedisTemplate.class, KafkaTemplate.class, RestTemplate.class, ResponseEntity.class})
public class ProjectServiceTest extends ProjectManagerApplicationTests {

    List<Project> projects;
    Project project;
    Project projectUpdate;
    Project projectInitial;
    private TestDataMaker testDataMaker;

    @Value("${github.api.path}")
    private String githubAPIPath;

    @Mock
    private RestInterfaceManager restInterfaceManager;

    @Mock
    private ProjectDao projectDao;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private KafkaTemplate kafkaTemplate;


    @Autowired
    @InjectMocks
    private ProjectService projectService = new ProjectServiceImpl();


    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        testDataMaker = new TestDataMaker();
        project = testDataMaker.projectMakerPro2();
        projectUpdate = testDataMaker.projectMakerPro1Update();
        projectInitial = testDataMaker.projectMakerPro1();
        projects = new ArrayList<>();
        projects.add(project);
        MemberModifier.field(ProjectServiceImpl.class,"restInterfaceManager").set(projectService,restInterfaceManager);
        System.out.println("finish mocking");
    }

    @Test
    public void addOneProjectTest() throws Exception {
        String account_id = "accId";
        String url = "https://github.com/pdtx/WebMagicForBlog";
        String username = "username";
        String password = "password";
        String name = "projectName";
        String type = "bug";
        String branch="master";
        JSONObject projectInfo = new JSONObject();
        projectInfo.put("url",null);
        projectInfo.put("isPrivate",false);
        projectInfo.put("username",username);
        projectInfo.put("password",null);
        projectInfo.put("name",name);
        projectInfo.put("type",type);
        String userToken = "token";
        /*
            当传入的project的url地址为null
        */
        try {
            projectService.addOneProject(userToken, projectInfo);
        } catch (RuntimeException e) {
            assertEquals("please input the project url!", e.getMessage());
        }

        /*
            当传入的project的url地址为不是正确路径
         */
        projectInfo.put("url","https://ithub.com/pdtx/WebMagicForBlog");
        try {
            projectService.addOneProject(userToken, projectInfo);
        } catch (RuntimeException e) {
            assertEquals("invalid url!", e.getMessage());
        }

        /*
            当是私有库又没有密码时
         */
        projectInfo.put("url",url);
        projectInfo.put("isPrivate",true);
        try {
            projectService.addOneProject(userToken, projectInfo);
        } catch (RuntimeException e) {
            assertEquals("this project is private,please provide your git username and password!",e.getMessage());
        }
        /*
            当传入的project的url地址已存在库内
         */
        projectInfo.put("password",password);
        PowerMockito.when(restInterfaceManager.getAccountId(userToken)).thenReturn(account_id);
        PowerMockito.when(projectDao.hasBeenAdded(account_id,url,type,branch)).thenReturn(true);
        try {
            projectService.addOneProject(userToken, projectInfo);
        } catch (RuntimeException e) {
            assertEquals("The project has been added!",e.getMessage());
        }
        /*
            添加成功
         */
        PowerMockito.when(projectDao.hasBeenAdded(account_id,url,type,branch)).thenReturn(false);
        PowerMockito.when(projectDao.getProjectsByURLAndTypeBranch(url,type,branch)).thenReturn(projects);
        doNothing().when(projectDao).addOneProject(any(Project.class));
        PowerMockito.when(kafkaTemplate.send(eq("ProjectManager"), anyString())).thenReturn(null);
        projectService.addOneProject(userToken, projectInfo);
        verify(projectDao,times(1)).addOneProject(any(Project.class));

    }


    @Test
    public void getProjectList() {
        String userToken = "userToken";
        String type = "bug";
        String account_id = "accId";
        List<Project> list = new ArrayList<Project>();
        list.add(project);
        list.add(projectUpdate);
        PowerMockito.when(projectDao.getProjectList(account_id,type)).thenReturn(list);
        PowerMockito.when(restInterfaceManager.getAccountId(userToken)).thenReturn(account_id);

        List<Project> listResult = (List<Project>) projectService.getProjectList(userToken,type,0);
        Assert.assertEquals(list.size(), listResult.size());
        for (int i = 0; i < listResult.size(); ++i) {
            Assert.assertEquals(list.get(i).getUuid(), listResult.get(i).getUuid());
        }

    }

    @Test
    public void getProjectByRepoId() {
        String repo_id = "RepoId";
        List<Project> projects = new ArrayList<Project>();
        projects.add(project);
        Mockito.when(projectDao.getProjectByRepoId(repo_id)).thenReturn(projects);
        List<Project> projectsResult = (List<Project>) projectService.getProjectByRepoId(repo_id);
        Assert.assertEquals(projects.size(), projectsResult.size());
        for(int i=0;i<projectsResult.size();i++){
            Assert.assertEquals(projects.get(i).getUuid(), projectsResult.get(i).getUuid());
        }
    }

    @Test
    public void getProjectByAccountId() {
        String account_id = "account_id";
        List<Project> projects = new ArrayList<Project>();
        projects.add(project);
        Mockito.when(projectDao.getProjectByAccountId(account_id)).thenReturn(projects);
        List<Project> projectsResult = (List<Project>) projectService.getProjectByAccountId(account_id,0);
        Assert.assertEquals(projects.size(), projectsResult.size());
        for(int i=0;i<projectsResult.size();i++){
            Assert.assertEquals(projects.get(i).getUuid(), projectsResult.get(i).getUuid());
        }
    }

    @Test
    public void getProjectListByKeyWord() {
        String token ="token";
        String accountId = "1";
        String keyWord = "ja";
        String type = "bug";
        List<Project> list = new ArrayList<Project>();
        list.add(project);
        list.add(projectInitial);
        PowerMockito.when(restInterfaceManager.getAccountId(token)).thenReturn(accountId);
        //PowerMockito.when(projectDao.getProjectByKeyWordAndAccountId(accountId, keyWord,type)).thenReturn(list);

        //List<Project> projects = (List<Project>) projectService.getProjectListByKeyWord(token, keyWord,type,0);
        for (Project eachProject : projects) {
            Assert.assertEquals("Java", eachProject.getName());
        }
    }

    @Test
    public void getRepoIdsByAccountIdAndType() {
        String accountId = "1";
        String type = "bug";
        List<String> repoIds = new ArrayList<String>();
        repoIds.add("repo1");
        repoIds.add("repo2");
        PowerMockito.when(projectDao.getRepoIdsByAccountIdAndType(accountId,type)).thenReturn(repoIds);

        List<String> result = (List<String>) projectService.getRepoIdsByAccountIdAndType(accountId,type,0);
        Assert.assertEquals(repoIds.size(), result.size());
        for(int i=0;i<result.size();i++){
            Assert.assertEquals(repoIds.get(i), result.get(i));
        }

    }

    @Test
    public void getProjectByID() {
        PowerMockito.when(projectDao.getProjectByID("pro2")).thenReturn(project);
        Project projectResult = projectService.getProjectByID("pro2");
        Assert.assertEquals("pro2", projectResult.getUuid());
    }


    @Test
    public void updateProjectStatus() {
        doNothing().when(projectDao).updateProjectStatus(project);
        projectService.updateProjectStatus(project);
        verify(projectDao,times(1)).updateProjectStatus(project);
    }

    /*
        remove方法的测试之后完善
     */
    @Test
    @Ignore
    public void remove() {


    }

    @Test
    public void getRepoId() {
        PowerMockito.when(projectDao.getRepoId("pro1")).thenReturn("RepoId");
        String repoId = projectService.getRepoId("pro1");
        Assert.assertEquals("RepoId", repoId);
    }


}