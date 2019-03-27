package cn.edu.fudan.measureservice.analyzer;

import cn.edu.fudan.measureservice.domain.Measure;
import cn.edu.fudan.measureservice.handler.ResultHandler;
import javancss.Javancss;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Slf4j
@Component
public class JavaNCSSAnalyzer implements MeasureAnalyzer {

    @Value("${java.ncss.work.home}")
    private String workHome;

    @Value("${result.file.home}")
    private String resultFileHome;


    @Override
    public Measure analyze(String targetPath, String level,ResultHandler resultHandler) {
        File file=new File(targetPath);
        String name;
        if(file.isDirectory()){
            name=targetPath.substring(targetPath.lastIndexOf(File.separator)+1);
        }else{
            name=targetPath.substring(targetPath.lastIndexOf(File.separator)+1,targetPath.lastIndexOf("."));
        }
        String resultFileName=name+".xml";
        try{
            invokeJavaNcss(targetPath,resultFileHome+resultFileName);
            return resultHandler.handle(resultFileName,level);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private void invokeJavaNcss(String targetPath,String outPath) throws IOException {
        String[] asArgs=new String[6];
        asArgs[0]="-all";
        asArgs[1]="-xml";
        asArgs[2]="-recursive";
        asArgs[3]=targetPath;
        asArgs[4]="-out";
        asArgs[5]=outPath;
        new Javancss(asArgs);
    }
}
