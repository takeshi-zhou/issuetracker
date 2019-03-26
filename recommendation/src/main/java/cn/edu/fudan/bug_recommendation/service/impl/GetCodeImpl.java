package cn.edu.fudan.bug_recommendation.service.impl;

import cn.edu.fudan.bug_recommendation.service.GetCode;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

@Service
public class GetCodeImpl implements GetCode {
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
