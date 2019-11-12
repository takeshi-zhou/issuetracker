package cn.edu.fudan.issueservice.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class SonarMappingServiceImpl extends BaseMappingServiceImpl{


    @Override
    public void mapping(String repo_id, String pre_commit_id, String current_commit_id, String category, String committer) {
        int pageSize = 100;
        //判断是否是第一次入库
        if (pre_commit_id.equals(current_commit_id)) {
            //获取与sonnar-scanner 扫描时对应的repo name
            JSONObject currentRepo = restInterfaceManager.getRepoById(repo_id);
            String localAddress=currentRepo.getJSONObject("data").getString("local_addr");
            String repoName = localAddress.substring(localAddress.lastIndexOf("/")+1);
            //请求sonar接口获取信息
            JSONObject sonarIssueResult = restInterfaceManager.getSonarIssueResults(repoName,null,1,false,0);
            int issueTotal = sonarIssueResult.getIntValue("total");
            //考虑到可能issue数量太大，分页获取
            int pages = issueTotal%pageSize>0?issueTotal/pageSize+1:issueTotal/pageSize;
            List rawIssues ;
            for(int i=1;i<=pages;i++){
                //获取第i页的全部issue结果
                JSONObject sonarResult = restInterfaceManager.getSonarIssueResults(repoName,null,pageSize,false,i);
                JSONArray issues = sonarResult.getJSONArray("issues");
                //遍历存储 location,rawIssue,issue
                for(int j=0; j<issues.size();j++){
                    String rawIssueUUID = UUID.randomUUID().toString();
                    JSONObject issue = issues.getJSONObject(j);

                }
            }
        }else{

        }
    }



    public void insertLocations(String rawIssueUUID,JSONObject issue){
        int startLine;
        int endLine;
        JSONObject textRange = issue.getJSONObject("textRange");
        startLine = textRange.getIntValue("startLine");
        endLine = textRange.getIntValue("endLine");
        JSONArray flows = issue.getJSONArray("flows");

    }

}
