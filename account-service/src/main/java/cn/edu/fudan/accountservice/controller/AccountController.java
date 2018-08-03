package cn.edu.fudan.accountservice.controller;

import cn.edu.fudan.accountservice.domain.Account;
import cn.edu.fudan.accountservice.domain.ResponseBean;
import cn.edu.fudan.accountservice.service.AccountService;
import cn.edu.fudan.accountservice.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/User")
public class AccountController {


    private AccountService accountService;

    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping(value = {"/login"})
    @CrossOrigin
    public Object login(@RequestParam("username")String username,@RequestParam("password")String password,HttpServletResponse response){
        ResponseBean responseBean= accountService.login(username,password);
        if(responseBean.getData()!=null){
            CookieUtil.addCookie(response,"userToken",responseBean.getData().toString(),24*60*60);
        }
        return responseBean;
    }

    @GetMapping(value="/accountId")
    @CrossOrigin
    public Object getAccountID(@RequestParam("userToken")String userToken){
        return accountService.getAccountByToken(userToken).getUuid();
    }

    @GetMapping(value="/auth/{userToken}")
    @CrossOrigin
    public Object auth(@PathVariable("userToken")String userToken){
        Account account=accountService.getAccountByToken(userToken);
        if(account!=null){
            return new ResponseBean(200,"auth pass",null);
        }else{
            return new ResponseBean(401,"auth failed",null);
        }
    }
}