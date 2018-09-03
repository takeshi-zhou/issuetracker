package cn.edu.fudan.tagservice.controller;

import cn.edu.fudan.tagservice.domain.ResponseBean;
import cn.edu.fudan.tagservice.domain.Tag;
import cn.edu.fudan.tagservice.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author WZY
 * @version 1.0
 **/
@RestController
public class TagController {

    private TagService tagService;

    @Autowired
    public void setTagService(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping("/tag")
    public Object addTag(@RequestBody Tag tag, @RequestParam("item_id")String item_id){
        try{
            tagService.addTag(tag,item_id);
            return new ResponseBean(200,"add success",null);
        }catch (Exception e){
            return new ResponseBean(401,"add failed :"+e.getMessage(),null);
        }
    }
}
