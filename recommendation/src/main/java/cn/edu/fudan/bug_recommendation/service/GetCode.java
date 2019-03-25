package cn.edu.fudan.bug_recommendation.service;

public interface GetCode {
    String getCodePrev(String repopath,String commitid,String nextcommitid,int startline,int endline);
    String getCodeCurr(String repopath,String commitid,String nextcommitid,int startline,int endline);
}
