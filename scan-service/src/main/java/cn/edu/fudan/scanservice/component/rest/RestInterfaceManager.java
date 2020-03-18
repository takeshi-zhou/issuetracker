package cn.edu.fudan.scanservice.component.rest;

import cn.edu.fudan.scanservice.exception.AuthException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author WZY
 * @version 1.0
 **/
@Slf4j
@Component
public class RestInterfaceManager {


    @Value("${account.service.path}")
    private String accountServicePath;
    @Value("${project.service.path}")
    private String projectServicePath;
    @Value("${commit.service.path}")
    private String commitServicePath;
    @Value("${repository.service.path}")
    private String repoServicePath;
    @Value("${issue.service.path}")
    private String issueServicePath;
    @Value("${code.service.path}")
    private String codeServicePath;
    @Value("${sonar.service.path}")
    private String sonarServicePath;
    @Value("${code-tracker.service.path}")
    private String codeTrackerServicePath;

    private RestTemplate restTemplate;

    public RestInterfaceManager(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private Logger logger = LoggerFactory.getLogger(RestInterfaceManager.class);

    //----------------------------------account service----------------------------------------------------
    public void userAuth(String userToken) throws AuthException {
        JSONObject result = restTemplate.getForObject(accountServicePath + "/user/auth/" + userToken, JSONObject.class);
        if (result == null || result.getIntValue("code") != 200) {
            throw new AuthException("auth failed!");
        }
    }

    //-----------------------------------commit service-------------------------------------------------------
    public JSONObject checkOut(String repo_id, String commit_id) {
        return restTemplate.getForObject(commitServicePath + "/checkout?repo_id=" + repo_id + "&commit_id=" + commit_id, JSONObject.class);
    }

    public JSONObject getCommitTime(String commitId,String repoId) {
        return restTemplate.getForObject(commitServicePath + "/commit-time?commit_id=" + commitId + "&repo_id=" +repoId, JSONObject.class);
    }

    public JSONObject getCommitByCommitId(String commitId) {
        return restTemplate.getForObject(commitServicePath + "/commit/" + commitId, JSONObject.class);
    }

    public JSONObject getCommitsOfRepo(String repoId, Integer page, Integer size) {
        String url = commitServicePath + "?repo_id=" + repoId;
        if(page != null ){
            if(size != null){
                if(size<=0 || page<=0){
                    logger.error("page size or page is not correct . page size --> {},page --> {}",size,page);
                    return null;
                }
                url += "&per_page="+size;
            }
            url += "&page="+page;
        }

        return restTemplate.getForObject(commitServicePath + "?repo_id=" + repoId + "&page=" + page + "&per_page=" + size + "&is_whole=true", JSONObject.class);
    }

    public JSONObject getCommitsOfRepoByConditions(String repoId, Integer page, Integer pageSize,Boolean isWhole) {

        String url = commitServicePath + "?repo_id=" + repoId;
        if(page != null ){
            if(pageSize != null){
                if(pageSize<=0 || page<=0){
                    logger.error("page size or page is not correct . page size --> {},page --> {}",pageSize,page);
                    return null;
                }
                url += "&per_page=" + pageSize;
            }
            url += "&page=" + page;
        }

        if(isWhole != null){
            url += "&is_whole=" + isWhole ;
        }
        return restTemplate.getForObject(url, JSONObject.class);

    }


    //-----------------------------------repo service--------------------------------------------------------
    public JSONObject getRepoById(String repoId) {
        return restTemplate.getForObject(repoServicePath + "/" + repoId, JSONObject.class);
    }

    //-----------------------------------issue service-------------------------------------------------------
    public JSONObject mapping(JSONObject requestParam) {
        return restTemplate.postForObject(issueServicePath + "/inner/issue/mapping", requestParam, JSONObject.class);
    }

    public void insertRawIssuesWithLocations(List<JSONObject> rawIssues) {
        restTemplate.postForObject(issueServicePath + "/inner/raw-issue", rawIssues, JSONObject.class);
    }

    public void deleteRawIssueOfRepo(String repoId, String category) {
        restTemplate.delete(issueServicePath + "/inner/raw-issue/" + category + "/" + repoId);
    }

    //-----------------------------------------------project service-------------------------------------------------
    public String getRepoIdOfProject(String projectId) {
        return restTemplate.getForObject(projectServicePath + "/inner/project/repo-id?project-id=" + projectId, String.class);
    }

    public JSONArray getProjectsOfRepo(String repoId) {
        return restTemplate.getForObject(projectServicePath + "/inner/project?repo_id=" + repoId, JSONArray.class);
    }

    public void updateProject(JSONObject projectParam) {
        try {
            restTemplate.put(projectServicePath + "/inner/project", projectParam, JSONObject.class);
        } catch (Exception e) {
            throw new RuntimeException("project update failed!");
        }
    }

    public JSONObject existThisProject(String repoId, String category, boolean isFirst) {
        return restTemplate.getForObject(projectServicePath + "/inner/project/exist?repo_id=" + repoId + "&type=" + category + "&is_first=" + isFirst, JSONObject.class);
    }

    public void updateFirstAutoScannedToTrue(String repoId, String category) {
        try {
            restTemplate.put(projectServicePath + "/inner/project/first-auto-scan?repo_id=" + repoId + "&type=" + category, JSONObject.class);
        } catch (Exception e) {
            throw new RuntimeException("project update failed!");
        }
    }

    //---------------------------------------------code service---------------------------------------------------------
    public String getRepoPath(String repoId, String commit_id) {
        String repoPath = null;
        JSONObject response = restTemplate.getForObject(codeServicePath + "?repo_id=" + repoId + "&commit_id=" + commit_id, JSONObject.class).getJSONObject("data");
        if (response != null) {
            if (response.getString("status").equals("Successful")) {
                repoPath = response.getString("content");
                logger.info("repoHome -> {}", repoPath);
            } else {
                logger.error("get repoHome fail -> {}", response.getString("content"));
            }
        } else {
            logger.error("code service response null!");
        }
        return repoPath;
    }

    public JSONObject freeRepoPath(String repoId, String repoPath) {
        if (repoPath != null) {
            return restTemplate.getForObject(codeServicePath + "/free?repo_id=" + repoId + "&path=" + repoPath, JSONObject.class);
        }
        return null;
    }


    //--------------------------------------------------------sonar api -----------------------------------------------------
    public JSONObject getSonarIssueResults(String repoName, String type, int pageSize, boolean resolved) {
        Map<String, String> map = new HashMap<>();
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(sonarServicePath + "/api/issues/search?componentKeys={componentKeys}&additionalFields={additionalFields}&s={s}&resolved={resolved}");
        map.put("additionalFields","_all");
        map.put("s","FILE_LINE");
        map.put("componentKeys",repoName);
        map.put("resolved",String.valueOf(resolved));
        if(type != null){
            String[] types = type.split(",");
            StringBuilder stringBuilder = new StringBuilder();
            for(int i=0;i<types.length;i++){
                String typeSb = types[i];
                if("CODE_SMELL".equals(typeSb) || "BUG".equals(typeSb) || "VULNERABILITY".equals(typeSb) || "SECURITY_HOTSPOT".equals(typeSb)){
                    stringBuilder.append(typeSb+",");
                }
            }
            if(!stringBuilder.toString().isEmpty()){
                urlBuilder.append("&componentKeys={componentKeys}");
                String requestTypes = stringBuilder.toString().substring(0,stringBuilder.toString().length()-1);
                map.put("types",requestTypes);
            }else{
                logger.error("this request type --> {} is not available in sonar api",type);
                return null;
            }
        }


        if(pageSize>0){
            urlBuilder.append("&ps={ps}");
            map.put("ps",String.valueOf(pageSize));
        }

        String url = urlBuilder.toString();

        try {
            ResponseEntity entity = restTemplate.getForEntity(url,JSONObject.class,map);
            JSONObject result  = JSONObject.parseObject(entity.getBody().toString());
            return result;

        }catch (RuntimeException e) {
            logger.error("repo name : {}  ----> request sonar api failed", repoName);
            throw e;
        }
    }

//    -------------------------------------------------- code tracker ------------------------------

    public boolean startCodeTracker(String repoId, String branch, String beginCommit) {
        boolean result = false;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("repoId",repoId);
        jsonObject.put("branch",branch);
        jsonObject.put("beginCommit",beginCommit);
        JSONObject requestResult = restTemplate.postForObject(codeTrackerServicePath+"/project/auto", jsonObject, JSONObject.class);
        if(requestResult != null){
            int code = requestResult.getInteger("code");
            if(code == 200){
                result = true;
            }
        }
        return result;
    }

    public boolean updateCodeTracker(String repoId, String branch) {
        boolean result = false;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("repoId",repoId);
        jsonObject.put("branch",branch);
        JSONObject requestResult = restTemplate.postForObject(codeTrackerServicePath+"/project/auto/update", jsonObject, JSONObject.class);
        if(requestResult != null){
            int code = requestResult.getInteger("code");
            if(code == 200){
                result = true;
            }
        }
        return result;
    }

    public String getCodeTrackerStatus(String repoId, String branch) {
        String result = "";
        JSONObject requestResult = restTemplate.getForObject(codeTrackerServicePath + "/project/scan/status?repoId=" + repoId + "&branch=" + branch, JSONObject.class);
        if(requestResult != null){
            int code = requestResult.getInteger("code");
            if(code == 200){
                result = requestResult.getString("data");
            }
        }
        return result;
    }

}

