package cn.edu.fudan.measureservice.util;


import cn.edu.fudan.measureservice.domain.CommitInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class GitUtil {

    @Value("${bin.home}")
    private String binHome;

    public  List<String> getCommitFiles(String repoPath,String since,String until){
       List<String> files=new ArrayList<>();
        BufferedReader bufferedReader=null;
        try{
            Runtime runtime=Runtime.getRuntime();
            String command = binHome+ "commitFiles.sh "+repoPath+" "+since+" "+until;
            Process process=runtime.exec(command);
            process.waitFor();
            String s;
            bufferedReader=new BufferedReader(new InputStreamReader(process.getInputStream()));
            while((s=bufferedReader.readLine())!=null){
                s=s.trim();
                if(!s.isEmpty())
                    files.add(s);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(bufferedReader!=null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return files;
    }

    public List<CommitInfo> getCommitInfoByAuthor(String repoPath, String since, String until){
        List<CommitInfo> infos=new ArrayList<>();
        CommitInfo total=new CommitInfo("total",0,0,0);
        int totalCommit=0,totalAdd=0,totalDel=0;
        try{
            Runtime runtime=Runtime.getRuntime();
            String command = binHome+ "commitCountByAuthor.sh "+repoPath+" "+since+" "+until;
            Process process=runtime.exec(command);
            process.waitFor();
            String s;
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(process.getInputStream()));
            while((s=bufferedReader.readLine())!=null){
                s=s.trim();
                if(!s.isEmpty()) {
                    CommitInfo commitInfo=new CommitInfo();
                    String []args=s.split("[\\s\\t]+");
                    int commit=Integer.valueOf(args[0]);
                    commitInfo.setCommit(commit);
                    commitInfo.setAuthor(args[1]);
                    command=binHome+"commitInfoByAuthor.sh "+repoPath+" "+since+" "+until+" "+args[1];
                    Process pro=runtime.exec(command);
                    pro.waitFor();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(pro.getInputStream()));
                    String []out=reader.readLine().split(":");
                    int add=Integer.valueOf(out[0]);
                    int del=Integer.valueOf(out[1]);
                    commitInfo.setAdd(add);
                    commitInfo.setDel(del);
                    totalCommit+=commit;
                    totalAdd+=add;
                    totalDel+=del;
                    infos.add(commitInfo);
                }
            }
            total.setCommit(totalCommit);
            total.setAdd(totalAdd);
            total.setDel(totalDel);
            infos.add(total);
        }catch (Exception e){
            e.printStackTrace();
        }
        return infos;
    }
}
