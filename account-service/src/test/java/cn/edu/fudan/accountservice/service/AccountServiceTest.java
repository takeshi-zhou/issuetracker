package cn.edu.fudan.accountservice.service;

import cn.edu.fudan.accountservice.AccountServiceApplicationTests;
import cn.edu.fudan.accountservice.domain.Account;
import cn.edu.fudan.accountservice.domain.ResponseBean;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class AccountServiceTest extends AccountServiceApplicationTests {

    @Autowired
    AccountService accountService ;
    

    @Test
    public void login() {
        /*
            正确的用户名及密码
         */
        ResponseBean responseBeanCorrect = accountService.login("admin","admin");
        System.out.println(responseBeanCorrect.toString());

        /*
            错误的用户名及密码
         */
        ResponseBean responseBeanIncorrect = accountService.login("admin","admins");
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
        if(account != null){
            System.out.println(account.getAccountName()+ " " + account.getPassword());
        }else{
            System.out.println("token输入错误");
        }


    }

    @Test
    public void getAccountIdByAccountName() {
        /*
            正确的AccountName
         */
        String idCorrect  = accountService.getAccountIdByAccountName("admin");
        System.out.println(idCorrect);

        /*
            错误的AccountName
         */
        String idIncorrect  = accountService.getAccountIdByAccountName("admins");
        System.out.println(idIncorrect);
    }

    @Ignore
    @Test
    public void addAccount() {
    }
}