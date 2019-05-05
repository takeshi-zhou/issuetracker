package cn.edu.fudan.issueservice.component;

import cn.edu.fudan.issueservice.exception.AuthException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    @Value("${code.service.path}")
    private String codeServicePath;
    @Value("${commit.service.path}")
    private String commitServicePath;
    @Value("${tag.service.path}")
    private String tagServicePath;
    @Value("${recommendation.path}")
    String recommendationServicePath;
    @Value("${repository.service.path}")
    private String repoServicePath;

    private RestTemplate restTemplate;

    public RestInterfaceManager(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    //------------------------------------------------account service-----------------------------------------------------------
    public String getAccountId(String userToken){
        Map<String,String> urlParameters=new HashMap<>();
        urlParameters.put("userToken",userToken);
        return restTemplate.getForObject(accountServicePath+"/user/accountId?userToken={userToken}",String.class,urlParameters);
    }

    public void userAuth(String userToken)throws AuthException{
        JSONObject result = restTemplate.getForObject(accountServicePath + "/user/auth/" + userToken, JSONObject.class);
        if (result == null || result.getIntValue("code") != 200) {
            throw new AuthException("auth failed!");
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> getAccountIds() {
        return restTemplate.getForObject(accountServicePath + "/user/accountIds", List.class);
    }

    //------------------------------------------------end----------------------------------------------------------------------


    //----------------------------------------------tag service---------------------------------------------------------------
    public void deleteTagsOfIssueInOneRepo(List<String> issueIds){
        if (issueIds != null && !issueIds.isEmpty()) {
            JSONObject response = restTemplate.postForObject(tagServicePath + "/tagged-delete", issueIds, JSONObject.class);
            if (response == null || response.getIntValue("code") != 200) {
                throw new RuntimeException("tag item delete failed!");
            }
        }
    }

    public JSONArray getTagsOfIssue(String issueId){
        return  restTemplate.getForObject(tagServicePath + "?item_id=" + issueId, JSONArray.class);
    }

    public JSONArray getSpecificTaggedIssueIds(String ...tagIds){
        return restTemplate.postForObject(tagServicePath + "/item-ids", tagIds, JSONArray.class);
    }
    public JSONArray getSpecificTaggedIssueIds(JSONArray tagIds){
        return restTemplate.postForObject(tagServicePath + "/item-ids", tagIds, JSONArray.class);
    }
    public JSONArray getSpecificTaggedIssueIds(List<String> tagIds){
        return restTemplate.postForObject(tagServicePath + "/item-ids", tagIds, JSONArray.class);
    }

    public JSONArray getSolvedIssueIds(List<String> tag_ids){
        return restTemplate.postForObject(tagServicePath + "/item-ids", tag_ids, JSONArray.class);
    }

    public void modifyTags(List<JSONObject> tags){
        restTemplate.postForObject(tagServicePath+"/tagged-modify", tags, JSONObject.class);
    }

    public void addTags(List<JSONObject> tags){
        restTemplate.postForObject(tagServicePath,tags, JSONObject.class);
    }

    public JSONArray  getIgnoreTypesOfRepo(String repoId){
        return restTemplate.getForObject(tagServicePath + "/ignore/types?repo-id=" + repoId, JSONArray.class);
    }


    //----------------------------------------------------end--------------------------------------------------------

    //-----------------------------------------------project service-------------------------------------------------
    public JSONArray getRepoIdsOfAccount(String account_id,String type) {
        return restTemplate.getForObject(projectServicePath + "/inner/project/repo-ids?account_id=" + account_id+"&type="+type, JSONArray.class);
    }

    public String getRepoIdOfProject(String projectId) {
        return restTemplate.getForObject(projectServicePath + "/inner/project/repo-id?project-id=" + projectId, String.class);
    }

    public JSONArray getProjectList(String account_id) {
        return restTemplate.getForObject(projectServicePath + "/inner/projects?account_id=" + account_id,JSONArray.class);
    }
    //-------------------------------------------------end-------------------------------------------------------------

    //---------------------------------------------commit service------------------------------------------------------

    public JSONObject getOneCommitByCommitId(String commitId){
        return restTemplate.getForObject(commitServicePath+"/"+commitId,JSONObject.class);
    }

    public JSONObject checkOut(String repo_id,String commit_id){
        return restTemplate.getForObject(commitServicePath + "/checkout?repo_id=" + repo_id + "&commit_id=" + commit_id, JSONObject.class);
    }

    public JSONObject getCommitTime(String commitId){
        return restTemplate.getForObject(commitServicePath+"/commit-time?commit_id="+commitId,JSONObject.class);
    }
    //----------------------------------------------end-----------------------------------------------------------------

    //---------------------------------------------code service---------------------------------------------------------
    public JSONObject getRepoPath(String repoId,String commit_id){
        return restTemplate.getForObject(codeServicePath + "?repo_id=" + repoId+"&commit_id="+commit_id, JSONObject.class);
    }

    public JSONObject freeRepoPath(String repoId,String repoPath){
        return restTemplate.getForObject(codeServicePath + "/free?repo_id=" + repoId+"&path="+repoPath, JSONObject.class);
    }

    //-----------------------------------recommendation service---------------------------------------------------------
    public void addSolvedIssueInfo(List<JSONObject> solvedInfos){
        try{
            restTemplate.postForObject(recommendationServicePath + "/add-bug-recommendation",solvedInfos,JSONObject.class);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getRepoPath(String repoId) {
        //restTemplate.getForObject(repoServicePath + "/" + repoId, String.class);
        return "/home/fdse/user/issueTracker/repo/github/FasterXML/jackson-core-master";
    }

    public Map<String, String> getDeveloperByCommits(Set<String> keySet) {
        //restTemplate.postForObject(repoServicePath + "/developerListsByCommits", keySet ,HashMap.class);
        Map<String,String> s = new HashMap<>();
        s.put("7e89cfe4d854e5c1b00d6f01b3790ba2d3c9738a", "tatu.saloranta@iki.fi");
        s.put("99d90d43bd14d7b1262e5b32f3fb14355dab220d", "tatu.saloranta@iki.fi");
        s.put("6cfdce3eed883e10b0c67ce4b4e7738cfcb1fc7b", "tatu.saloranta@iki.fi");
        s.put("b712954e0a4d7bf86f470d123f1768e07f14d6c3", "tatu.saloranta@iki.fi");
        s.put("105102ba5bbcdd8cd752f9a9dd820164132688e3", "tatu.saloranta@iki.fi");
        s.put("50905423394bbcf2d6df9d86a1472b81db3b6d62", "tatu.saloranta@iki.fi");
        s.put("c2b69429e5f8791022b3b6c1bbb585592983f880", "tatu.saloranta@iki.fi");
        s.put("feddb66f98874a1022c74a7b0bc5b550dc7236e1", "tatu.saloranta@iki.fi");
        s.put("acda0f9e69043dfcf0a6758ad4dee0af1de3b4ac", "tatu.saloranta@iki.fi");
        s.put("e7551ed0153df46ee7324567fd4dc8ce8afe7aff", "tatu.saloranta@iki.fi");
        s.put("569c9e9ec6e53ba54013d254825cf293257edbf6", "tatu.saloranta@iki.fi");
        s.put("d708338d421df111ab3d4a36bb90b14900594ec0", "tatu.saloranta@iki.fi");
        s.put("3b4b0a174949ddc83bbba5d74283a243c866844b", "tatu.saloranta@iki.fi");
        s.put("fdf1663ef024c89535aedef8f890a34938db8c4c", "tatu.saloranta@iki.fi");
        s.put("d8bed348eb375baa4a2dff933fdac1160ad35f67", "doug.roper@rallyhealth.com");
        s.put("24f65a28db07467ae9d3b8c5e765e7783067cf06", "tatu.saloranta@iki.fi");
        s.put("9b53cf5e214aa55f4eebee9e61cb25af21e35ec1", "tatu.saloranta@iki.fi");
        s.put("465fd8e3ef598abf919feeb01577376b492558a0", "tatu.saloranta@iki.fi");
        s.put("a8eb65dd6d4da0faf8b329de8fcf53ecd4c2fa8a", "tatu.saloranta@iki.fi");
        return s;
    }

    public Map getRepoAndLatestCommit(List repoList) {
        return null;
    }

    public Map getRepoAndCodeLine(Map repoCommit) {
        return null;
    }

/*    public List<String> getScanCommitsIdByDuration(String detail, String start, String end) {
        return null;
    }*/
}
