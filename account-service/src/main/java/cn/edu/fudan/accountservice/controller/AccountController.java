package cn.edu.fudan.accountservice.controller;

import cn.edu.fudan.accountservice.domain.Account;
import cn.edu.fudan.accountservice.domain.ResponseBean;
import cn.edu.fudan.accountservice.service.AccountService;
import cn.edu.fudan.accountservice.util.CookieUtil;
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

    @SuppressWarnings("need check and complete")
    @PostMapping
    @CrossOrigin
    public Object createUser(@RequestBody Account account){
        //need check out account is legal or not
        try{
            accountService.addAccount(account);
            return new ResponseBean(200,"Congratulations！successful registration.",null);
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"sigh up failed!",null);
        }
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

    //used in project service for get user project list;;need modify
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