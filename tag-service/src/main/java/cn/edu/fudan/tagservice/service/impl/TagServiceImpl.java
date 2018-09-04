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
        String item_id = requestBody.getString("item_id");
        String tag_id;
        if (requestBody.getString("addTag") == null){
            tag_id = tagDao.getUuidByNameAndScope(name,scope);
            if (tag_id == null){
                tag_id = UUID.randomUUID().toString();
                tagDao.addOneTag(new Tag(tag_id,name,scope,Priority.getByValue("name").getColor()));
            }
            tagDao.addOneTaggedItem(item_id,tag_id);
        }else{
            if (Priority.contains(name))
                throw new IllegalArgumentException("enter other tag"+name);
            tag_id = UUID.randomUUID().toString();
            Tag tag = new Tag(tag_id,name,scope,"8");
            tagDao.addOneTag(tag);
            tagDao.addOneTaggedItem(item_id,tag_id);
        }
    }
}
