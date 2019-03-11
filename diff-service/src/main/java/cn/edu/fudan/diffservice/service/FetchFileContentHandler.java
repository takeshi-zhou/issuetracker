package cn.edu.fudan.diffservice.service;

import cn.edu.fudan.diffservice.entity.*;
import cn.edu.fudan.diffservice.util.FileUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class FetchFileContentHandler {
    public String handle(String fileparam) {
        System.out.println("FetchFileContentHandler");
        try {
            System.out.println("fileparam: " + fileparam);
//            ByteArrayInputStream fileparamStream = new ByteArrayInputStream(fileparam.getBytes());
//            Map<String,String> mMap = MyNetUtil.parsePostedKeys(fileparamStream);
            Map<String,String> mMap = new HashMap<>();
            mMap = JSON.parseObject(fileparam,Map.class);
            // mMap keys: author,file_name,parent_commit_hash,project_name,commit_hash
            String commit_hash = mMap.get("commit_hash");
            String project_name = mMap.get("project_name");
            String fileName = mMap.get("file_name");
            System.out.println(commit_hash);
            System.out.println(project_name);
            System.out.println(fileName);
            String projectOwnerName = mMap.get("project_owner_name");
            String[] fileNames = fileName.split("---");
            int id = Integer.valueOf(fileNames[0]);
            //文件路径为global_Path/project_name/commit_id/meta.json
            String metaStr = FileUtil.read(Global.outputDir + "/" + projectOwnerName + "/" + project_name + "/" + commit_hash + "/meta.json");
            Meta meta = JSON.parseObject(metaStr, Meta.class);
            CommitFile file = meta.getFiles().get(id);
            String action = meta.getActions().get(id);
            String curr_file_path;
            String prev_file_path;
            String currFileContent = "";
            String prevFileContent = "";
            String diff = null;

            if ("modified".equals(action)) {
                prev_file_path = file.getPrev_file_path();
                curr_file_path = file.getCurr_file_path();
                currFileContent = FileUtil.read(Global.outputDir + "/" + projectOwnerName + "/" + project_name + "/" + commit_hash + "/" + curr_file_path);
                prevFileContent = FileUtil.read(Global.outputDir + "/" + projectOwnerName + "/" + project_name + "/" + commit_hash + "/" + prev_file_path);
            } else if ("added".equals(action)) {
                curr_file_path = file.getCurr_file_path();
                currFileContent = FileUtil.read(Global.outputDir + "/" + projectOwnerName + "/" + project_name + "/" + commit_hash + "/" + curr_file_path);
            } else if ("deleted".equals(action)) {
                prev_file_path = file.getPrev_file_path();
                prevFileContent = FileUtil.read(Global.outputDir +  "/" + projectOwnerName + "/" + project_name + "/" + commit_hash + "/" + prev_file_path);
            }
            if(file.getDiffPath()!=null){
                diff = FileUtil.read(file.getDiffPath());
            }
            String link = FileUtil.read(meta.getLinkPath());
            Content content = new Content(prevFileContent, currFileContent, diff, link);
            String contentResultStr = JSON.toJSONString(content);

              System.out.println(contentResultStr);
//              System.out.println(String.valueOf(contentResultStr.length()));
//            byte[] bytes = contentResultStr.getBytes();
            return contentResultStr;
        }catch (Exception e){
            e.printStackTrace();
            try {
                return "error";
            }catch (Exception e2){
                e2.printStackTrace();
            }
        }
        return "Uncaught error";
    }
}
