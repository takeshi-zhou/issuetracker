package cn.edu.fudan.tagservice.controller;

import cn.edu.fudan.tagservice.domain.ResponseBean;
import cn.edu.fudan.tagservice.service.TagService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/tags")
    public Object getTags(@RequestParam("item_id")String item_id){
        return tagService.getTagsByItemId(item_id);
    }

    //JSONObject(name、scope、itemId、isDefault)
    @PostMapping("/tags")
    public Object addTag(@RequestBody JSONObject requestBody){
        try{
            tagService.addTag(requestBody);
            return new ResponseBean(200,"add success",null);
        }catch (Exception e){
            return new ResponseBean(401,"add failed :"+e.getMessage(),null);
        }
    }

    @DeleteMapping("/tags/{tag-id}")
    public Object deleteTag(@PathVariable("tag-id")String tagId,@RequestParam("item-id") String itemId){
        try{
            tagService.deleteTag(tagId,itemId);
            return new ResponseBean(200,"add success",null);
        }catch (Exception e){
            return new ResponseBean(401,"add failed :"+e.getMessage(),null);
        }
    }

    //JSONObject(tagId,name,isDefault,itemId)
    @PutMapping("/tags")
    public  Object modifyTag(@RequestBody JSONObject requestBody){
        try{
            tagService.modifyTag(requestBody);
            return new ResponseBean(200,"add success",null);
        }catch (Exception e){
            return new ResponseBean(401,"add failed :"+e.getMessage(),null);
        }
    }

    @PostMapping("/tags/item-ids")
    public Object getItemIds(@RequestBody List<String> tagIds){
        return tagService.getItemIdsByTagIds(tagIds);
    }
}