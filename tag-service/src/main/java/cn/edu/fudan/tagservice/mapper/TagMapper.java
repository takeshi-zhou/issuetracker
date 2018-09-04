package cn.edu.fudan.tagservice.mapper;

import cn.edu.fudan.tagservice.domain.Tag;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TagMapper {

    void addOneTag(Tag tag);

    void addOneTaggedItem(@Param("item_id") String item_id, @Param("tag_id")String tag_id);

    String getUuidByNameAndScope(@Param("name")String name, @Param("scope")String scope);
}
