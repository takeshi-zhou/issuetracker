package cn.edu.fudan.issueservice.controller;

import cn.edu.fudan.issueservice.IssueServiceApplicationTests;
import cn.edu.fudan.issueservice.domain.Issue;
import com.alibaba.druid.support.json.JSONUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Before;
import org.junit.Ignore;
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
import org.springframework.web.context.WebApplicationContext;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class IssueControllerTest extends IssueServiceApplicationTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    IssueController issueController;

    private MockMvc mockMvc;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockHttpSession session;
    Map<String,Object> map;
    ObjectMapper mapper ;

    List<Issue> list ;
    Issue issue1;
    Issue issue2;

    @Before
    public void setupMockMvc() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        session = new MockHttpSession();
        request = new MockHttpServletRequest();
        request.setCharacterEncoding("UTF-8");
        response = new MockHttpServletResponse();

        map = new LinkedHashMap<String,Object>();
        map.put("project_id","222");
        map.put("type","OBL_UNSATISFIED_OBLIGATION_EXCEPTION_EDGE");
        map.put("start","0");
        map.put("size","2");
        map.put("page","0");

        mapper = new ObjectMapper();

        issue1 = new Issue();
        issue1.setUuid("334");
        issue1.setType("OBL_UNSATISFIED_OBLIGATION_EXCEPTION_EDGE");
        issue1.setStart_commit("badfd40225cb3208106ec30c403928fd55f84886");
        issue1.setEnd_commit("badfd40225cb3208106ec30c403928fd55f84886");
        issue1.setRaw_issue_start("10cfe678-8606-41cb-abf4-fda25292cc2b");
        issue1.setRaw_issue_end("10cfe678-8606-41cb-abf4-fda25292cc2b");
        issue1.setProject_id("222");
        issue1.setTarget_files("DatabaseTool.java");

        issue2 = new Issue();
        issue2.setUuid("333");
        issue2.setType("OBL_UNSATISFIED_OBLIGATION_EXCEPTION_EDGE");
        issue2.setStart_commit("badfd40225cb3208106ec30c403928fd55f84886");
        issue2.setEnd_commit("badfd40225cb3208106ec30c403928fd55f84886");
        issue2.setRaw_issue_start("c5b30c01-86bd-4a66-a828-0d78688fb005");
        issue2.setRaw_issue_end("c5b30c01-86bd-4a66-a828-0d78688fb005");
        issue2.setProject_id("222");
        issue2.setTarget_files("DatabaseTool.java");

        list = new ArrayList<>();
        list.add(issue1);
        list.add(issue2);
    }

    @Test
    public void getIssues()  throws Exception{
        request.addHeader("token","ec15d79e36e14dd258cfff3d48b73d35");
        List<Issue> listIssue = (List<Issue>)issueController.getIssues(map);
        for(Issue issue : listIssue){
            System.out.println(issue.getUuid());
        }
    }

    /*
    *   需要启动account-service
    */
    @Test
    public void getDashBoardInfo()  throws Exception{
        request.addHeader("token","ec15d79e36e14dd258cfff3d48b73d35");
        Map<String,Object> result = (Map<String,Object>)issueController.getDashBoardInfo("yesterday",request);
        System.out.println(result.toString());
    }

    @Test
    public void addIssues() throws Exception{
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        java.lang.String requestJson = ow.writeValueAsString(list);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/inner/issue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    public void deleteIssues() throws Exception{
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/inner/issue/222")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    public void updateIssues() throws Exception{
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        java.lang.String requestJson = ow.writeValueAsString(list);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/inner/issue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    /*
        等scan单元测试之后补充
     */
    @Test
    public void mapping() {
    }
}