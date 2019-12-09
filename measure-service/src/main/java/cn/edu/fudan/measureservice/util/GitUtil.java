package cn.edu.fudan.measureservice.util;


import cn.edu.fudan.measureservice.domain.CommitBase;
import cn.edu.fudan.measureservice.domain.CommitInfo;
import cn.edu.fudan.measureservice.domain.Developer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GitUtil {

    @Value("${bin.home}")
    private String binHome;

    private Logger logger = LoggerFactory.getLogger(GitUtil.class);

    public  List<String> getCommitFiles(String repoPath,String since,String until){
       List<String> files=new ArrayList<>();
        try{
            Runtime runtime=Runtime.getRuntime();
            String command = binHome+ "commitFiles.sh "+repoPath+" "+since+" "+until;
            Process process=runtime.exec(command);
            process.waitFor();
            String s;
            try(BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(process.getInputStream()))){
                while((s=bufferedReader.readLine())!=null){
                    s=s.trim();
                    if(!s.isEmpty()) {
                        files.add(s);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
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


    public int getCommitCount(String repoPath,String since,String until,String author){
        try{
            String command ;
            Runtime runtime=Runtime.getRuntime();
            if(author==null){
                command = binHome+ "commitCount.sh "+repoPath+" "+since+" "+until;
            }else {
                command = binHome+ "commitCount.sh "+repoPath+" "+since+" "+until+" "+author;
            }

            Process process=runtime.exec(command);
            process.waitFor();
            try(BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(process.getInputStream()))){
                return Integer.valueOf(bufferedReader.readLine());
            }catch (Exception e){
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }


    public int[] getRepoLineChanges(String repoPath,String since,String until,String author){
        int[] result = new int[3];
        String out;
        try{
            Runtime runtime=Runtime.getRuntime();
            String command;
            if(author == null){
                command = binHome+ "repoLinesChangesCount.sh "+repoPath+" "+since+" "+until;
            }else{
                command = binHome+ "repoLinesChangesCount.sh "+repoPath+" "+since+" "+until+" "+author;
            }

            Process process=runtime.exec(command);
            process.waitFor();
            try(BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(process.getInputStream()))){
                while((out=bufferedReader.readLine())!=null) {
                    out = out.trim();
                    System.out.println(out);
                    if (!out.isEmpty()) {
                        System.out.println("split:" + out);
                        String[] args = out.trim().split(" ");
                        result[0] = Integer.valueOf(args[0]);
                        result[1] = Integer.valueOf(args[1]);
                        result[2] = Integer.valueOf(args[2]);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }


    public List<Developer> getRepoDevelopers(String repoPath, String since, String until){
        List<Developer> developers = new ArrayList<>();
        String out;
        try{
            Runtime runtime=Runtime.getRuntime();
            String command = binHome+ "getRepoDevelopers.sh "+repoPath+" "+since+" "+until;
            Process process=runtime.exec(command);
            process.waitFor();
            try(BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(process.getInputStream()))){


                while((out=bufferedReader.readLine())!=null){
                    out=out.trim();
                    if(!out.isEmpty()) {
                        Developer developer = new Developer();
                        String []args=out.split("[\\s\\t]+");
                        developer.setName(args[1]);
                        developer.setEmail(args[2]);
                        developers.add(developer);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return developers;
    }


    public CommitBase getOneCommitChanges(String repoPath, String commitId){
        CommitBase commitBase = new CommitBase();
        String out;
        String[] args;
        try{
            Runtime runtime=Runtime.getRuntime();
            String command = binHome+ "linesChangesByCommit.sh "+ repoPath+" "+ commitId;
            Process process=runtime.exec(command);
            process.waitFor();
            try(BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(process.getInputStream()))){

                while((out=bufferedReader.readLine())!=null){
                    out=out.trim();
                    if(!out.isEmpty()) {
                        //当包含files changed或者file changed时，此行是行数变化值
                        if(out.contains("files changed") || out.contains("file changed")){
                            args = out.split("[\\D]+");
                            if(args.length>=3){
                                commitBase.setAddLines(Integer.valueOf(args[1]));
                                commitBase.setDelLines(Integer.valueOf(args[2]));
                            }else{
                                if(out.contains("insertions") || out.contains("insertion")){
                                    commitBase.setAddLines(Integer.valueOf(args[1]));
                                    commitBase.setDelLines(0);
                                }else{
                                    commitBase.setAddLines(0);
                                    commitBase.setDelLines(Integer.valueOf(args[1]));
                                }
                                logger.info(" the result is -->{}" ,out);
                            }
                        }else if(out.contains("Author")){
                            String name ="";
                            String email = "";
                            args = out.split("[\\s]+");
                            Developer author = new Developer();
                            for(int i= 1;i< args.length;i++){
                                if(args[i].startsWith("<") && args[i].endsWith(">")){
                                    String judge;
                                    judge = args[i].substring(1,args[i].length()-1);
                                    Pattern pattern = Pattern.compile("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
                                    Matcher matcher = pattern.matcher(judge);
                                    if (!matcher.matches()) {
                                        logger.warn("invalid email format --> {} which commit id is --> {}",judge,commitId);

                                    }
                                    email = judge;
                                }else{
                                    if("".equals(name)){
                                        name = args[i];
                                    }else{
                                        name = name + " " +args[i];
                                    }
                                }
                            }
                            author.setName(name);
                            author.setEmail(email);
                            commitBase.addAuthor(author);
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return commitBase;
    }





}
