package cn.edu.fudan.issueservice.component;

import cn.edu.fudan.issueservice.exception.AuthException;
import cn.edu.fudan.issueservice.util.RegexUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
    @Value("${measure.service.path}")
    private String measureServicePath;

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
        return null;
    }

    public Map getRepoAndCodeLine(Map repoCommit) {
        return null;
    }

/*    public List<String> getScanCommitsIdByDuration(String detail, String start, String end) {
        return null;
    }*/


    //--------------------------------------------------------sonar api -----------------------------------------------------
    public JSONObject getSonarIssueResults(String repoName, String type, int pageSize, boolean resolved,int page) {
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

        if(page>0){
            urlBuilder.append("&p={p}");
            map.put("p",String.valueOf(page));
        }
        if(pageSize>0){
            urlBuilder.append("&ps={ps}");
            map.put("ps",String.valueOf(pageSize));
        }

        String url = urlBuilder.toString();

        try {
            ResponseEntity entity = restTemplate.getForEntity(url, JSONObject.class,map);
            JSONObject result  = JSONObject.parseObject(entity.getBody().toString());
            return result;

        }catch (RuntimeException e) {
            logger.error("repo name : {}  ----> request sonar api failed", repoName);
            throw e;
        }


    }

    public JSONObject getSonarIssueResultsBySonarIssueKey(String issues,int pageSize) {
        //由于pathurl不能过长，所以请求时设置为5个issueKey为一组
        String baseRequestUrl = sonarServicePath + "/api/issues/search?issues={issues}";
        Map<String, String> map = new HashMap<>();
        map.put("issues",issues);
        if(pageSize>0){
            map.put("ps",pageSize+"");
            baseRequestUrl= baseRequestUrl+"&ps={ps}";
        }
        try {
            ResponseEntity entity = restTemplate.getForEntity(baseRequestUrl,JSONObject.class,map);
            JSONObject result  = JSONObject.parseObject(entity.getBody().toString());
            return result;
        } catch (RuntimeException e) {
            logger.error("issues : {}  ----> request sonar api failed", issues);
            throw e;
        }
    }


    public JSONObject getRuleInfo(String ruleKey,String actives,String organizationKey){
        Map<String, String> map = new HashMap<>();

        String baseRequestUrl = sonarServicePath + "/api/rules/show";
        if(ruleKey ==null){
            logger.error("ruleKey is missing");
            return null;
        }else{
            map.put("key",ruleKey);
        }
        if(actives != null){
            map.put("actives",actives);
        }
        if(organizationKey != null){
            map.put("organization",organizationKey);
        }

        try{
            return restTemplate.getForObject(baseRequestUrl + "?key=" + ruleKey, JSONObject.class);
        }catch(RuntimeException e){
            logger.error("ruleKey : {}  ----> request sonar  rule infomation api failed", ruleKey);
            throw e;
        }

    }

    public JSONObject getSonarSourceLines(String componentKey,int from,int to){
        if(to<from){
            logger.error("lines {} can not greater {} ",from,to);
        }
        if(from <= 0){
            from = 1;
        }
        Map<String, String> map = new HashMap<>();

        String baseRequestUrl = sonarServicePath + "/api/sources/lines?key={key}&from={from}&to={to}";
        map.put("key",componentKey);
        map.put("from",String.valueOf(from));
        map.put("to",String.valueOf(to));
        try{
            ResponseEntity entity = restTemplate.getForEntity(baseRequestUrl,JSONObject.class,map);
            return JSONObject.parseObject(entity.getBody().toString());
        }catch (RuntimeException e){
            e.printStackTrace();
            logger.error("componentKey : {}  ----> request sonar  source Lines  api failed , from --> {} , to --> {}", componentKey,from,to);
            return null;
        }



    }

    /**该函数用来解析/api/sources/show返回来的每行代码（由于返回的数据出现各种标签，现需去掉标签）
     * @param componentKey
     * @param from
     * @param to
     * @return
     */
    public List<String> getSonarSourcesLinesShow(String componentKey, int from, int to){
        if (to<from){
            logger.error("lines {} can not greater than {} ",from,to);
        }

        ArrayList<String> linesList = new ArrayList<>();
        Map<String,String> paraMap = new HashMap<>(3);
        String baseRequestUrl = sonarServicePath + "api/sources/show?key={key}&from={from}&to={to}";
        paraMap.put("key",componentKey);
        paraMap.put("from",String.valueOf(from));
        paraMap.put("to",String.valueOf(to));
        JSONObject linesJO = restTemplate.getForObject(baseRequestUrl, JSONObject.class,paraMap);
        List<java.lang.Object> sourcesList = (List<Object>) linesJO.get("sources");
        int sourceListSize = sourcesList.size();
        Map<List<String>,String> regexAndReplaceStr = new HashMap<>();
        regexAndReplaceStr.put(Arrays.asList("<span[^>]*>","</span[^>]*>"),"");
        regexAndReplaceStr.put(Arrays.asList("&lt;"),"<");
        regexAndReplaceStr.put(Arrays.asList("&gt;"),">");
        regexAndReplaceStr.put(Arrays.asList("&amp;"),"&");
        for (int i = 0; i < sourceListSize; i++) {
            String code = (String) ((List<Object>)(sourcesList.get(i))).get(1);
            code = RegexUtil.getNoTagCode(code,regexAndReplaceStr);
            linesList.add(code);
        }
        return linesList;
    }

    //------------------------------------------------------scan api ---------------------------------------------


    public JSONObject getScanByCategoryAndRepoIdAndCommitId(String repoId,String commitId ,String category){
        return restTemplate.getForObject(scanServicePath + "/inner/scan/commit?repo_id=" + repoId+"&commit_id="+commitId+"&category="+category, JSONObject.class);
    }

    public List<String> getPreScannedCommitByCurrentCommit(String repoId,String commitId ,String category){
        JSONArray preCommits = restTemplate.getForObject(scanServicePath + "/inner/scan/pre-scanned-commit?repo_id=" + repoId+"&commit_id="+commitId+"&category="+category, JSONArray.class);
        List<String> parentCommits = new ArrayList<>();
        if(preCommits != null){
            parentCommits = preCommits.toJavaList(String.class);
        }


        return parentCommits;
    }

    public String getLatestScanFailedCommitId(String repoId,String commitId ,String category){
        String failedCommitId = restTemplate.getForObject(scanServicePath + "/inner/scan/pre-failed-commit?repo_id=" + repoId+"&commit_id="+commitId+"&category="+category, String.class);
        if(failedCommitId != null){
            return failedCommitId;
        }
        return null;

    }

    // --------------------------------------------------------measure api ---------------------------------------------------------


    public JSONObject getCodeChangesByDurationAndDeveloperName(String developerName,String since ,String until,String category,String repoId){

        HttpHeaders headers = new HttpHeaders();
        headers.add("token",null);
        HttpEntity request = new HttpEntity(headers);
        StringBuilder urlBuilder = new StringBuilder();
        boolean isFirstPram =true;
        urlBuilder.append(measureServicePath + "/measure/developer/code-change?");
        if(developerName != null){
            if(!isFirstPram){
                urlBuilder.append("&");
            }else{
                isFirstPram=false;
            }
            urlBuilder.append("developer_name=" + developerName);
        }

        if(since != null){
            if(!isFirstPram){
                urlBuilder.append("&");
            }else{
                isFirstPram=false;
            }
            urlBuilder.append("since=" + since);
        }
        if(until != null){
            if(!isFirstPram){
                urlBuilder.append("&");
            }else{
                isFirstPram=false;
            }
            urlBuilder.append("until=" + until);
        }
        if(category != null){
            if(!isFirstPram){
                urlBuilder.append("&");
            }else{
                isFirstPram=false;
            }
            urlBuilder.append("category=" + category);
        }
        if(repoId != null){
            if(!isFirstPram){
                urlBuilder.append("&");
            }else{
                isFirstPram=false;
            }
            urlBuilder.append("repo_id=" + repoId);
        }
        String url = urlBuilder.toString();
        ResponseEntity responseEntity = restTemplate.exchange(url , HttpMethod.GET,request,JSONObject.class);
        String body = responseEntity.getBody().toString();
        JSONObject result = JSONObject.parseObject(body,JSONObject.class);
        return result;
    }


    public JSONObject getDeveloperListByDuration(String developerName,String since ,String until,String repoId){
        HttpHeaders headers = new HttpHeaders();
        headers.add("token",null);
        HttpEntity request = new HttpEntity(headers);
        StringBuilder urlBuilder = new StringBuilder();
        boolean isFirstPram =true;
        urlBuilder.append(measureServicePath + "/measure/repository/duration?");
        urlBuilder.append("repo_id=" + repoId);
        urlBuilder.append("&since=" + since);
        urlBuilder.append("&until=" + until);

        if(developerName != null){
            urlBuilder.append("&developer_name=" + developerName);
        }

        String url = urlBuilder.toString();
        ResponseEntity responseEntity = restTemplate.exchange(url , HttpMethod.GET,request,JSONObject.class);
        String body = responseEntity.getBody().toString();
        JSONObject result = JSONObject.parseObject(body,JSONObject.class);
        if(result == null){
            logger.error("request /measure/repository/duration failed");
        }
        return result;
    }


}
