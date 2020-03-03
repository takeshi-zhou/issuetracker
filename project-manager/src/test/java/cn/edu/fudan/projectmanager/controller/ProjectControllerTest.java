package cn.edu.fudan.projectmanager.controller;

import cn.edu.fudan.projectmanager.ProjectManagerApplicationTests;
import cn.edu.fudan.projectmanager.domain.Project;
import cn.edu.fudan.projectmanager.interceptor.AuthTokenInterceptor;
import cn.edu.fudan.projectmanager.service.ProjectService;
import cn.edu.fudan.projectmanager.service.impl.ProjectServiceImpl;
import cn.edu.fudan.projectmanager.tool.TestDataMaker;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;


@PrepareForTest({ProjectController.class, ProjectService.class, ProjectServiceImpl.class, AuthTokenInterceptor.class})
@WebAppConfiguration
public class ProjectControllerTest extends ProjectManagerApplicationTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    ProjectService service;

    @Autowired
    @InjectMocks
    ProjectController controller;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockHttpSession session;
    private MockMvc mockMvc;
    private TestDataMaker testDataMaker;
    ObjectMapper mapper;
    Project project;
    Project projectUpdate;
    Project projectInitial;
    String token;
    Map map;

    @Before
    public void setupMockMvc() throws Exception {
        service = Mockito.mock(ProjectService.class);
        mapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        session = new MockHttpSession();
        request = new MockHttpServletRequest();
        request.setCharacterEncoding("UTF-8");
        response = new MockHttpServletResponse();

        testDataMaker = new TestDataMaker();
        project = testDataMaker.projectMakerPro2();
        projectUpdate = testDataMaker.projectMakerPro1Update();
        projectInitial = testDataMaker.projectMakerPro1();

        MemberModifier.field(ProjectController.class, "projectService").set(controller, service);

        token = "password";

        map = new HashMap();
        map.put("url","url");
        map.put("isPrivate",false);
        map.put("name","projectName");
        map.put("type","bug");
    }

    @Test
    public void getProject() throws Exception {
        String projectId = "pro2";
        PowerMockito.when(service.getProjectByID(projectId)).thenReturn(project);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/inner/project/" + projectId)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("token", token)
                .session(session)
        ).andReturn();
        Project projectResult = JSONObject.parseObject(result.getResponse().getContentAsString(), Project.class);
        Assert.assertEquals(project.getUuid(), projectResult.getUuid());

    }

    /*
        添加成功
     */
    @Test
    public void addProjectTest1() throws Exception {
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        java.lang.String requestJson = ow.writeValueAsString(map);
        doNothing().when(service).addOneProject(eq(token), any(JSONObject.class));
        MemberModifier.stub(MemberMatcher.method(AuthTokenInterceptor.class, "preHandle")).toReturn(true);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/project")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("token", token)
                .content(requestJson)
                .session(session)
        ).andReturn();
        Map map = JSON.parseObject(result.getResponse().getContentAsString());
        Assert.assertEquals(200, map.get("code"));

    }

    /*
        添加失败
     */
    @Test
    public void addProjectTest2() throws Exception {
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        java.lang.String requestJson = ow.writeValueAsString(map);
        PowerMockito.when(service, "addOneProject", eq(token), any(JSONObject.class)).thenThrow(new RuntimeException());
        MemberModifier.stub(MemberMatcher.method(AuthTokenInterceptor.class, "preHandle")).toReturn(true);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/project")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("token", token)
                .content(requestJson)
                .session(session)
        ).andReturn();
        Map map = JSON.parseObject(result.getResponse().getContentAsString());
        Assert.assertEquals(401, map.get("code"));
    }


    @Test
    public void query() throws Exception {
        List<Project> list = new ArrayList<Project>();
        list.add(project);
        list.add(projectInitial);
        //PowerMockito.when(service.getProjectList(token,"bug")).thenReturn(list);
        MemberModifier.stub(MemberMatcher.method(AuthTokenInterceptor.class, "preHandle")).toReturn(true);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/project")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("token", token)
                .session(session)
        ).andReturn();

        List<Project> listResult = JSONObject.parseArray(result.getResponse().getContentAsString(), Project.class);
        Assert.assertEquals(list.size(), listResult.size());
        for (int i = 0; i < listResult.size(); ++i) {
            Assert.assertEquals(list.get(i).getUuid(), listResult.get(i).getUuid());
        }

    }

    @Test
    public void keyWordQuery() throws Exception {
        String keyword = "ja";
        List<Project> list = new ArrayList<Project>();
        list.add(project);
        list.add(projectInitial);
        PowerMockito.when(service.getProjectListByKeyWord(token, keyword,"bug")).thenReturn(list);
        MemberModifier.stub(MemberMatcher.method(AuthTokenInterceptor.class, "preHandle")).toReturn(true);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/project/filter")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("token", token)
                .param("keyWord", keyword)
                .session(session)
        ).andReturn();

        List<Project> listResult = JSONObject.parseArray(result.getResponse().getContentAsString(), Project.class);
        Assert.assertEquals(list.size(), listResult.size());
        for (int i = 0; i < listResult.size(); ++i) {
            Assert.assertEquals(list.get(i).getUuid(), listResult.get(i).getUuid());
        }
    }


    /*
            delete方法的测试删除成功
     */
    @Test
    public void deleteTest1() throws Exception{
        String projectId = "pro1";
        doNothing().when(service).remove(projectId,"bug",token);
        MemberModifier.stub(MemberMatcher.method(AuthTokenInterceptor.class,"preHandle")).toReturn(true);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/project/"+projectId)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("token",token)
                .session(session)
        ).andReturn();
        Map map = JSON.parseObject(result.getResponse().getContentAsString());
        Assert.assertEquals(200,map.get("code"));
    }

    /*
            delete方法的测试删除失败
     */
    @Test
    public void deleteTest2() throws Exception {
        String projectId = "pro1";
        PowerMockito.when(service, "remove", projectId,"bug",token).thenThrow(new RuntimeException());
        MemberModifier.stub(MemberMatcher.method(AuthTokenInterceptor.class, "preHandle")).toReturn(true);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/project/" + projectId)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("token", token)
                .session(session)
        ).andReturn();
        Map map = JSON.parseObject(result.getResponse().getContentAsString());
        Assert.assertEquals(401, map.get("code"));
    }

    /*
        更新project成功
     */
    @Test
    public void updateProjectTest1() throws Exception {
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        java.lang.String requestJson = ow.writeValueAsString(projectUpdate);
        doNothing().when(service).updateProjectStatus(any(Project.class));
        MemberModifier.stub(MemberMatcher.method(AuthTokenInterceptor.class, "preHandle")).toReturn(true);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/inner/project")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("token", token)
                .content(requestJson)
                .session(session)
        ).andReturn();
        Map map = JSON.parseObject(result.getResponse().getContentAsString());
        Assert.assertEquals(200, map.get("code"));
    }

    /*
        更新project失败
     */
    @Test
    public void updateProjectTest2() throws Exception {
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        java.lang.String requestJson = ow.writeValueAsString(projectUpdate);
        PowerMockito.when(service, "updateProjectStatus", any(Project.class)).thenThrow(new RuntimeException());
        doNothing().when(service).updateProjectStatus(projectUpdate);
        MemberModifier.stub(MemberMatcher.method(AuthTokenInterceptor.class, "preHandle")).toReturn(true);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/inner/project")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("token", token)
                .content(requestJson)
                .session(session)
        ).andReturn();
        Map map = JSON.parseObject(result.getResponse().getContentAsString());
        Assert.assertEquals(401, map.get("code"));
    }


    @Test
    public void getRepoId() throws Exception {
        String projectId = "pro1";
        String repoId = "repoId";
        Mockito.when(service.getRepoId(projectId)).thenReturn(repoId);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/inner/project/repo-id")
                .param("project-id", projectId)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
        ).andReturn();

        Assert.assertEquals(repoId, result.getResponse().getContentAsString());
    }

    @Test
    public void getProjectIds() throws Exception {
        String accountId = "1";
        String type = "bug";
        String proId1 = "pro1";
        String proId2 = "pro2";
        List<String> projectIds = new ArrayList<String>();
        projectIds.add(proId1);
        projectIds.add(proId2);
        Mockito.when(service.getRepoIdsByAccountIdAndType(accountId,type,0)).thenReturn(projectIds);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/inner/project/repo-ids")
                .param("account_id", accountId)
                .param("type", type)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
        ).andReturn();
        List<String> listResult = JSONObject.parseArray(result.getResponse().getContentAsString(), String.class);
        Assert.assertEquals(projectIds.size(), listResult.size());
        Assert.assertEquals(proId1, listResult.get(0));
        Assert.assertEquals(proId2, listResult.get(1));
    }

    @Test
    public void getProjectByRepoId() throws Exception {
        String repo_id = "RepoId";
        List<Project> projects = new ArrayList<Project>();
        projects.add(project);
        Mockito.when(service.getProjectByRepoId(repo_id)).thenReturn(projects);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/inner/project")
                .param("repo_id", repo_id)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
        ).andReturn();
        List<Project> projectsResult  = JSONObject.parseArray(result.getResponse().getContentAsString(), Project.class);
        Assert.assertEquals(projects.size(), projectsResult.size());
        for(int i=0;i<projectsResult.size();i++){
            Assert.assertEquals(projects.get(i).getUuid(), projectsResult.get(i).getUuid());
        }
    }

    @Test
    public void getProjectByAccountId() throws Exception {
        String account_id = "account_id";
        List<Project> projects = new ArrayList<Project>();
        projects.add(project);
        Mockito.when(service.getProjectByAccountId(account_id,0)).thenReturn(projects);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/inner/projects")
                .param("account_id", account_id)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
        ).andReturn();
        List<Project> projectsResult  = JSONObject.parseArray(result.getResponse().getContentAsString(), Project.class);
        Assert.assertEquals(projects.size(), projectsResult.size());
        for(int i=0;i<projectsResult.size();i++){
            Assert.assertEquals(projects.get(i).getUuid(), projectsResult.get(i).getUuid());
        }

    }

}