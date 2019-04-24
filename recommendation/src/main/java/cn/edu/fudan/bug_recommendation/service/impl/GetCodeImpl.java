package cn.edu.fudan.bug_recommendation.service.impl;

import cn.edu.fudan.bug_recommendation.service.GetCode;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class GetCodeImpl implements GetCode {
    @Value("${codePath}")
    private String codeServicePath;

    @Autowired
    private RestTemplate restTemplate;

    public String curr_code_isExist = "";

    @Override
    public JSONObject getRepoPath(String repoId,String commit_id){
        return restTemplate.getForObject(codeServicePath + "?repo_id=" + repoId + "&commit_id=" + commit_id, JSONObject.class);

    }
    @Override
    public JSONObject freeRepoPath(String repoId,String repoPath){
        System.out.println("repoId: "+repoId);
        System.out.println("repoPath: "+repoPath);
        return restTemplate.getForObject(codeServicePath + "/free?repo_id=" + repoId + "&path=" +repoPath, JSONObject.class);
    }
    @Override
    public String getCode(String repoId,String commit_id,String location){
        System.out.println("intogetCode");
        String code = null;
        String repoHome = null;
        String[] paths = location.split("/");
        String file_path = location;
//        for (int i = 2; i<paths.length ;i++){
//            file_path += paths[i];
//            if(i!=paths.length-1){
//                file_path += "/";
//            }
//        }

        System.out.println("file_path: " + file_path);
        try{
            JSONObject response = getRepoPath(repoId,commit_id).getJSONObject("data");
            if (response != null && response.getString("status").equals("Successful")) {
                repoHome=response.getString("content");
                code = getFileContent(repoHome+"/" +file_path);
            } else {
                code = "";
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }finally {
            if(repoHome!=null){
                JSONObject response = freeRepoPath(repoId,repoHome);
                if (response != null && response.getJSONObject("data").getString("status").equals("Successful"))
                    System.out.println(("free success: "+repoHome));
                else
                    System.out.println(("free failed: "+repoHome));
            }

        }
        return code;
    }
    @Override
    public String getFileContent(String filePath){
        File file = new File(filePath);
        if (file.exists()){
            curr_code_isExist = "modify";
        }else {
            curr_code_isExist = "delete";
        }
        StringBuilder code = new StringBuilder();
        String s = "";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            while ((s = bufferedReader.readLine()) != null) {
                code.append(s);
                code.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return code.toString();
    }
    @Override
    public String getModification_method(){
        return curr_code_isExist;
    }

    //repopath前两块/nextscan/curr/commitid/repo剩下的  next的
    //repopath前两块/nextscan/prev/commitid/repo剩下的  bug的
    public String getCodePrev(String repopath,String commitid,String nextcommitid,int startline,int endline){
        String prevPath = "diffpath/";
        String[] paths = repopath.split("/");
        for (int i=0;i<2;i++){
            prevPath += paths[i];
            prevPath += "/";
        }
        prevPath = prevPath + nextcommitid + "/" + "prev" + "/" + commitid + "/" ;
        for (int i = 2; i<paths.length ;i++){
            prevPath += paths[i];
            if(i!=paths.length-1){
                prevPath += "/";
            }
        }
        File file = new File(prevPath);
        StringBuilder sb = new StringBuilder();
        String txt = "";
        try {
            FileReader fileReader = new FileReader(file);
            LineNumberReader reader = new LineNumberReader(fileReader);
            while ((txt = reader.readLine())!=null){
                if (reader.getLineNumber()>=startline && reader.getLineNumber()<=endline){
                    sb.append(txt);
                }
            }
            reader.close();
            fileReader.close();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        return sb.toString();
    }
    public String getCodeCurr(String repopath,String commitid,String nextcommitid,int startline,int endline){
        String prevPath = "diffpath/";
        String[] paths = repopath.split("/");
        for (int i=0;i<2;i++){
            prevPath += paths[i];
            prevPath += "/";
        }
        prevPath = prevPath + nextcommitid + "/" + "curr" + "/" + commitid + "/" ;
        for (int i = 2; i<paths.length ;i++){
            prevPath += paths[i];
            if(i!=paths.length-1){
                prevPath += "/";
            }
        }
        File file = new File(prevPath);
        StringBuilder sb = new StringBuilder();
        String txt = "";
        try {
            FileReader fileReader = new FileReader(file);
            LineNumberReader reader = new LineNumberReader(fileReader);
            while ((txt = reader.readLine())!=null){
                if (reader.getLineNumber()>=startline && reader.getLineNumber()<=endline){
                    sb.append(txt);
                }
            }
            reader.close();
            fileReader.close();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        return sb.toString();
    }
}
