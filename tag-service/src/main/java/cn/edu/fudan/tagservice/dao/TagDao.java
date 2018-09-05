package cn.edu.fudan.tagservice.dao;

import cn.edu.fudan.tagservice.domain.Tag;
import cn.edu.fudan.tagservice.mapper.TagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author WZY
 * @version 1.0
 **/
@Component
public class TagDao {

    private TagMapper tagMapper;

    @Autowired
    public void setTagMapper(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    public void addOneTag(Tag tag){
        tagMapper.addOneTag(tag);
    }

    public void addOneTaggedItem( String item_id, String tag_id){
        tagMapper.addOneTaggedItem(item_id,tag_id);
    }

    public String getUuidByNameAndScope(String name, String scope) {
        return tagMapper.getUuidByNameAndScope(name,scope);
    }

    public void deleteOneTag(String tagId) {
        tagMapper.deleteOneTag(tagId);
    }

    public void deleteOneTagged(String tagId, String itemId) {
        tagMapper.deleteOneTagged(tagId,itemId);
    }

    public void modifyOneTag(String tagId, String name) {
        tagMapper.modifyOneTag(tagId,name);
    }

    public void modifyOneTagged(String oldTagId,String newTagId, String iteamId) {
        tagMapper.modifyOneTagged(oldTagId,newTagId,iteamId);
    }
}
