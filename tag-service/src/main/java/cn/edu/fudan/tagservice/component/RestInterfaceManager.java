/**
 * @description:
 * @author: fancying
 * @create: 2019-01-02 21:07
 **/
package cn.edu.fudan.tagservice.component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import reactor.util.annotation.Nullable;

import javax.validation.constraints.Null;
import java.util.HashMap;
import java.util.Map;

@Component
public class RestInterfaceManager {

    @Value("${account.service.path}")
    private String accountServicePath;

    @Value("${project.service.path}")
    private String projectServicePath;

    @Value("${repository.service.path}")
    private String repoServicePath;

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
     * project service
     * */
    public String getProjectNameByProjectId(String projectId) {
        String name = restTemplate.getForObject(projectServicePath + "/inner/project?repo_id=" + projectId, JSONObject.class).getString("name");
        return name;
    }
    public String getProjectNameByRepoId(String repoId) {
        JSONObject currentRepo = restTemplate.getForObject(repoServicePath + "/repository/" + repoId, JSONObject.class);
        return currentRepo.getJSONObject("data").getString("repo_name");
    }

    /**
     * fundamental service
     * */
    public String getGitRepoId(String repoId) {
        return "repoId";
    }


}