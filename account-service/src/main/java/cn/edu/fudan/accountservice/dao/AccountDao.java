package cn.edu.fudan.accountservice.dao;

import cn.edu.fudan.accountservice.domain.Account;
import cn.edu.fudan.accountservice.mapper.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AccountDao {

    @Autowired
    private AccountMapper accountMapper;

    public String getAccountIdByAccountName(String accountName){
        return accountMapper.getAccountIdByAccountName(accountName);
    }

    public Account login(String accountName, String password){
        return accountMapper.login(accountName,password);
    }

    public boolean isAccountLegal() {
        return  false;
    }

    public void addAccount(Account account) {
        return;
    }

    public List<String> getAllAccountId(){
        return accountMapper.getAllAccountId();
    }
}
