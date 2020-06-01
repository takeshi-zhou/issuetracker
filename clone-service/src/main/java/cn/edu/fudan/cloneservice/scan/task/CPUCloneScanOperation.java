package cn.edu.fudan.cloneservice.scan.task;

import cn.edu.fudan.cloneservice.scan.dao.CloneLocationDao;
import cn.edu.fudan.cloneservice.scan.domain.CloneLocation;
import cn.edu.fudan.cloneservice.scan.domain.CloneScan;
import cn.edu.fudan.cloneservice.scan.domain.CloneScanInitialInfo;
import cn.edu.fudan.cloneservice.scan.domain.CloneScanResult;
import cn.edu.fudan.cloneservice.util.ASTUtil;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * @author zyh
 * @date 2020/5/25
 */
@Component("CPUClone")
public class CPUCloneScanOperation extends ScanOperationAdapter {

    private static final String SNIPPET = "snippet";

    private static final Logger logger= LoggerFactory.getLogger(CPUCloneScanOperation.class);

    @Value("${clone.workHome}")
    private String cloneWorkHome;
    @Value("${clone.resultFileHome}")
    private String cloneResultFileHome;
    @Value("${repoHome}")
    private String repoHome;

    private CloneLocationDao cloneLocationDao;

    @Autowired
    private void setCloneLocationDao(CloneLocationDao cloneLocationDao) {
        this.cloneLocationDao = cloneLocationDao;
    }

    @SuppressWarnings("unchecked")
    private boolean analyzeResultFile(String repoId,String repoPath,String commitId,String resultFilePath, String type){
        SAXReader reader = new SAXReader();
        try{
            Document doc = reader.read(new File(resultFilePath));
            Element root = doc.getRootElement();
            Iterator<Element> iterator = root.elementIterator("group");
            List<CloneLocation> cloneLocationList=new ArrayList<>();
            while (iterator.hasNext()){
                Element group=iterator.next();
                String groupId=group.attributeValue("id");
                Iterator<Element> cloneInstances=group.elementIterator("cloneInstance");
                while(cloneInstances.hasNext()){
                    //一个clone instance是一个rawIssue
                    //List<JSONObject> cloneLocations=new ArrayList<>();
                    Element cloneInstance=cloneInstances.next();
                    String filePath=cloneInstance.attributeValue("path");
                    String cloneLocationId= UUID.randomUUID().toString();
                    CloneLocation cloneLocation = new CloneLocation();
                    cloneLocation.setUuid(cloneLocationId);
                    cloneLocation.setRepoId(repoId);
                    cloneLocation.setCommitId(commitId);
                    cloneLocation.setCategory(groupId);
                    //截取filePath
                    cloneLocation.setFilePath(filePath.substring(repoPath.length() + 1));
                    //clone的方法起始行，结束行
                    String methodStartLine = cloneInstance.attributeValue("methodStartLine");
                    String methodEndLine = cloneInstance.attributeValue("methodEndLine");
                    String methodLines = methodStartLine + "," + methodEndLine;
                    cloneLocation.setMethodLines(methodLines);
                    //具体clone代码的其实行结束行
                    String fragStart=cloneInstance.attributeValue("fragStartLine");
                    String fragEnd=cloneInstance.attributeValue("fragEndLine");
                    String cloneLines = fragStart + "," + fragEnd;
                    cloneLocation.setCloneLines(cloneLines);
                    //method or snippet
                    cloneLocation.setType(type);
                    //类名 方法名
                    cloneLocation.setClassName(cloneInstance.attributeValue("className"));
                    cloneLocation.setMethodName(cloneInstance.attributeValue("methodName"));
                    //具体代码
                    cloneLocation.setCode(ASTUtil.getCode(Integer.parseInt(methodStartLine), Integer.parseInt(methodEndLine),filePath));
                    //插入列表
                    cloneLocationList.add(cloneLocation);
                }
            }
            if(!cloneLocationList.isEmpty()){
                cloneLocationDao.insertCloneLocations(cloneLocationList);
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean invokeCloneTool(String repoPath, String granularity){
        String cmd = "./main.sh " + repoPath + " " + granularity;
        logger.info("command -> {}",cmd);
        try {
            Process processMethod = Runtime.getRuntime().exec(cmd,null,new File(cloneWorkHome));
            processMethod.waitFor();
            if(processMethod.exitValue()==0){
                logger.info("method scan complete -> {}",cmd);
            }
            return processMethod.exitValue()==0;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public CloneScanResult doScan(CloneScanInitialInfo cloneScanInitialInfo) {
        CloneScan cloneScan = cloneScanInitialInfo.getCloneScan();
        String repoId = cloneScan.getRepoId();
        String commitId = cloneScan.getCommitId();
        String type = cloneScan.getType();
        String repoPath = cloneScanInitialInfo.getRepoPath();
        logger.info("start to invoke tool to scan......");
        if (!invokeCloneTool(repoPath, type)) {
            logger.error("Invoke Analyze Tool Failed!");
            //return new CloneScanResult("clone","failed", "tool invoke failed");
            return new CloneScanResult(repoId, commitId, type, "failed", "tool invoke failed");
        }
        logger.info("tool invoke complete!");
        logger.info("scan complete");
        logger.info("start to analyze resultFile......");
        String resultFilePath = cloneResultFileHome;
        //只有片段级的入库
        if(SNIPPET.equals(type)){
            if(!analyzeResultFile(repoId, repoPath, commitId, resultFilePath, type)){
                logger.error("Result File Analyze Failed!");
                return new CloneScanResult(repoId, commitId, type, "failed", "analyze failed");
            }
            logger.info("resultFile analyze complete");
        }
        return new CloneScanResult(repoId, commitId, type, "success", "Scan Success");
    }


}
