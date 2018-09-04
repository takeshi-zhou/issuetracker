package cn.edu.fudan.tagservice.controller;

import cn.edu.fudan.tagservice.domain.ResponseBean;
import cn.edu.fudan.tagservice.domain.Tag;
import cn.edu.fudan.tagservice.service.TagService;
import com.alibaba.fastjson.JSONObject;
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

    //JSONObject(name、scope、item_id、option)
    @PostMapping("/tag")
    public Object addTag(@RequestBody JSONObject requestBody){
        try{
            tagService.addTag(requestBody);
            return new ResponseBean(200,"add success",null);
        }catch (Exception e){
            return new ResponseBean(401,"add failed :"+e.getMessage(),null);
        }
    }
}