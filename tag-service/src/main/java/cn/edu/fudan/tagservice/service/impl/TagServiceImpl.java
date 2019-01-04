package cn.edu.fudan.tagservice.service.impl;

import cn.edu.fudan.tagservice.component.RestInterfaceManager;
import cn.edu.fudan.tagservice.dao.IgnoreRecodeDao;
import cn.edu.fudan.tagservice.dao.TagDao;

import cn.edu.fudan.tagservice.domain.*;
import cn.edu.fudan.tagservice.service.TagService;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class TagServiceImpl implements TagService {

    private Logger logger = LoggerFactory.getLogger(TagServiceImpl.class);

    private TagDao tagDao;

    private IgnoreRecodeDao ignoreRecodeDao;

    private RestInterfaceManager restInterfaceManager;

    @Autowired
    public void setTagDao(TagDao tagDao) {
        this.tagDao = tagDao;
    }

    @Autowired
    public void setIgnoreRecodeDao(IgnoreRecodeDao ignoreRecodeDao) {
        this.ignoreRecodeDao = ignoreRecodeDao;
    }

    @Autowired
    public void setRestInterfaceManager(RestInterfaceManager restInterfaceManager) {
        this.restInterfaceManager = restInterfaceManager;
    }

    @Transactional
    @Override
    public void addTag(JSONObject requestBody) {
        String name = requestBody.getString("name");
        String scope = requestBody.getString("scope");
        String itemId = requestBody.getString("itemId");
        String tag_id;
        if (requestBody.getBoolean("isDefault")) {   // 默认的tag，不是自定义的
            tag_id = tagDao.getUuidByNameAndScope(name, scope);
            if (tag_id == null) {
                tag_id = UUID.randomUUID().toString();
                tagDao.addOneTag(new Tag(tag_id, name, scope, PriorityEnum.getByValue(name).getColor()));
            }
            logger.error(tagDao.hasBeenTagged(tag_id, itemId).toString());
            if (tagDao.hasBeenTagged(tag_id, itemId) > 0)
                throw new RuntimeException("duplicate tag!");
            tagDao.addOneTaggedItem(itemId, tag_id);
        } else {
            if (PriorityEnum.contains(name.toUpperCase()))
                throw new IllegalArgumentException("enter other tag" + name);
            tag_id = UUID.randomUUID().toString();
            tagDao.addOneTag(new Tag(tag_id, name, scope, "#ffffff"));
            tagDao.addOneTaggedItem(itemId, tag_id);
        }
    }

    @Override
    public void addMultiTaggedItem(List<TaggedItem> list) {
        tagDao.addMultiTaggedItem(list);
    }

    @Override
    public void modifyMultiTaggedItem(List<TaggedItem> list) {
        tagDao.modifyMultiTaggedItem(list);
    }

    @Override
    public void deleteTag(String tagId, String itemId) {
        //tagDao.deleteOneTag(tagId);
        tagDao.deleteOneTagged(tagId, itemId);
    }

    @Override
    public void deleteTaggeds(List<String> itemIds) {
        tagDao.deleteTaggeds(itemIds);
    }

    @Override
    public void modifyTag(JSONObject requestBody) {
        String name = requestBody.getString("name");
        String scope = requestBody.getString("scope");
        String itemId = requestBody.getString("itemId");
        String oldName = requestBody.getString("oldName");
        String oldTagId = tagDao.getUuidByNameAndScope(oldName, scope);
        String newTagId = tagDao.getUuidByNameAndScope(name, scope);
        if (requestBody.getBoolean("isDefault")) {
            if(name.equals("Not Tagged")){
                //取消某个issue的tag
               tagDao.deleteOneTagged(oldTagId,itemId);
            }else{
                tagDao.modifyOneTagged(oldTagId, newTagId, itemId);
            }
        } else {
            if (PriorityEnum.contains(name.toUpperCase()))
                throw new IllegalArgumentException("enter other tag" + name);
            tagDao.modifyOneTag(oldTagId, name);
        }

    }

    @Override
    public List<Tag> getTagsByItemId(String item_id) {
        return tagDao.getTagsByItemId(item_id);
    }

    @Override
    public List<String> getItemIdsByTagIds(List<String> tagIds) {
        if (tagIds == null || tagIds.size() == 0)
            return null;
        return tagDao.getItemIdsByTagIds(tagIds);
    }

    @Override
    public List<Tag> getAllDefaultTags() {
        List<Tag> tags= tagDao.getAllDefaultTags();
        tags.sort(Comparator.comparingInt((Tag tag)->PriorityEnum.getByValue(tag.getName()).getLevel()));
        return tags;
    }

    @Override
    public void deleteTagByProjectId(String projectId) {
        tagDao.deleteTagByProjectId(projectId);
    }


    /**
     * Two scenarios ：USER ,PROJECT
     * */
    @Override
    public void ignoreOneType(JSONObject requestBody) {
        String userId = restInterfaceManager.getUserId(requestBody.getString("userToken"));
        IgnoreLevelEnum ignoreLevel = IgnoreLevelEnum.valueOf(requestBody.getString("ignore-level").toUpperCase());
        String type = requestBody.getString("type");
        String repoId = requestBody.getString("repo-id");
        // before ignore tag query the type is ignored or not
        if (isIgnored(userId, ignoreLevel.value(), type, repoId)) {
            throw new RuntimeException("this type has been ignored");
        }

        /*
        if (ignoreLevel == IgnoreLevelEnum.REPOTORY) {
            repoId = restInterfaceManager.getGitRepoId(repoId);
        }
        */

        // insert ignore relation
        ignoreRecodeDao.insertOneRecord( new IgnoreRecord(UUID.randomUUID().toString(), userId, ignoreLevel.value(), type, repoId) );
    }

    @Override
    public void cancelOneIgnoreRecord(JSONObject requestBody) {
        String userId = restInterfaceManager.getUserId(requestBody.getString("userToken"));
        IgnoreLevelEnum ignoreLevel = IgnoreLevelEnum.valueOf(requestBody.getString("ignore-level").toUpperCase());
        String type = requestBody.getString("type");
        String repoId = requestBody.getString("repo-id");

        if (ignoreLevel == IgnoreLevelEnum.USER) {
            ignoreRecodeDao.cancelInvalidRecord(userId, type);
            return;
        }
        ignoreRecodeDao.cancelOneIgnoreRecord(userId, ignoreLevel.value(), type, repoId);
    }

    /**
     *  根据ignore 的level 级别返回对应的结果
     * */
    private boolean isIgnored(String userId, int level, String type, String repoId) {
        Integer recordLevel = ignoreRecodeDao.queryMinIgnoreLevelByUserId(userId, type);
        if (recordLevel == null)
            return false;

        // user 1 repo 2 project 3
        if (recordLevel == IgnoreLevelEnum.USER.value() ) {
            return true;
        }

        if (level == IgnoreLevelEnum.USER.value()) {
            ignoreRecodeDao.cancelInvalidRecord(userId, type);
            return false;
        }

        IgnoreRecord ignoreRecord = ignoreRecodeDao.queryOneRecord(userId, level, type, repoId);
        return ignoreRecord != null;
    }
}
