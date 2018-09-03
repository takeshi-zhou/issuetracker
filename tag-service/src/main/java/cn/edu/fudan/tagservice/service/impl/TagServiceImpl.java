package cn.edu.fudan.tagservice.service.impl;

import cn.edu.fudan.tagservice.dao.TagDao;
import cn.edu.fudan.tagservice.domain.Tag;
import cn.edu.fudan.tagservice.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void addTag(Tag tag, String item_id) {
        tagDao.addOneTaggedItem(item_id,tag.getUuid());
        tagDao.addOneTag(tag);
    }
}
