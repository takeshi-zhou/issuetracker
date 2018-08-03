package cn.edu.fudan.scanscheduler.findbug;

import cn.edu.fudan.scanscheduler.domain.*;
import cn.edu.fudan.scanscheduler.service.impl.ScanOperationAdapter;
import cn.edu.fudan.scanscheduler.util.ASTUtil;
import cn.edu.fudan.scanscheduler.util.ExcuteShellUtil;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

@Component("findBug")
public class FindBugScanOperation extends ScanOperationAdapter {

    private final static Logger logger = LoggerFactory.getLogger(FindBugScanOperation.class);

    @Value("${resultFileHome}")
    private String resultFileHome;
    @Value("${repoHome}")
    private String repoHome;
    @Value("${issue.service.path}")
    private String issueServicePath;

    @SuppressWarnings("unchecked")
    private String getJsonString(Element element){
        List<Attribute> attributes=(List<Attribute>)element.attributes();
        StringBuilder json=new StringBuilder();
        json.append("{");
        for(int i=0;i<attributes.size();i++){
            json.append("\"");
            json.append(attributes.get(i).getName());
            json.append("\":\"");
            json.append(attributes.get(i).getValue());
            json.append("\"");
            if(i!=attributes.size()-1)
                json.append(",");
        }
        json.append("}");
        return json.toString();
    }

    private boolean executeAnalyzeTool(String repoPath,String projectName){
        return ExcuteShellUtil.excuteAnalyse(repoPath,projectName);
    }

    @SuppressWarnings("unchecked")
    private String  analyzeLocations(String rawIssueUUID,String projectName,Element bugInstance,List<JSONObject> locations){
        Element sourceLineInClass=bugInstance.element("Class").element("SourceLine");
        String className=sourceLineInClass.attributeValue("classname");
        String fileName=sourceLineInClass.attributeValue("sourcefile");
        String filePath=ExcuteShellUtil.getFileLocation(projectName,fileName);
        if(filePath==null){
            logger.error(fileName+" 找不到源文件！");
            return null;
        }
        //Method节点
        Element method=bugInstance.element("Method");
        Iterator<Element> iterator=bugInstance.elementIterator("SourceLine");
        if(method==null)
            return null;
        String methodName = method.attributeValue("name");
        Element sourceLineInMethod = method.element("SourceLine");
        int start = Integer.parseInt(sourceLineInMethod.attributeValue("start"));
        int end = Integer.parseInt(sourceLineInMethod.attributeValue("end"));
        String bugLines=null;
        if(iterator!=null){
            Set<String> container=new HashSet<>();
            StringBuilder bugLineBuilder=new StringBuilder();
            while(iterator.hasNext()){
                Element SourceLine=iterator.next();
                String bugLine=SourceLine.attributeValue("start");
                if(!container.contains(bugLine)){
                    container.add(bugLine);
                    bugLineBuilder.append(",");
                    bugLineBuilder.append(bugLine);
                }
            }
            if(bugLineBuilder.length()>0)
                bugLines=bugLineBuilder.deleteCharAt(0).toString();

            String code = ASTUtil.getFullCode(repoHome + filePath);
            JSONObject location=new JSONObject();
            location.put("uuid",UUID.randomUUID().toString());
            location.put("start_line",start);
            location.put("end_line",end);
            location.put("bug_lines",bugLines);
            location.put("file_path",filePath);
            location.put("class_name",className);
            location.put("method_name",methodName);
            location.put("rawIssue_id",rawIssueUUID);
            location.put("code",code);
            locations.add(location);
        }
        return fileName;
    }

    @SuppressWarnings("unchecked")
    private boolean analyzeXML(Scan scan,String projectName,String xmlPath){
        SAXReader reader=new SAXReader();
        try {
            Document doc=reader.read(new File(xmlPath));
            Element root=doc.getRootElement();
            Element summary=root.element("FindBugsSummary");
            String result_summary=getJsonString(summary);
            scan.setResult_summary(result_summary);

            Iterator<Element> iterator=root.elementIterator("BugInstance");
            List<JSONObject> rawIssues=new ArrayList<>();
            List<JSONObject> locations=new ArrayList<>();
            while(iterator.hasNext()){
                Element bugInstance=iterator.next();
                String rawIssueUUID=UUID.randomUUID().toString();
                //解析当前bugInstance中的location
                String fileName=analyzeLocations(rawIssueUUID,projectName,bugInstance,locations);
                if(fileName!=null){
                    //只有location解析成功才会插入当前rawIssue
                    JSONObject rawIssue=new JSONObject();
                    rawIssue.put("type",bugInstance.attributeValue("type"));
                    rawIssue.put("detail",getJsonString(bugInstance));
                    rawIssue.put("file_name",fileName);
                    rawIssue.put("scan_id",scan.getUuid());
                    rawIssue.put("commit_id",scan.getCommit_id());
                    rawIssue.put("uuid",rawIssueUUID);
                    rawIssues.add(rawIssue);
                }
            }
            if(!rawIssues.isEmpty()){
                //插入所有的rawIssue
                restTemplate.postForEntity(issueServicePath+"/RawIssue/add",rawIssues,JSONObject.class).getBody();
            }

            if(!locations.isEmpty()){
                //插入所有的location
                restTemplate.postForEntity(issueServicePath+"/Location/add",locations,JSONObject.class).getBody();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean compile(String repoPath){
      return ExcuteShellUtil.excuteMvn(repoPath);
    }

    @Override
    public ScanResult doScan(ScanInitialInfo scanInitialInfo) {
         Scan scan=scanInitialInfo.getScan();
         String repoPath=scanInitialInfo.getRepoPath();
         String projectName=scanInitialInfo.getProjectName();

        logger.info("start to compile the repository ->" +repoPath);
        if(!compile(repoPath)){
            logger.error("Project Compile Failed!");
            return new ScanResult("failed","compile failed");
        }
        logger.info("compile complete");
        logger.info("start to invoke tool to scan......");
        //如果编译成功,调用findBug得到输出的XML文件
        if(!executeAnalyzeTool(repoPath,projectName)){
            logger.error("Invoke Analyze Tool Failed!");
            return new ScanResult("failed","tool invoke failed");
        }
        logger.info("scan complete");
        logger.info("start to analyze resultFile......");
        //如果fingBug调用成功，开始解析XML文件
        String xmlPath=resultFileHome+projectName+".xml";
        if(!analyzeXML(scan,projectName,xmlPath)){
            logger.error("Result File Analyze Failed!");
            return new ScanResult("failed","analyze failed");
        }
        logger.info("resultFile analyze complete");
        return new ScanResult("success","Scan Success!");
    }
}
