package cn.edu.fudan.diffservice.controller;

import cn.edu.fudan.diffservice.entity.Global;
import cn.edu.fudan.diffservice.service.ClearCacheHandler;
import cn.edu.fudan.diffservice.service.FetchFileContentHandler;
import cn.edu.fudan.diffservice.service.FetchMetaCacheHandler;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/Diffservice")
public class DiffController {
    private FetchMetaCacheHandler fetchMetaCacheHandler;
    private FetchFileContentHandler fetchFileContentHandler;
    private ClearCacheHandler clearCacheHandler;
    @Autowired
    public void setFetchMetaCacheHandler(FetchMetaCacheHandler fetchMetaCacheHandler){this.fetchMetaCacheHandler=fetchMetaCacheHandler;}
    @Autowired
    public void setFetchFileContentHandler(FetchFileContentHandler fetchFileContentHandler){this.fetchFileContentHandler=fetchFileContentHandler;}
    @Autowired
    public void setClearCacheHandler(ClearCacheHandler clearCacheHandler){this.clearCacheHandler=clearCacheHandler;}
    @PostMapping("/fetchMeta")
    @CrossOrigin
    public Object getMeta(@RequestBody String diffparam){
        String meta = fetchMetaCacheHandler.handle(diffparam);
        return meta;
    }
    @PostMapping("/fetchFile")
    @CrossOrigin
    public Object getAboutFile(@RequestBody String fileparam){
        System.out.println("getfile");
        System.out.println(fileparam);
        String contentResultStr = fetchFileContentHandler.handle(fileparam);
        return contentResultStr;
    }
    @GetMapping("/clearCommitRecord")
    @CrossOrigin
    public Object getMetaCache(@RequestBody String poststring){
        String result = clearCacheHandler.handle(poststring);
        return result;
    }
}
