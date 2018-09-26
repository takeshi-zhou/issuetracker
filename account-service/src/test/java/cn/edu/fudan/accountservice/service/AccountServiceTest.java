package cn.edu.fudan.accountservice.service;

import cn.edu.fudan.accountservice.AccountServiceApplicationTests;
import cn.edu.fudan.accountservice.domain.Account;
import cn.edu.fudan.accountservice.domain.ResponseBean;
import cn.edu.fudan.accountservice.tool.MockTestConnection;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Statement;
import java.util.List;

import static org.junit.Assert.*;

public class AccountServiceTest extends AccountServiceApplicationTests {

    @Autowired
    AccountService accountService;

    public static MockTestConnection mockTestConnection;

    @BeforeClass
    public static void setupConnection() throws Exception {
        mockTestConnection = new MockTestConnection();
        mockTestConnection.setupCoon();
    }

    @Before
    public void setupData() throws Exception {
        String sql = "insert into account values(\"3\",\"admin\",\"f6fdffe48c908deb0f4c3bd36c032e72\",\"admin\",\"123456@fudan.edu.cn\")";
        Statement stmt = mockTestConnection.getConn().createStatement();//创建一个Statement对象
        stmt.executeUpdate(sql);//执行sql语句
        System.out.println("finish mocking");
    }


    @Test
    public void login() {
        /*
            正确的用户名及密码
         */
        ResponseBean responseBeanCorrect = accountService.login("admin", "admin");
        System.out.println(responseBeanCorrect.toString());

        /*
            错误的用户名及密码
         */
        ResponseBean responseBeanIncorrect = accountService.login("admin", "admins");
        System.out.println(responseBeanIncorrect.toString());
    }

    @Test
    public void getUserNameByToken() {
        /*
            正确的token
         */
        String usernameCorrect = accountService.getUserNameByToken("ec15d79e36e14dd258cfff3d48b73d35");
        System.out.println(usernameCorrect);

        /*
            错误的token
         */
        //String usernameIncorrect = accountService.getUserNameByToken("123"); // 错误的token
        //System.out.println(usernameCorrect);
    }

    @Test
    public void getAccountByToken() {
        Account account = accountService.getAccountByToken("ec15d79e36e14dd258cfff3d48b73d35");
        if (account != null) {
            System.out.println(account.getAccountName() + " " + account.getPassword());
        } else {
            System.out.println("token输入错误");
        }


    }

//    @Test
//    public void getAccountIdByAccountName() {
//        /*
//            正确的AccountName
//         */
//        String idCorrect  = accountService.getAccountIdByAccountName("admin");
//        System.out.println(idCorrect);
//
//        /*
//            错误的AccountName
//         */
//        String idIncorrect  = accountService.getAccountIdByAccountName("admins");
//        System.out.println(idIncorrect);
//    }

    @Ignore
    @Test
    @Transactional
    public void addAccount() {
    }

    @Test
    public void getAllAccountId() {
        List<String> listString = accountService.getAllAccountId();
        for (String id : listString) {
            System.out.println(id);
        }
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