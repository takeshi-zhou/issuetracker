package cn.edu.fudan.issueservice.component;

import cn.edu.fudan.issueservice.exception.AuthException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @author WZY
 * @version 1.0
 **/
@Component
public class RestInterfaceManager {

    private static Logger logger = LoggerFactory.getLogger(RestInterfaceManager.class);


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
    @Value("${scan.service.path}")
    private String scanServicePath;
    @Value("${sonar.service.path}")
    private String sonarServicePath;

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
        try{
            return restTemplate.getForObject(commitServicePath+"/"+commitId,JSONObject.class);
        }catch (Exception e){
            logger.error(" through the API , commit id ---> {} may return  several commits info ",commitId);
            throw  e;
        }

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

    public JSONObject getRepoById(String repoId) {
        return restTemplate.getForObject(repoServicePath + "/" + repoId, JSONObject.class);
    }

    public String getRepoPath(String repoId) {
        //restTemplate.getForObject(repoServicePath + "/" + repoId, String.class);
        MultiValueMap<String, String> requestEntity = new LinkedMultiValueMap<>(1);
        requestEntity.add("query", "uuid=\"" + repoId + "\"");
        JSONObject jsonObject = restTemplate.postForObject(repoServicePath, requestEntity,JSONObject.class);
        return jsonObject.getJSONArray("data").getJSONObject(0).getString("local_addr");
    }

    public Map<String, String> getDeveloperByCommits(Set<String> keySet) {
        //restTemplate.postForObject(repoServicePath + "/developerListsByCommits", keySet ,HashMap.class);
/*        Map<String,String> s = new HashMap<>();
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
        s.put("a8eb65dd6d4da0faf8b329de8fcf53ecd4c2fa8a", "tatu.saloranta@iki.fi");*/
        StringBuffer stringBuffer = new StringBuffer();
        for (String s : keySet) {
            stringBuffer.append(s);
            stringBuffer.append(",");
        }
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        MultiValueMap<String, String> requestEntity = new LinkedMultiValueMap<>(1);
        requestEntity.add("key_set", stringBuffer.toString());
        JSONObject jsonObject = restTemplate.postForObject(commitServicePath + "/developer-lists-by-commits", requestEntity,JSONObject.class);
        return jsonObject.getObject("data",Map.class);
    }

    public Map getRepoAndLatestScannedCommit(Set repoList) {
        //restTemplate.postForObject(scanServicePath + "/repo", repoList, JSONArray.class);
        return null;
    }

    public Map getRepoAndCodeLine(Map repoCommit) {
        //return restTemplate.postForObject(tagServicePath + "/item-ids", tag_ids, JSONArray.class);
        return null;
    }

/*    public List<String> getScanCommitsIdByDuration(String detail, String start, String end) {
        return null;
    }*/


    //--------------------------------------------------------sonar api -----------------------------------------------------
    public JSONObject getSonarIssueResults(String repoName, String type, int pageSize, boolean resolved,int page) {
        String baseRequestUrl = sonarServicePath + "/api/issues/search?componentKeys="
                + repoName
                + "&s=FILE_LINE&resolved="
                + resolved
                + "&ps="
                + pageSize
                + "&organization=default-organization&facets=severities%2Ctypes&additionalFields=_all";
        try {
            if(page == 0){
                if(type != null && (type.equals("CODE_SMELL") || type.equals("BUG") || type.equals("VULNERABILITY") ||type.equals("SECURITY_HOTSPOT"))){
                    return restTemplate.getForObject(baseRequestUrl+"&types="+type, JSONObject.class);
                }else if(type == null ){
                    return restTemplate.getForObject(baseRequestUrl, JSONObject.class);
                }else{
                    logger.error("this request type --> {} is not available in sonar api",type);
                    return null;
                }
            }else if(page > 0){
                if(type != null && (type.equals("CODE_SMELL") || type.equals("BUG") || type.equals("VULNERABILITY") ||type.equals("SECURITY_HOTSPOT"))){
                    return restTemplate.getForObject(baseRequestUrl+"&types="+type+"&p="+page, JSONObject.class);
                }else if(type == null ){
                    return restTemplate.getForObject(baseRequestUrl+"&p="+page, JSONObject.class);
                }else{
                    logger.error("this request type --> {} is not available in sonar api",type);
                    return null;
                }
            }else{
                logger.error("this request page --- {} is not available in sonar api ",page);
                return null;
            }
        } catch (RuntimeException e) {
            logger.error("repo name : {}  ----> request sonar api failed", repoName);
            throw new RuntimeException("get sonar result failed");
        }
    }


    public JSONObject getRuleInfo(String ruleKey,String actives,String organizationKey){
        String baseRequestUrl = sonarServicePath + "/api/rules/show?key=";
        if(ruleKey ==null){
            logger.error("ruleKey is missing");
            return null;
        }
        if(actives==null ){
            if(organizationKey ==null){
                return restTemplate.getForObject(baseRequestUrl+ruleKey, JSONObject.class);
            }else{
                return restTemplate.getForObject(baseRequestUrl+ruleKey+"&organization="+organizationKey, JSONObject.class);
            }

        }else{
            if(organizationKey ==null){
                return restTemplate.getForObject(baseRequestUrl+ruleKey+"&actives="+actives, JSONObject.class);
            }else{
                return restTemplate.getForObject(baseRequestUrl+ruleKey+"&organization="+organizationKey+"&actives="+actives, JSONObject.class);
            }
        }

    }

    //------------------------------------------------------scan api ---------------------------------------------


    public JSONObject getScanByCategoryAndRepoIdAndCommitId(String repoId,String commit_id ,String category){
        return restTemplate.getForObject(scanServicePath + "/inner/scan/commit?repo_id=" + repoId+"&commit_id="+commit_id+"&category="+category, JSONObject.class);
    }








}
