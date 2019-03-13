package cn.edu.fudan.diffservice.service;

import cn.edu.fudan.diffservice.entity.Global;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
public class ClearCacheHandler {
    public String handle(String postString) {
        System.out.println("clear cache");
        try {
            System.out.println("PostContent: " + postString);
            Map<String, Object> map = new HashMap<String, Object>();
            map = JSON.parseObject(postString, map.getClass());

            String projectOwnerName = map.get("projectOwnerName").toString();
            String projectName = map.get("projectName").toString();
            String branch = map.get("branch").toString();

            String folderPath = projectOwnerName + File.separator + projectName + "-" + branch;
            String folderDetailPath = Global.outputDir + File.separator + folderPath;
            delFolder(folderDetailPath);
            String success = "SUCCESS\n";
            System.out.println(success);
            return success;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "Uncaught error";
    }
    public static void delFolder(String folderPath) throws Exception{
        delAllFile(folderPath); // 删除完里面所有内容
        String filePath = folderPath;
        java.io.File myFilePath = new java.io.File(filePath);
        myFilePath.delete(); // 删除空文件夹
    }

    public static boolean delAllFile (String folderPath) throws Exception{
        boolean flag = false;
        File file = new File(folderPath);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (folderPath.endsWith(File.separator)) {
                temp = new File(folderPath + tempList[i]);
            } else {
                temp = new File(folderPath + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(folderPath + "/" + tempList[i]);// 先删除文件夹里面的文件
                delFolder(folderPath + "/" + tempList[i]);// 再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }
}
