package cn.edu.fudan.eventservice.component;

import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author WZY
 * @version 1.0
 **/
@Component
public class RestInterfaceManager {


    @Value("${account.service.path}")
    private String accountServicePath;
    @Value("${project.service.path}")
    private String projectServicePath;

    private RestTemplate restTemplate;

    public RestInterfaceManager(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private String getAccountId(String userToken){
        return restTemplate.getForObject(accountServicePath+"/user/accountId?userToken="+userToken,String.class);
    }


    public JSONArray getRepoIdsOfAccount(String userToken, String category){
        String accountId=getAccountId(userToken);
        return restTemplate.getForObject(projectServicePath+"/inner/project/repo-ids?account_id="+accountId+"&type="+category,JSONArray.class);
    }


}
