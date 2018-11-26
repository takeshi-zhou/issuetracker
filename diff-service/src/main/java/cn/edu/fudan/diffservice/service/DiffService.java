package cn.edu.fudan.diffservice.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;

@Service
public class DiffService {

    @Value("${checkout.service.path}")
    private String checkoutServicePath;
    @Value("${master.service.path}")
    private String masterServicePath;
    @Value("${getcode.service.path}")
    private String getCodeServicePath;
    @Value("${repoHome.service.path}")
    private String repoHome;
    @Value("${repoAddr.service.path}")
    private String repoAddrServicePath;
    @Value("${getRepoAddr.service.path}")
    private String getRepoAddrPath;
    @Value("${getWholeCommit.service.path}")
    private String getWholeCommit;

    private RestTemplate restTemplate;
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public JSONObject generateFile (String repo_id, String commit_id, String file_path) throws IOException{
        Long fileOne= System.currentTimeMillis();
        JSONObject content = new JSONObject();
        content.put("filename",fileOne);
        String response = restTemplate.getForObject(checkoutServicePath +"commit_id=" + commit_id +"&repo_id=" + repo_id  , String.class);
        if (response != null && response.contains("Successful")) {
            System.out.println("checkout success!");
            JSONObject codeResponse = restTemplate.getForObject(getCodeServicePath + repoHome +file_path, JSONObject.class);
            if (codeResponse != null && codeResponse.getJSONObject("data").getString("status").equals("Successful")){
                //result.put("code", newCodeResponse.getJSONObject("data").getString("content"));
                FileWriter fileWriter = new FileWriter("/home/fdse/user/issueTracker/diffpath/"+fileOne+".java");
                fileWriter.write(codeResponse.getJSONObject("data").getString("content"));
                content.put("code",codeResponse.getJSONObject("data").getString("content"));
                fileWriter.close();
            } else {
                throw new RuntimeException("load file failed!");
            }
        } else {
            throw new RuntimeException("check out failed!");
        }
        String masterResponse = restTemplate.getForObject(masterServicePath + repo_id, String.class);
        if(masterResponse!=null && masterResponse.contains("Successful")){
            System.out.println("generateFile successful");
            return content;
        }else {
            throw new RuntimeException("check out failed!");
        }


    }

    public String readToString(String fileName) {
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }

    public String getPreCommitid(String repo_id, String commit_id){
        JSONObject commitResponse = restTemplate.getForObject(getWholeCommit + "repo_id=" + repo_id + "&is_whole=true",JSONObject.class);
        if (commitResponse != null && commitResponse.getJSONArray("data")!=null){
            JSONArray data = commitResponse.getJSONArray("data");
            for(int i =0;i<data.size();i++){
                if(data.getJSONObject(i).getString("commit_id").equals(commit_id)){
                    System.out.println("the pre commitid is "+data.getJSONObject(i+1).getString("commit_id"));
                    return data.getJSONObject(i+1).getString("commit_id");
                }
            }
        }
        System.out.println("get pre commitid failed!");
        return null;
    }

    public JSONObject diffTwoFile(String dirPath, String repo_id, String commit_id_a, String file_path_a) throws Exception{
        String commit_id_b = getPreCommitid(repo_id,commit_id_a);
        JSONObject jsonObject1 = generateFile(repo_id,commit_id_a,file_path_a);
        JSONObject jsonObject2 = generateFile(repo_id,commit_id_b,file_path_a);
        String fileOneName = jsonObject1.getString("filename");
        String fileOneCode = jsonObject1.getString("code");
        String fileTwoName = jsonObject2.getString("filename");
        String fileTwoCode = jsonObject1.getString("code");
        String cmd = "java -jar CLDIFFTest.jar  " + "/home/fdse/user/issueTracker/diffpath/"+fileOneName+".java" + " " +  "/home/fdse/user/issueTracker/diffpath/"+fileTwoName+".java"+" "+ dirPath;
        System.out.println("cmd: "+cmd);
        //java -jar CLDIFFTest.jar D:\www\1.java D:\www\2.java D:\www
        JSONObject toWeb = new JSONObject();
        toWeb.put("fileone",fileOneCode);
        toWeb.put("filetwo",fileTwoCode);
        String dir1 = "/home/fdse/user/issueTracker/diffpath/"+fileOneName+".java";
        String dir2 = "/home/fdse/user/issueTracker/diffpath/"+fileTwoName+".java";
        try {
            Process process = Runtime.getRuntime().exec(cmd, null, new File("/home/fdse/user/issueTracker/"));
            process.waitFor();
            File[] fs = new File("/home/fdse/user/issueTracker/diffpath/testproject/commitid/gen").listFiles();
            String jsonName = new String();
            for(File f: fs){
                if(f.isFile()){
                    toWeb.put("diffJson",readToString("/home/fdse/user/issueTracker/diffpath/testproject/commitid/gen/"+f.getName()));
                    jsonName = "/home/fdse/user/issueTracker/diffpath/testproject/commitid/gen/"+f.getName();
                }
            }
            deleteFile(jsonName);
            deleteFile(dir1);
            deleteFile(dir2);
            return toWeb;
        } catch (Exception e) {
            e.printStackTrace();
            deleteFile(dir1);
            deleteFile(dir2);
            return toWeb;
        }
    }


    private void deleteFile(String path){
        new File(path).delete();
    }

    public void getRepoDiff(String repoId,String commitId,String dirPath){
        JSONObject Response = restTemplate.getForObject(repoAddrServicePath + repoId, JSONObject.class);
        String addr= getRepoAddrPath + Response.getJSONObject("data").getString("local_addr") + "/.git";
        String cmd = "java -jar \\home\\fdse\\user\\issueTracker\\CLDIFFCmd.jar  " + addr + " " +  commitId+" "+ dirPath;
        try {
            Process process = Runtime.getRuntime().exec(cmd, null, new File("\\home\\fdse\\user\\issueTracker\\"));
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
