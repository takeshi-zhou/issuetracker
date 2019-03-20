package cn.edu.fudan.accountservice.controller;

import cn.edu.fudan.accountservice.component.cat.CatHttpRequestTransaction;
import cn.edu.fudan.accountservice.domain.Account;
import cn.edu.fudan.accountservice.domain.ResponseBean;
import cn.edu.fudan.accountservice.service.AccountService;
import cn.edu.fudan.accountservice.util.CookieUtil;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/user")
public class AccountController {

    private AccountService accountService;

    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/account-name/check")
    @CrossOrigin
    @CatHttpRequestTransaction(type = "accountURL", name = "/user/account-name/check")
    public Object checkUserName(@RequestParam("accountName") String accountName) {
        return new ResponseBean(200, "success", accountService.isAccountNameExist(accountName));
    }

    @GetMapping("/email/check")
    @CrossOrigin
    @CatHttpRequestTransaction(type = "accountURL", name = "/user/email/check")
    public Object checkEmail(@RequestParam("email") String email) {
        return new ResponseBean(200, "success", accountService.isEmailExist(email));
    }

    @GetMapping("/nick-name/check")
    @CrossOrigin
    @CatHttpRequestTransaction(type = "accountURL", name = "/user/nick-name/check")
    public Object checkNickName(@RequestParam("nickName") String nickName) {
        return new ResponseBean(200, "success", accountService.isNameExist(nickName));
    }

    @PostMapping("/register")
    @CrossOrigin
    @CatHttpRequestTransaction(type = "accountURL", name = "/user/register")
    public Object createUser(@RequestBody Account account) {
        try {
            accountService.addAccount(account);
            return new ResponseBean(200, "Congratulations！successful registration.", null);
        } catch (Exception e) {
            return new ResponseBean(401, "sign up failed! " + e.getMessage(), null);
        }
    }

    @GetMapping(value = {"/login"})
    @CrossOrigin
    @CatHttpRequestTransaction(type = "accountURL", name = "/user/login")
    public Object login(@RequestParam("username") String username, @RequestParam("password") String password, HttpServletResponse response) {
        ResponseBean responseBean = accountService.login(username, password);
        if (responseBean.getData() != null) {
            CookieUtil.addCookie(response, "userToken", responseBean.getData().toString(), 24 * 60 * 60);
        }
        return responseBean;
    }

    @GetMapping(value = "/accountId")
    @CrossOrigin
    @CatHttpRequestTransaction(type = "accountURL", name = "/user/accountId")
    public Object getAccountID(@RequestParam("userToken") String userToken) {
        return accountService.getAccountByToken(userToken).getUuid();
    }

    @GetMapping(value = "/auth/{userToken}")
    @CrossOrigin
    @CatHttpRequestTransaction(type = "accountURL")
    public Object auth(@PathVariable("userToken") String userToken) {
        if (accountService.authByToken(userToken)) {
            return new ResponseBean(200, "auth pass", null);
        } else {
            return new ResponseBean(401, "token time out,please login", null);
        }
    }

    @GetMapping(value = "/accountIds")
    @CatHttpRequestTransaction(type = "accountURL", name = "/user/accountIds")
    public Object getAccountIds() {
        return accountService.getAllAccountId();
    }
}