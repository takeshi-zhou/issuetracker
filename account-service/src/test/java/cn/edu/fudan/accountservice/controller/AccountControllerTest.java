package cn.edu.fudan.accountservice.controller;

import cn.edu.fudan.accountservice.AccountServiceApplicationTests;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


public class AccountControllerTest extends AccountServiceApplicationTests {

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
    //
    public void login() throws Exception{
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/user/login")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("username","admin")
                .param("password","admin")  //.param("password","admins")
                .session(session)
            ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    public void getAccountID() throws Exception{
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/user/accountId")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("userToken","ec15d79e36e14dd258cfff3d48b73d35")
                .session(session)
        ).andReturn();

        System.out.println(result.getResponse().getContentAsString());


    }

    @Test
    public void auth() throws Exception{
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(
                "/user/auth/ec15d79e36e14dd258cfff3d48b73d3")   
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
            ).andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }
}