package cn.edu.fudan.measureservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@Slf4j
@Component
public class FileUtil {

    @Value("${bin.home}")
    private String binHome;

    public int getFileCountInDirectory(String path){
        try{
            Runtime runtime=Runtime.getRuntime();
            String command = binHome+"getFileCount.sh  " +path;
            Process process=runtime.exec(command);
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(process.getInputStream()));
            return Integer.valueOf(bufferedReader.readLine());
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return 0;
    }
}
