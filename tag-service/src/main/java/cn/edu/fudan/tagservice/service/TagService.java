package cn.edu.fudan.tagservice.service;

import cn.edu.fudan.tagservice.domain.Tag;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface TagService {

    void addTag(JSONObject requestBody);

    void deleteTag(String tagId,String itemId);

    void modifyTag(JSONObject requestBody);

    List<Tag> getTagsByItemId(String item_id);

    List<String> getItemIdsByTagIds(List<String> tagIds);
}
