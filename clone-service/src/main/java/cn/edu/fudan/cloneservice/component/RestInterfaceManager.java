package cn.edu.fudan.cloneservice.component;

import cn.edu.fudan.cloneservice.domain.Scan;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author WZY
 * @version 1.0
 **/
@Component
public class RestInterfaceManager {

    @Value("${issue.service.path}")
    private String issueServicePath;
    @Value("${commit.service.path}")
    private String commitServicePath;
    @Value("${scan.service.path}")
    private String scanServicePath;
    @Value("${code.service.path}")
    private String codeServicePath;

    private RestTemplate restTemplate;

    public RestInterfaceManager(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    //------------------------------------scan service----------------------------------------------------------
    public String getLastScannedCommitId(String repoId,String category){
        return restTemplate.getForObject(scanServicePath+"/inner/scan/last-commit?repo_id="+repoId+"&category="+category,String.class);
    }

    public JSONObject insertScan(Scan scan){
        return restTemplate.postForObject(scanServicePath+"/inner/scan",scan,JSONObject.class);
    }

    //-----------------------------------commit service-------------------------------------------------------
    public JSONObject checkOut(String repo_id,String commit_id){
        return restTemplate.getForObject(commitServicePath + "/checkout?repo_id=" + repo_id + "&commit_id=" + commit_id, JSONObject.class);
    }

    //-----------------------------------code service-------------------------------------------------------

    public JSONObject getURL(String repo_id, String commit_id){
        return restTemplate.getForObject(codeServicePath + "?repo_id=" + repo_id + "&commit_id=" + commit_id, JSONObject.class);
    }

    public JSONObject freeRepoPath(String repoId,String repoPath){
        return restTemplate.getForObject(codeServicePath + "/free?repo_id=" + repoId+"&path="+repoPath, JSONObject.class);
    }
}
