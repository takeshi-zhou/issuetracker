package cn.edu.fudan.issueservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ExecuteShellUtil {

    @Value("${binHome}")
    private String binHome;

    public Map<String, Integer> developersLinesOfCode(String start, String end, String repoPath, List<String> user) {
        Runtime rt = Runtime.getRuntime();
        StringBuilder sb = new StringBuilder(binHome + "lineNumberOfDevelopers.sh " + repoPath + " " + start + " " + end + " ");
        for (String userName : user) {
            sb.append(userName);
            sb.append(" ");
        }
        String command = sb.toString();
        log.info("command -> {}",command);
        Map<String,Integer> usersCodeLine = new ConcurrentHashMap<>();
        try {
            Process process = rt.exec(command);
            if (process.waitFor() == 1){
                throw new RuntimeException("execute bin error!");
            }
            BufferedReader bReader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            String line;
            String userName = "";
            boolean isNumber = false;
            while ((line = bReader.readLine())!= null) {
                if (isNumber) {
                    usersCodeLine.put( userName,Integer.valueOf(line));
                    isNumber = false;
                }else {
                    userName = line;
                    isNumber = true;
                }
            }
        }catch (Exception e) {
            log.info(e.getMessage());
        }
        return usersCodeLine;
    }

}
