package cn.edu.fudan.recommendation.service;

import com.alibaba.fastjson.JSONObject;

public interface AnalyzeDiffFile {
    //repopath前两段 + nextcommitid + gen + commitid + (Diff+repopath最后一段+.json)
    JSONObject getDiffRange(String repopath, String nextcommitid, String commitid, String bug_line);
}
