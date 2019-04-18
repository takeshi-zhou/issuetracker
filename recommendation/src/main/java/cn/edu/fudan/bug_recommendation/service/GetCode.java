package cn.edu.fudan.bug_recommendation.service;

import com.alibaba.fastjson.JSONObject;
//得到代码的通用接口
public interface GetCode {
    JSONObject getRepoPath(String repoId,String commit_id);
    JSONObject freeRepoPath(String repoId,String repoPath);
    String getCode(String repoId,String commit_id,String location);
    String getFileContent(String filePath);
    String getModification_method();
    String getCodePrev(String repopath,String commitid,String nextcommitid,int startline,int endline);
    String getCodeCurr(String repopath,String commitid,String nextcommitid,int startline,int endline);
}
