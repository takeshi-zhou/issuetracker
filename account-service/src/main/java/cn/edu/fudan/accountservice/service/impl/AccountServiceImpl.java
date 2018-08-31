package cn.edu.fudan.accountservice.service.impl;

import cn.edu.fudan.accountservice.dao.AccountDao;
import cn.edu.fudan.accountservice.domain.Account;
import cn.edu.fudan.accountservice.domain.ResponseBean;
import cn.edu.fudan.accountservice.service.AccountService;
import cn.edu.fudan.accountservice.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AccountServiceImpl implements AccountService {

    private RedisTemplate<Object,Object> redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate<Object,Object> redisTemplate){
        this.redisTemplate=redisTemplate;
    }


    private AccountDao accountDao;

    @Autowired
    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public ResponseBean login(String username, String password) {

        String encodePassword= MD5Util.md5(username+password);
        Account account= accountDao.login(username,encodePassword);
        if(account!=null){
            String userToken= MD5Util.md5(encodePassword);
            redisTemplate.opsForValue().set(userToken,account);
            return new ResponseBean(200,"登录成功!",userToken);
        }else{
           return new ResponseBean(401,"用户名或密码错误!",null);
        }
    }

    @Override
    public String getUserNameByToken(String userToken) {
        return getAccountByToken(userToken).getName();
    }

    @Override
    public Account getAccountByToken(String userToken) {
        return (Account)redisTemplate.opsForValue().get(userToken);
    }

    @Override
    public String getAccountIdByAccountName(String accountName) {
        return accountDao.getAccountIdByAccountName(accountName);
    }

    @Override
    public void addAccount(Account account) {
        if(accountDao.isAccountLegal())
            accountDao.addAccount(account);
    }

    @Override
    public List<String> getAllAccountId() {
        return accountDao.getAllAccountId();
    }


}
