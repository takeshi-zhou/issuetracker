package cn.edu.fudan.diffservice.service;

import cn.edu.fudan.diffservice.entity.Global;
import cn.edu.fudan.diffservice.util.FileUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class FetchMetaCacheHandler {

    @Value("${repoDetailPath}")
    private String repoDetailPath;
    @Value("${CLDIFFPath}")
    private String CLDIFFPath;
    @Value("${excDiffServiceShPath}")
    private String excDiffServicePath;
    public String handle(String diffparam) {
        System.out.println(diffparam);
//        diffparam = diffparam.replace("%7B","{").replace("%22","\"").replace("%3A",":")
////                .replace("%2C",",").replace("%2F","/").replace("%7D","}");
////        diffparam = diffparam.substring(0,diffparam.length()-1);

        System.out.println("FetchMetaCacheHandler");
        try {
            System.out.println("diffparam: " + diffparam);
            Map<String,Object> map = new HashMap<>();
            map = JSON.parseObject(diffparam,Map.class);

            String repoPath = map.get("repoPath").toString();
            String commitId = map.get("commitId").toString();
            String isSolved = map.get("isSolved").toString();

            System.out.println("repoPath: "+ map.get("repoPath"));
            System.out.println("commitId: "+ map.get("commitId"));
            System.out.println("isSolved: "+ map.get("isSolved"));

            String realRepoPath= "";
            String[] repoPaths = repoPath.split("/");
            for(int i=0;i<2;i++){
                realRepoPath += repoPaths[i];
                realRepoPath += "/";
            }
            //应为绝对路径
            realRepoPath =  realRepoPath + ".git";

            String[] data = realRepoPath.split("/");
            Global.projectName = data[data.length-2];
            String projectOwnerName = data[data.length-3];


            Global.repoPath = repoDetailPath + realRepoPath;

            System.out.println("Global.projectName:"+Global.projectName);
            System.out.println("Global.repoPath:"+Global.repoPath);
            String meta = null;

                /*
                    当isSolved的为false的时候只需要一个commit跟repoPath
                 */
            if(!Boolean.parseBoolean(isSolved)){
                Object prevScanObject = map.get("prevScan");
                if(prevScanObject == null){
                    File metaFile = new File(Global.outputDir + "/" + projectOwnerName + "/" + Global.projectName + "/" + commitId + "/meta.json");

                    if (!metaFile.exists()) {
                        synchronized(this){
                            //生成文件
                            //文件路径为global_Path/project_name/commit_id/meta.txt
                            try{
                                String command = excDiffServicePath + " " + Global.outputDir + " " + projectOwnerName +" "+ Global.projectName +" "+ commitId +" "+"one"+" " + commitId+" "+Global.outputDir+" "+Global.repoPath;
                                System.out.println("command: " +command);
                                Process process = Runtime.getRuntime().exec(command);
                                process.waitFor();
//                          String cmd = "java -jar " + "CLDiffServerOffline.jar " + Global.outputDir + " " + projectOwnerName +" "+ Global.projectName +" "+ commitId +" "+"one"+" " + commitId+" "+Global.outputDir+" "+Global.repoPath + " > diff-service-3.log 2>&1";
                                //meta = generateCLDIFFResult(commitId, metaFile, Global.outputDir);
                                BufferedReader bReader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
                                StringBuffer sBuffer = new StringBuffer();
                                String line ;
                                while ((line = bReader.readLine())!= null) {
                                    sBuffer.append(line).append("/n");
                                }
                                System.out.println("process's content: " + sBuffer.toString());
                            }catch(Exception e){
                                e.printStackTrace();
                                return "Error in invoking tool";
                            }
                            meta = FileUtil.read(Global.outputDir + "/" + projectOwnerName + "/" + Global.projectName + "/" + commitId + "/meta.json");
                        }
                    } else {
                        meta = FileUtil.read(Global.outputDir + "/" + projectOwnerName + "/" + Global.projectName + "/" + commitId + "/meta.json");
                    }
                }else {
                    String prevScan = prevScanObject.toString();
                    System.out.println("prevScan: "+ map.get("prevScan"));
                    File metaFile = new File(Global.outputDir + "/" + projectOwnerName + "/" + Global.projectName + "/" + commitId + "/meta.json");

                    if (!metaFile.exists()) {
                        //生成文件
                        //文件路径为global_Path/project_name/commitId/meta.txt
                        synchronized (this){
                            try{
                                String command = excDiffServicePath + " " + Global.outputDir +" "+projectOwnerName+" "+ Global.projectName+" "+commitId+" "+"two"+" " + prevScan+" "+commitId+" "+Global.outputDir+" "+Global.repoPath;
                                System.out.println("command: " +command);
                                Process process = Runtime.getRuntime().exec(command);
                                process.waitFor();
                                //meta = generateTwoCommitsCLDIFFResult(prevScan,commitId,metaFile,Global.outputDir);
//                          String cmd = "java -jar " + "CLDiffServerOffline.jar" + " " +Global.outputDir +" "+projectOwnerName+" "+ Global.projectName+" "+commitId+" "+"two"+" " + prevScan+" "+commitId+" "+Global.outputDir+" "+Global.repoPath + " > diff-service-3.log 2>&1";
                                BufferedReader bReader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
                                StringBuffer sBuffer = new StringBuffer();
                                String line ;
                                while ((line = bReader.readLine())!= null) {
                                    sBuffer.append(line).append("/n");
                                }
                                System.out.println("process's content: " + sBuffer.toString());
                            }catch(Exception e){
                                e.printStackTrace();
                                return "Error in invoking tool";
                            }
                            meta = FileUtil.read(Global.outputDir + "/" + projectOwnerName + "/" + Global.projectName + "/" + commitId + "/meta.json");
                        }
                    } else {
                        meta = FileUtil.read(Global.outputDir + "/" + projectOwnerName + "/" + Global.projectName + "/" + commitId + "/meta.json");
                    }
                }

            }else{
                String nextScan = map.get("nextScan").toString();
                System.out.println("nextScan: "+ map.get("nextScan"));
                File metaFile = new File(Global.outputDir + "/" + projectOwnerName + "/" + Global.projectName + "/" + nextScan + "/meta.json");

                if (!metaFile.exists()) {
                    synchronized (this){
                        //生成文件
                        //文件路径为global_Path/project_name/nextScan/meta.txt
                        try{
                            String command = excDiffServicePath + " " + Global.outputDir+" "+projectOwnerName+" "+Global.projectName+ " "+nextScan+" "+"two"+" " + commitId+" "+nextScan+" "+Global.outputDir+" "+Global.repoPath;
                            System.out.println("command: " +command);
                            Process process = Runtime.getRuntime().exec(command);
                            //meta = generateTwoCommitsCLDIFFResult(commitId,nextScan,metaFile,Global.outputDir);
                            //String cmd = "java -jar " + "CLDiffServerOffline.jar" + " " +Global.outputDir+" "+projectOwnerName+" "+Global.projectName+ " "+nextScan+" "+"two"+" " + commitId+" "+nextScan+" "+Global.outputDir+" "+Global.repoPath + " > diff-service-3.log 2>&1";
                            process.waitFor();
                            BufferedReader bReader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
                            StringBuffer sBuffer = new StringBuffer();
                            String line ;
                            while ((line = bReader.readLine())!= null) {
                                sBuffer.append(line).append(":");
                                sBuffer.append(line).append("/n");
                            }
                            System.out.println("process's content: " + sBuffer.toString());
                        }catch(Exception e){
                            return "Error in invoking tool";
                        }
                        meta = FileUtil.read(Global.outputDir + "/" + projectOwnerName + "/" + Global.projectName + "/" + nextScan + "/meta.json");
                    }
                } else {
                    meta = FileUtil.read(Global.outputDir + "/" + projectOwnerName + "/" + Global.projectName + "/" + nextScan + "/meta.json");
                }
            }
            System.out.println(meta);
            return meta;
        }catch(Exception e){
            e.printStackTrace();
            try {
                return "The wrong parameters were passed";

            }catch (Exception e2){
                e2.printStackTrace();
            }
        }
        return "Uncaught error";
    }
}
