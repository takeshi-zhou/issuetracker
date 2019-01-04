/**
 * @description:
 * @author: fancying
 * @create: 2019-01-02 21:07
 **/
package cn.edu.fudan.tagservice.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class RestInterfaceManager {

    @Value("${account.service.path}")
    private String accountServicePath;

    private RestTemplate restTemplate;

    public RestInterfaceManager(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * account service
     * */
    public String getUserId(String userToken) {
        Map<String,String> urlParameters = new HashMap<>();
        urlParameters.put("userToken",userToken);
        return restTemplate.getForObject(accountServicePath+"/user/accountId?userToken={userToken}",String.class,urlParameters);
    }


    /**
     * fundamental service
     * */
    public String getGitRepoId(String repoId) {
        return "repoId";
    }
}