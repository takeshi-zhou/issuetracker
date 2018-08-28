package cn.edu.fudan.issueservice.controller;

import cn.edu.fudan.issueservice.IssueServiceApplicationTests;
import cn.edu.fudan.issueservice.domain.Issue;
import cn.edu.fudan.issueservice.domain.Location;
import cn.edu.fudan.issueservice.domain.RawIssue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
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
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.*;

public class RawIssueControllerTest extends IssueServiceApplicationTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    RawIssueController rawIssueController;

    private MockMvc mockMvc;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockHttpSession session;

    ObjectMapper mapper;
    RawIssue rawIssue1;
    RawIssue rawIssue2;
    List<RawIssue> list;

    @Before
    public void setupMockMvc() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        session = new MockHttpSession();
        request = new MockHttpServletRequest();
        request.setCharacterEncoding("UTF-8");
        response = new MockHttpServletResponse();

        mapper = new ObjectMapper();

        rawIssue1 = new RawIssue();
        rawIssue1.setUuid("111");
        rawIssue1.setType("OBL_UNSATISFIED_OBLIGATION");
        rawIssue1.setDetail("{\"type\":\"OBL_UNSATISFIED_OBLIGATION\",\"priority\":\"2\",\"rank\":\"20\",\"abbrev\":\"OBL\",\"category\":\"EXPERIMENTAL\"}");
        rawIssue1.setFile_name("DatabaseTool.java");
        rawIssue1.setScan_id("f012de17-318e-4bef-9f8c-9a263bbece6b");
        rawIssue1.setIssue_id("fe491c9a-4fd1-48d8-a577-057ce3c93a34");
        rawIssue1.setCommit_id("94628087eaf6c81584223617d287c26af2116a96");

        rawIssue2 = new RawIssue();
        rawIssue2.setUuid("222");
        rawIssue2.setType("OBL_UNSATISFIED_OBLIGATION");
        rawIssue2.setDetail("{\"type\":\"OBL_UNSATISFIED_OBLIGATION\",\"priority\":\"2\",\"rank\":\"20\",\"abbrev\":\"OBL\",\"category\":\"EXPERIMENTAL\"}");
        rawIssue2.setFile_name("DatabaseTool.java");
        rawIssue2.setScan_id("f012de17-318e-4bef-9f8c-9a263bbece6b");
        rawIssue2.setIssue_id("fe491c9a-4fd1-48d8-a577-057ce3c93a34");
        rawIssue2.setCommit_id("94628087eaf6c81584223617d287c26af2116a96");

        list = new ArrayList<RawIssue>();
        list.add(rawIssue1);
        list.add(rawIssue2);

    }

    @Test
    public void getRawIssueList() throws Exception{
        request.addHeader("token","ec15d79e36e14dd258cfff3d48b73d35");
        List<RawIssue> listRaw = (List<RawIssue>)rawIssueController.getRawIssueList("fe491c9a-4fd1-48d8-a577-057ce3c93a34");
        for(RawIssue rawIssue : listRaw){
            System.out.println(rawIssue.getUuid());
        }
    }

    @Test
    public void getLocationList() throws Exception{
        request.addHeader("token","ec15d79e36e14dd258cfff3d48b73d35");
        List<Location> listLocation = (List<Location>)rawIssueController.getRawIssueList("00b70f51-5cc4-45f1-bfed-9874a275af96");
        for(Location location : listLocation){
            System.out.println(location.getUuid());
        }


    }

    @Test
    public void addRawIssues() throws Exception{
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        java.lang.String requestJson = ow.writeValueAsString(list);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/inner/raw-issue")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    public void deleteRawIssue() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/inner/raw-issue/9151ecba-e749-4a14-b6e3-f3a1388139ec")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    public void updateRawIssues() throws Exception{
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        java.lang.String requestJson = ow.writeValueAsString(list);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/inner/raw-issue")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    /*
        等scan单元测试之后完善
     */
    @Test
    public void getRawIssues() throws Exception{
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/inner/raw-issue/list-by-commit")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("commit_id","")
                .session(session)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }
}