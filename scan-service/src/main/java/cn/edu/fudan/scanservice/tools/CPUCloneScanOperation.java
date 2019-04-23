package cn.edu.fudan.scanservice.tools;

import cn.edu.fudan.scanservice.domain.Scan;
import cn.edu.fudan.scanservice.domain.ScanInitialInfo;
import cn.edu.fudan.scanservice.domain.ScanResult;
import cn.edu.fudan.scanservice.util.ASTUtil;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * @author WZY
 * @version 1.0
 **/
@Component("CPUClone")
public class CPUCloneScanOperation extends ScanOperationAdapter {

    private static final Logger logger= LoggerFactory.getLogger(CPUCloneScanOperation.class);

    @Value("${clone.workHome}")
    private String cloneWorkHome;
    @Value("${clone.resultFileHome}")
    private String cloneResultFileHome;
    @Value("${repoHome}")
    private String repoHome;


    @SuppressWarnings("unchecked")
    private boolean analyzeResultFile(String repo_id,String repoPath,String scanId,String commitId,String resultFilePath){
        SAXReader reader = new SAXReader();
        try{
            Document doc = reader.read(new File(resultFilePath));
            Element root = doc.getRootElement();
            Iterator<Element> iterator = root.elementIterator("group");
            List<JSONObject> cloneRawIssues=new ArrayList<>();
            while (iterator.hasNext()){
                Element group=iterator.next();
                String group_id=group.attributeValue("id");
                Iterator<Element> cloneInstances=group.elementIterator("cloneInstance");
                while(cloneInstances.hasNext()){
                    //一个clone instance是一个rawIssue
                    List<JSONObject> cloneLocations=new ArrayList<>();
                    Element cloneInstance=cloneInstances.next();
                    String filePath=cloneInstance.attributeValue("path");
                    String rank=cloneInstance.attributeValue("rank");
                    String cloneRawIssueId= UUID.randomUUID().toString();
                    JSONObject cloneRawIssue=new JSONObject();
                    cloneRawIssue.put("uuid",cloneRawIssueId);
                    cloneRawIssue.put("type",group_id);
                    cloneRawIssue.put("category","clone");
                    cloneRawIssue.put("detail","{\"rank\":"+rank+"}");
                    cloneRawIssue.put("file_name",filePath);
                    cloneRawIssue.put("scan_id",scanId);
                    cloneRawIssue.put("commit_id",commitId);
                    cloneRawIssue.put("repo_id",repo_id);

                    JSONObject cloneLocation=new JSONObject();
                    cloneLocation.put("uuid",UUID.randomUUID().toString());
                    int startLine=Integer.parseInt(cloneInstance.attributeValue("methodStartLine"));
                    int endLine=Integer.parseInt(cloneInstance.attributeValue("methodEndLine"));
                    String bugLines=null;
                    int fragStart=Integer.parseInt(cloneInstance.attributeValue("fragStartLine"));
                    int fragEnd=Integer.parseInt(cloneInstance.attributeValue("fragEndLine"));
                    StringBuilder sb=new StringBuilder();
                    while(fragStart<=fragEnd){
                        sb.append(",");
                        sb.append(fragStart);
                        fragStart++;
                    }
                    if (sb.length() > 0)
                        bugLines = sb.deleteCharAt(0).toString();
                    cloneLocation.put("start_line",startLine);
                    cloneLocation.put("end_line",endLine);
                    cloneLocation.put("bug_lines",bugLines);
                    cloneLocation.put("start_token",null);
                    cloneLocation.put("end_token",null);
                    cloneLocation.put("class_name", cloneInstance.attributeValue("className"));
                    cloneLocation.put("method_name", cloneInstance.attributeValue("methodName"));
                    cloneLocation.put("file_path",filePath);
                    cloneLocation.put("rawIssue_id",cloneRawIssueId);
                    cloneLocation.put("code", ASTUtil.getCode(startLine,endLine,repoPath+"/"+filePath));
                    cloneLocations.add(cloneLocation);

                    cloneRawIssue.put("locations",cloneLocations);
                    cloneRawIssues.add(cloneRawIssue);
                }
            }
            if(!cloneRawIssues.isEmpty()){
                //插入所有的rawIssue
                restInterfaceManager.insertRawIssuesWithLocations(cloneRawIssues);
            }

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    private boolean invokeCloneTool(String repoPath,String repoName){
        //repoPath = repoPath.substring(repoPath.indexOf("/") + 1);//去除github前缀
        //String cmd = "java -jar CodeLexer.jar  " + repoHome+repoPath + " " + repoName;
        String cmd = "java -jar SAGA-CPUv423.jar " + repoPath + " " + repoName;
        logger.info("command -> {}",cmd);
        try {
            Process process = Runtime.getRuntime().exec(cmd,null,new File(cloneWorkHome));
            //输出process打印信息
            process.waitFor();
            return process.exitValue()==0;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ScanResult doScan(ScanInitialInfo scanInitialInfo) {
        Scan scan = scanInitialInfo.getScan();
        String scanId=scan.getUuid();
        String repoId=scan.getRepo_id();
        String commitId=scan.getCommit_id();
        String repoPath = scanInitialInfo.getRepoPath();
        String repoName = scanInitialInfo.getRepoName();
        logger.info("start to invoke tool to scan......");
        if (!invokeCloneTool(repoPath, repoName)) {
            logger.error("Invoke Analyze Tool Failed!");
            return new ScanResult("clone","failed", "tool invoke failed");
        }
        logger.info("scan complete");
        logger.info("start to analyze resultFile......");
        logger.info("tool invoke complete!");
        String resultFilePath1=cloneResultFileHome+repoName+"_A.xml";
        if(!analyzeResultFile(repoId,repoPath,scanId,commitId,resultFilePath1)){
            logger.error("Result File Analyze Failed!");
            return new ScanResult("clone","failed", "analyze failed");
        }
        logger.info("resultFile analyze complete");
        return new ScanResult("clone","success", "Scan Success!");
    }
}
