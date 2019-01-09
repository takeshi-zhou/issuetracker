/**
 * @description:
 * @author: fancying
 * @create: 2019-01-02 21:07
 **/
package cn.edu.fudan.tagservice.component;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RestInterfaceManager {

    @Value("${account.service.path}")
    private String accountServicePath;

    @Value("${project.service.path}")
    private String projectServicePath;

    @Value("${repository.service.path}")
    private String repoServicePath;

    @Value("${issue.service.path}")
    private String issueServicePath;

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

    /**
     * issue service
     * */
    @SuppressWarnings("unchecked")
    public List<String> getIssueListByTypeAndRepoId(String repoId, String type) {
        return restTemplate.getForObject(issueServicePath + "/inner/issue/uuid?repo-id=" + repoId + "&type=" + type, List.class);
    }

    public void batchUpdateIssueListPriority(List<String> ignoreUuidList, int priority) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("list",ignoreUuidList);
        jsonObject.put("priority",priority);
        Object o = restTemplate.patchForObject(issueServicePath + "/inner/issue/priority" ,jsonObject , Object.class);
        if (o == null || o.toString().contains("failed")) {
            throw new RuntimeException("Batch Update Issue List Priority ERROR!");
        }
    }
}