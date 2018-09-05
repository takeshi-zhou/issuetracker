package cn.edu.fudan.tagservice.service.impl;

import cn.edu.fudan.tagservice.dao.TagDao;
import cn.edu.fudan.tagservice.domain.Priority;
import cn.edu.fudan.tagservice.domain.Tag;
import cn.edu.fudan.tagservice.service.TagService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author WZY
 * @version 1.0
 **/
@Service
public class TagServiceImpl implements TagService {

    private TagDao tagDao;

    @Autowired
    public void setTagDao(TagDao tagDao) {
        this.tagDao = tagDao;
    }

    @Transactional
    @Override
    public void addTag(JSONObject requestBody) {
        String name = requestBody.getString("name");
        String scope = requestBody.getString("scope");
        String itemId = requestBody.getString("itemId");
        String tag_id ;
        if (requestBody.getBoolean("isDefault")){   // 默认的tag，不是自定义的
            tag_id = tagDao.getUuidByNameAndScope(name,scope);
            if (tag_id == null){
                tag_id = UUID.randomUUID().toString();
                tagDao.addOneTag(new Tag(tag_id,name,scope,Priority.getByValue("name").getColor()));
            }
            tagDao.addOneTaggedItem(itemId,tag_id);
        }else{
            if (Priority.contains(name))
                throw new IllegalArgumentException("enter other tag"+name);
            tag_id = UUID.randomUUID().toString();
            tagDao.addOneTag(new Tag(tag_id,name,scope,"#ffffff"));
            tagDao.addOneTaggedItem(itemId,tag_id);
        }
    }

    @Override
    public void deleteTag(String tagId,String itemId) {
        tagDao.deleteOneTag(tagId);
        tagDao.deleteOneTagged(tagId,itemId);
    }

    @Override
    public void modifyTag(JSONObject requestBody) {
        String name = requestBody.getString("name");
        String scope = requestBody.getString("scope");
        String itemId = requestBody.getString("itemId");
        String oldTagId = requestBody.getString("tagId");
        String newTagId = tagDao.getUuidByNameAndScope(name,scope);
        if (requestBody.getBoolean("isDefault")){
            tagDao.modifyOneTagged(oldTagId,newTagId,itemId);
        }else {
            if (Priority.contains(name))
                throw new IllegalArgumentException("enter other tag"+name);
            tagDao.modifyOneTag(oldTagId,name);
        }

    }
}
