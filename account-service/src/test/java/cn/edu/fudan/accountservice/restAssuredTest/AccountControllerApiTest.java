package cn.edu.fudan.accountservice.restAssuredTest;

import cn.edu.fudan.accountservice.AccountServiceApplication;
import cn.edu.fudan.accountservice.controller.AccountController;
import cn.edu.fudan.accountservice.service.AccountService;
import io.restassured.RestAssured;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;
import static org.hamcrest.Matchers.*;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountServiceApplication.class)
@TestPropertySource("classpath:application-test.properties")
@WebAppConfiguration
public class AccountControllerApiTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AccountController accountController;

    @Before
    public void  before_test() {
        RestAssuredMockMvc.webAppContextSetup(context);
    }

    @After
    public void rest_assured_is_reset_after_each_test() {
        RestAssuredMockMvc.reset();
    }

    @Test
    public void checkUserNameTest(){
        given().
                standaloneSetup(accountController). param("accountName", "Johan").
                when().
                    get("/user/account-name/check").
                then().
                    statusCode(200). body("msg", equalTo("success")). body("data", equalTo(false));
    }

}
