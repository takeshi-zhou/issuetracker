package cn.edu.fudan.projectmanager.controller;

import cn.edu.fudan.projectmanager.ProjectManagerApplicationTests;
import cn.edu.fudan.projectmanager.domain.Project;
import cn.edu.fudan.projectmanager.domain.ResponseBean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

public class ProjectControllerTest extends ProjectManagerApplicationTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    ProjectController controller;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockHttpSession session;


    Project project;

    @Before
    public void setupMockMvc() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        session = new MockHttpSession();
        request = new MockHttpServletRequest();
        request.setCharacterEncoding("UTF-8");
        response = new MockHttpServletResponse();

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
    public void getProject() throws Exception{
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/inner/project/9151ecba-e749-4a14-b6e3-f3a1388139ec")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("token","ec15d79e36e14dd258cfff3d48b73d35")
                .session(session)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());

    }
    /*
        addProject方法的测试需要account-service项目启动

     */
    @Test
    @Transactional
    public void addProject() throws Exception{
       request.addHeader("token","ec15d79e36e14dd258cfff3d48b73d35");
        //Project project = (Project)controller.getProject("a585c7d8-e8a9-47c9-878d-761f8bfaaf62");
       ResponseBean responseBean = (ResponseBean)controller.addProject(request,project);
       System.out.println(responseBean);



    }

    @Test
    public void query() {
        request.addHeader("token","ec15d79e36e14dd258cfff3d48b73d35");
        List<Project> list = (List<Project>)controller.query(request);
        for(Project project : list){
            System.out.println(project.getName() + "  " + project.getUrl());
        }
    }

    @Test
    public void keyWordQuery(){
        request.addHeader("token","ec15d79e36e14dd258cfff3d48b73d35");
        List<Project> list = (List<Project>)controller.keyWordQuery(request,"web");
        for(Project project :list ){
            System.out.println(project.getName());
        }
    }


    /*
            delete方法的测试需要启动rawIssue.service，issue.service，account.service，scan.service
     */
    @Test
    @Transactional
    public void delete() throws Exception{
        request.addHeader("token","ec15d79e36e14dd258cfff3d48b73d35");
        ResponseBean responseBean = (ResponseBean)controller.delete("22");
    }

    @Test
    @Transactional
    public void updateProject() {
        ResponseBean responseBean = (ResponseBean) controller.updateProject(project);
        System.out.println(responseBean);
    }

    @Test
    public void getRepoPath() throws Exception{
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/inner/project/9151ecba-e749-4a14-b6e3-f3a1388139ec")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }



    @Test
    public void getRepoId() throws Exception{
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/inner/project/repo-id")
                .param("project-id","9151ecba-e749-4a14-b6e3-f3a1388139ec")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }
}