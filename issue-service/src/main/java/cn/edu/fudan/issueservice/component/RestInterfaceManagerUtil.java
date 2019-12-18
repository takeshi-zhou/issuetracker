package cn.edu.fudan.issueservice.component;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class RestInterfaceManagerUtil {

    private RestInterfaceManager restInterfaceManager;

    public RestInterfaceManagerUtil(RestInterfaceManager restInterfaceManager) {
        this.restInterfaceManager = restInterfaceManager;

    }

    public int getTotalCodeChangedLines(String developerName,String since ,String until,String tool,String repoId) throws RuntimeException{
        int totalAddLines=0;
        int totalDelLines=0;
        JSONObject totalCodeChangesResponse = restInterfaceManager.getCodeChangesByDurationAndDeveloperName(developerName,since,until,tool,repoId);
        if( totalCodeChangesResponse!= null && totalCodeChangesResponse.getInteger("code") == 200){
            JSONObject totalCommitBase = totalCodeChangesResponse.getJSONObject("data");
            totalAddLines = totalCommitBase.getIntValue("addLines");
            totalDelLines = totalCommitBase.getIntValue("delLines");
        }else{
            throw new RuntimeException("request /measure/developer/code-change failed") ;
        }

        return totalAddLines+totalDelLines;
    }
}
