package cn.edu.fudan.accountservice.dao;

import cn.edu.fudan.accountservice.AccountServiceApplicationTests;
import cn.edu.fudan.accountservice.domain.Account;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

public class AccountDaoTest extends AccountServiceApplicationTests {

    @Autowired
    AccountDao accountDao ;

    @Test
    public void getAccountIdByAccountName() {
        /*
            正确的用户名
         */
        String accountIdCorrect = accountDao.getAccountIdByAccountName("admin");
        System.out.println(accountIdCorrect);

        /*
            错误的用户名
         */
        String accountIdIncorrect = accountDao.getAccountIdByAccountName("admins");
        System.out.println(accountIdIncorrect);
    }

    @Test
    public void login() {
        /*
            正确的用户名及密码
         */
        Account accountCorrect =  accountDao.login("admin","f6fdffe48c908deb0f4c3bd36c032e72");
        System.out.println(accountCorrect.getAccountName()+ " " + accountCorrect.getPassword());

        /*
            错误的用户名及密码
         */
        Account accountIncorrect =  accountDao.login("admins","123");
        System.out.println(accountIncorrect);
    }

    @Ignore
    @Test
    @Transactional
    public void addAccount() {
    }
}