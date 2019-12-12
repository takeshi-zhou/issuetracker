/**
 * @description:
 * @author: fancying
 * @create: 2019-12-12 14:01
 **/
package cn.edu.fudan.issueservice.component.rest;

import cn.edu.fudan.issueservice.exception.AuthException;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RestAccountService extends AbstractRestService {

    @Value("${account.service.path}")
    private String accountServicePath;

    private RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public String getAccountId(String userToken){
        Map<String,String> urlParameters=new HashMap<>();
        urlParameters.put("userToken",userToken);
        return restTemplate.getForObject(accountServicePath+"/user/accountId?userToken={userToken}",String.class,urlParameters);
    }

    public void userAuth(String userToken)throws AuthException {
        JSONObject result = restTemplate.getForObject(accountServicePath + "/user/auth/" + userToken, JSONObject.class);
        if (result == null || result.getIntValue("code") != 200) {
            throw new AuthException("auth failed!");
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> getAccountIds() {
        return restTemplate.getForObject(accountServicePath + "/user/accountIds", List.class);
    }

}