package cn.edu.fudan.measureservice.component;

import cn.edu.fudan.measureservice.domain.CommitCountsMonthly;
import cn.edu.fudan.measureservice.service.MeasureServiceImpl;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RestInterfaceManager {

    @Value("${account.service.path}")
    private String accountServicePath;
    @Value("${code.service.path}")
    private String codeServicePath;
    @Value("${repository.service.path}")
    private String repoServicePath;
    @Value("${commit.service.path}")
    private String commitServicePath;
    @Value("${project.service.path}")
    private String projectServicePath;
    @Value("${issue.service.path}")
    private String issueServicePath;


    private RestTemplate restTemplate;

    public RestInterfaceManager(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private Logger logger = LoggerFactory.getLogger(RestInterfaceManager.class);

    //----------------------------------account service----------------------------------------------------
    public String getAccountId(String userToken){
        Map<String,String> urlParameters=new HashMap<>();
        urlParameters.put("userToken",userToken);
        return restTemplate.getForObject(accountServicePath+"/user/accountId?userToken={userToken}",String.class,urlParameters);
    }

    //-----------------------------------------------project service-------------------------------------------------
    public String getRepoIdOfProject(String projectId) {
        return restTemplate.getForObject(projectServicePath + "/inner/project/repo-id?project-id=" + projectId, String.class);
    }

    public JSONArray getCommitsOfRepo(String repoId){
        JSONObject response=restTemplate.getForObject(commitServicePath + "?repo_id=" + repoId + "&is_whole=true", JSONObject.class);
        if(response==null||response.getJSONArray("data")==null) {
            return null;
        }
        return response.getJSONArray("data");
    }

    public JSONObject getCommitByCommitId(String commitId){
        return restTemplate.getForObject(commitServicePath+"/"+ commitId,JSONObject.class);
    }

    public JSONObject getProjectListByCondition(String token,String category,String name,String module){

        HttpHeaders headers = new HttpHeaders();
        headers.add("token",token);
        HttpEntity request = new HttpEntity(headers);
        ResponseEntity responseEntity = restTemplate.exchange(projectServicePath  + "/project/search?category=" + category+"&name=" +name+"&module="+module,HttpMethod.GET,request,JSONObject.class);
        String body = responseEntity.getBody().toString();
        JSONObject result = JSONObject.parseObject(body);
        return result;
    }


    public JSONArray getProjectList(String account_id) {
        return restTemplate.getForObject(projectServicePath + "/inner/projects?account_id=" + account_id,JSONArray.class);
    }

    public JSONArray getProjectsOfRepo(String repoId){
        return restTemplate.getForObject(projectServicePath + "/inner/project?repo_id=" + repoId,JSONArray.class);
    }

    //---------------------------------------------code service---------------------------------------------------------
    public String getRepoPath(String repoId,String commit_id){
        String repoPath=null;
        JSONObject response=restTemplate.getForObject(codeServicePath + "?repo_id=" + repoId+"&commit_id="+commit_id, JSONObject.class).getJSONObject("data");
        if (response != null ){
            if(response.getString("status").equals("Successful")) {
                repoPath=response.getString("content");
                logger.info("repoHome -> {} , repoId -->{} , commit_id -->{}" ,repoPath,repoId,commit_id);
            }else{
                logger.error("get repoHome fail -> {}",response.getString("content"));
                logger.error("repoId -> {} commitId -> {}",repoId,commit_id);
            }
        } else {
            logger.error("code service response null!");
        }
        return repoPath;
    }

    public JSONObject freeRepoPath(String repoId,String repoPath){
        JSONObject response=restTemplate.getForObject(codeServicePath + "/free?repo_id=" + repoId+"&path="+repoPath, JSONObject.class);
        if(response!=null&&response.getJSONObject("data").getString("status").equals("Successful")){
            logger.info("{} -> free success",repoPath);
        }else {
            logger.warn("{} -> free failed",repoPath);
        }
        return response;
    }

    public JSONObject getRepoById(String repoId){
        return restTemplate.getForObject(repoServicePath + "/" + repoId, JSONObject.class);
    }


    public int getNumberOfNewIssueByCommit(String repoId,String commit,String category,String spaceType,String token){
        HttpHeaders headers = new HttpHeaders();
        headers.add("token",token);
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity responseEntity = restTemplate.exchange(issueServicePath  + "/measurement/newIssue/"+ repoId + "/" + commit + "?spaceType=" + spaceType + "&category=" + category ,HttpMethod.GET,request,Object.class);
        int result = Integer.valueOf(responseEntity.getBody().toString());
        return result;
    }

    public int getNumberOfEliminateIssueByCommit(String repoId,String commit,String category,String spaceType,String token){
        HttpHeaders headers = new HttpHeaders();
        headers.add("token",token);
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity responseEntity = restTemplate.exchange(issueServicePath  + "/measurement/eliminateIssue/"+ repoId + "/" + commit + "?spaceType=" + spaceType + "&category=" + category ,HttpMethod.GET,request,Object.class);
        int result = Integer.valueOf(responseEntity.getBody().toString());
        return result;
    }

    public int getNumberOfRemainingIssue(String repoId,String commit,String spaceType ,String detail,String token){
        HttpHeaders headers = new HttpHeaders();
        headers.add("token",token);
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity responseEntity = restTemplate.exchange(issueServicePath  + "/measurement/remainingIssue/"+ repoId + "/" + commit + "?spaceType=" + spaceType ,HttpMethod.GET,request,Object.class);
        int result = Integer.valueOf(responseEntity.getBody().toString());
        return result;
    }

    public List<JSONObject> getProjectListByCategory(String token,String category){
        HttpHeaders headers = new HttpHeaders();
        headers.add("token",token);
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity responseEntity = restTemplate.exchange(projectServicePath  + "/project" + "?type=" + category ,HttpMethod.GET,request,JSONArray.class);
        String body = responseEntity.getBody().toString();
        List<JSONObject> result = JSONArray.parseArray(body,JSONObject.class);
        return result;
    }

    //----------------------------------commit service----------------------------------------------------
    public JSONArray getCommitList(String repo_id){
        JSONObject response = restTemplate.getForObject(commitServicePath  + "?repo_id=" + repo_id + "&is_whole=true",JSONObject.class);
        JSONArray data = response.getJSONArray("data");
        return data;
    }


}

