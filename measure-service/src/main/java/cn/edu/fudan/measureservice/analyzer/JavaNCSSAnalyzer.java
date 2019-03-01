package cn.edu.fudan.measureservice.analyzer;

import cn.edu.fudan.measureservice.domain.Measure;
import cn.edu.fudan.measureservice.handler.ResultHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class JavaNCSSAnalyzer implements MeasureAnalyzer {

    @Value("${java.ncss.work.home}")
    private String workHome;

    @Value("${result.file.home}")
    private String resultFileHome;


    @Override
    public Measure analyze(String targetPath, ResultHandler resultHandler) {
        File file=new File(targetPath);
        String name;
        if(file.isDirectory()){
            name=targetPath.substring(targetPath.lastIndexOf(File.separator)+1);
        }else{
            name=targetPath.substring(targetPath.lastIndexOf(File.separator)+1,targetPath.lastIndexOf("."));
        }
        String resultFileName=name+".xml";
        Runtime runtime=Runtime.getRuntime();
        String command=workHome+"javancss.bat -all -xml -recursive "+targetPath +" > " +resultFileHome+resultFileName;
        System.out.println(command);
        try{
            Process process = runtime.exec(command);
            process.waitFor();
            return resultHandler.handle(resultFileName);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
