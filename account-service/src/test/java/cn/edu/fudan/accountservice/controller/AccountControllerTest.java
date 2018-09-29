package cn.edu.fudan.accountservice.controller;

import cn.edu.fudan.accountservice.AccountServiceApplicationTests;
import cn.edu.fudan.accountservice.tool.MockTestConnection;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.sql.Statement;


public class AccountControllerTest extends AccountServiceApplicationTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private MockHttpSession session;

    public static MockTestConnection mockTestConnection;

    @BeforeClass
    public static void setupConnection() throws Exception {
        mockTestConnection = new MockTestConnection();
        mockTestConnection.setupCoon();
    }

    @Before
    public void setupMockMvcAndData() throws Exception {
        //setupMockMvc
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        session = new MockHttpSession();

        //setupData
        String sql = "insert into account values(\"3\",\"admin\",\"f6fdffe48c908deb0f4c3bd36c032e72\",\"admin\",\"123456@fudan.edu.cn\")";
        Statement stmt = mockTestConnection.getConn().createStatement();//创建一个Statement对象
        stmt.executeUpdate(sql);//执行sql语句
        System.out.println("finish mocking");
    }

    @Test
    //
    public void login() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/user/login")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("username", "admin")
                .param("password", "admin")  //.param("password","admins")
                .session(session)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    public void getAccountID() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/user/accountId")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("userToken", "f6fdffe48c908deb0f4c3bd36c032e72")
                .session(session)
        ).andReturn();

        System.out.println(result.getResponse().getContentAsString());


    }

    @Test
    public void auth() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(
                "/user/auth/f6fdffe48c908deb0f4c3bd36c032e72")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    public void getAccountIds() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(
                "/user/accountIds")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());

    }

    @After
    public void cleanData() throws Exception {
        String sql = "delete from account where uuid='3'";
        Statement stmt = mockTestConnection.getConn().createStatement();//创建一个Statement对象
        stmt.executeUpdate(sql);//执行sql语句
        System.out.println("finish cleaning");
    }

    @AfterClass
    public static void closeConnection() throws Exception {
        mockTestConnection.closeCoon();
        System.out.println("关闭数据库成功");
    }

}