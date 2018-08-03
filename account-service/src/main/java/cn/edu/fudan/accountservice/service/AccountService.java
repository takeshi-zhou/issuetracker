package cn.edu.fudan.accountservice.service;


import cn.edu.fudan.accountservice.domain.Account;
import cn.edu.fudan.accountservice.domain.ResponseBean;


public interface AccountService {

     ResponseBean login(String username, String password);

     String getUserNameByToken(String userToken);

     Account getAccountByToken(String userToken);

     String getAccountIdByAccountName(String accountName);

}
