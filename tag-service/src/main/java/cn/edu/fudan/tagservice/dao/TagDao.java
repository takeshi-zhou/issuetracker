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
}
