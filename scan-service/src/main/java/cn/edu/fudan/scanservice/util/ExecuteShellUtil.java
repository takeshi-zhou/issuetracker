package cn.edu.fudan.scanservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class ExecuteShellUtil {

    @Value("${repoHome}")
    private String repoHome;

    @Value("${binHome}")
    private String binHome;

    public  boolean executeAnalyse(String repoPath, String projectName) {
        try {
            Runtime rt = Runtime.getRuntime();
            //String findbugs = "findbugs -xml -output  /home/fdse/issueTracker/resultfile/" + projectName
            //		+ ".xml " + repoPath;
            //脚本实现 用来解耦 还需要与tool解耦合
            String command = binHome + "executeTools.sh " + projectName + " " + repoPath;
           log.info("command -> {}",command);
            Process process = rt.exec(command);
            int exitValue = process.waitFor();
            if (exitValue == 0)
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public  boolean executeMvn(String repoPath) {
        try {
            Runtime rt = Runtime.getRuntime();
            String command = binHome + "executeMvn.sh " + repoPath;
            log.info("command -> {}",command);
            Process process = rt.exec(command);
            int exitValue = process.waitFor();
            if (exitValue == 0)
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public  String getFileLocation(String repoPath, String fileName) {
        try {
            Runtime rt = Runtime.getRuntime();
            String command = "find "+repoHome + repoPath + " -name " + fileName;
            //String command =  binHome + "findOneFile.sh " + repoHome + repoPath + " "+ fileName;
            Process process = rt.exec(command);
            process.waitFor();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            StringBuffer sBuffer = new StringBuffer();
            String line ;
            while ((line = bReader.readLine())!= null) {
                line = line.replace(repoHome, "");
                sBuffer.append(line).append(":");
            }
            return  sBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
