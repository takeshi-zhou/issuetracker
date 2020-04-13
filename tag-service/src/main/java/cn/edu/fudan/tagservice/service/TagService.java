package cn.edu.fudan.tagservice.service;

import cn.edu.fudan.tagservice.domain.ModifyTaggedItem;
import cn.edu.fudan.tagservice.domain.Tag;
import cn.edu.fudan.tagservice.domain.TaggedItem;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface TagService {

    void addTag(JSONObject requestBody);

    void addMultiTaggedItem(JSONArray requestBody);

    void modifyMultiTaggedItem(JSONArray requestBody);

    void deleteTag(String tagId, String itemId);

    void deleteTaggeds(List<String> itemIds);

    void modifyTag(JSONObject requestBody);

    List<Tag> getTagsByItemId(String item_id);

    List<String> getItemIdsByTagIds(List<String> tagIds);

    List<String> getItemIdsByTagIds(JSONObject jsonObject);

    List<Tag> getAllDefaultTags();

    void deleteTagByProjectId(String projectId);

    boolean isSolved(String itemId);

    /**
     * 以下是ignoreRecord 相关的service
     * */

    void ignoreOneType(JSONObject requestBody, String token);

    void cancelOneIgnoreRecord(String repoId, String level, String type, String token);

    Object getIgnoreRecordList(String token);

    List<String> getIgnoreTypeListByRepoId(String repoId);

    void deleteIgnoreRecordWhenRepoRemove(String repoId, String accountId);

    /**
     * 通过scope获取相应的tag列表
     * @param scope
     * @return
     */
    List<Tag> getTagsByScope(String scope);

    String getTagIdByItemIdAndScope(String itemId , String scope);

    /**
     * 通过条件获取tag列表
     * @param scope
     * @param name
     * @param uuid
     * @return
     */
    List<Tag> getTagsByCondition(String uuid,String name,String scope);

}
