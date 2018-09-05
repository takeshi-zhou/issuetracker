package cn.edu.fudan.tagservice.service;

import com.alibaba.fastjson.JSONObject;

public interface TagService {

    void addTag(JSONObject requestBody);

    void deleteTag(String tagId,String itemId);

    void modifyTag(JSONObject requestBody);
}
