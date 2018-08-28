package cn.edu.fudan.issueservice.controller;

import cn.edu.fudan.issueservice.IssueServiceApplicationTests;
import cn.edu.fudan.issueservice.domain.Issue;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.junit.Assert.*;

public class RawIssueControllerTest extends IssueServiceApplicationTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private MockHttpSession session;

    @Before
    public void setupMockMvc() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        session = new MockHttpSession();


    }

    @Test
    public void getRawIssueList() throws Exception{
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/raw-issue")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("issue_id","fe491c9a-4fd1-48d8-a577-057ce3c93a34")
                .session(session)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    public void getLocationList() throws Exception{
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/raw-issue/code")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("raw_issue_id","ec76e7ff-33c7-46ea-899b-7f24f6b51d3c")
                .session(session)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());

    }

    @Test
    public void addRawIssues() {
    }

    @Test
    public void deleteRawIssue() {
    }

    @Test
    public void updateRawIssues() {
    }

    @Test
    public void getRawIssues() {
    }
}