package cn.edu.fudan.cloneservice.task;

import cn.edu.fudan.cloneservice.dao.CloneLocationDao;
import cn.edu.fudan.cloneservice.dao.CloneRawIssueDao;
import cn.edu.fudan.cloneservice.domain.CloneLocation;
import cn.edu.fudan.cloneservice.domain.CloneRawIssue;
import cn.edu.fudan.cloneservice.domain.Scan;
import cn.edu.fudan.cloneservice.domain.ScanResult;
import cn.edu.fudan.cloneservice.lock.RedisLock;
import cn.edu.fudan.cloneservice.util.ASTUtil;
import cn.edu.fudan.cloneservice.util.DateTimeUtil;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author WZY
 * @version 1.0
 **/
@Component
public class CloneScanTask {

    private static final Logger logger= LoggerFactory.getLogger(CloneScanTask.class);

    @Value("${resultFileHome}")
    private String resultFileHome;
    @Value("${inner.service.path}")
    private String innerServicePath;
    private KafkaTemplate kafkaTemplate;
    private CloneRawIssueDao cloneRawIssueDao;
    private CloneLocationDao clonelocationDao;
    private RestTemplate restTemplate;
    private RedisLock redisLock;
    private HttpHeaders httpHeaders;

    @Autowired
    public CloneScanTask(KafkaTemplate kafkaTemplate,
                                CloneRawIssueDao cloneRawIssueDao,
                                CloneLocationDao clonelocationDao,
                                RestTemplate restTemplate,
                                RedisLock redisLock,
                                HttpHeaders httpHeaders) {
        this.kafkaTemplate = kafkaTemplate;
        this.cloneRawIssueDao = cloneRawIssueDao;
        this.clonelocationDao = clonelocationDao;
        this.restTemplate = restTemplate;
        this.redisLock=redisLock;
        this.httpHeaders=httpHeaders;
    }

    @SuppressWarnings("unchecked")
    private boolean analyzeResultFile(String scanId,String commitId,String resultFilePath){
        SAXReader reader = new SAXReader();
        try{
            Document doc = reader.read(new File(resultFilePath));
            Element root = doc.getRootElement();
            Iterator<Element> iterator = root.elementIterator("group");
            List<CloneRawIssue> cloneRawIssues=new ArrayList<>();
            List<CloneLocation> cloneLocations=new ArrayList<>();
            while (iterator.hasNext()){
                Element group=iterator.next();
                String cloneRawIssueId= UUID.randomUUID().toString();
                String group_id=group.attributeValue("id");
                CloneRawIssue cloneRawIssue=new CloneRawIssue();
                cloneRawIssue.setUuid(cloneRawIssueId);
                cloneRawIssue.setGroup_id(group_id);
                cloneRawIssue.setCommit_id(commitId);
                cloneRawIssue.setScan_id(scanId);
                cloneRawIssues.add(cloneRawIssue);
                Iterator<Element> cloneInstances=group.elementIterator("cloneInstance");
                while(cloneInstances.hasNext()){
                    Element cloneInstance=cloneInstances.next();
                    CloneLocation cloneLocation=new CloneLocation();
                    cloneLocation.setUuid(UUID.randomUUID().toString());
                    int startLine=Integer.parseInt(cloneInstance.attributeValue("startLine"));
                    int endLine=Integer.parseInt(cloneInstance.attributeValue("endLine"));
                    cloneLocation.setStart_line(startLine);
                    cloneLocation.setEnd_line(endLine);
                    cloneLocation.setStart_token(Integer.parseInt(cloneInstance.attributeValue("startToken")));
                    cloneLocation.setEnd_token(Integer.parseInt(cloneInstance.attributeValue("endToken")));
                    String filePath=cloneInstance.attributeValue("path");
                    cloneLocation.setFile_path(filePath);
                    cloneLocation.setGroup_id(group_id);
                    cloneLocation.setClone_rawIssue_id(cloneRawIssueId);
                    cloneLocation.setCode(ASTUtil.getCodeFormSmb(startLine,endLine,filePath));
                    cloneLocations.add(cloneLocation);
                }
            }
            if(!cloneRawIssues.isEmpty()){
                cloneRawIssueDao.insertCloneRawIssueList(cloneRawIssues);
            }
            if(!cloneLocations.isEmpty()){
                clonelocationDao.insertCloneLocationList(cloneLocations);
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean invokeCloneTool(String repoName,String repoPath){
        //调用克隆工具，生成结果文件到本地某个固定的目录，文件名以repoName命名
        //结果文件中文件的路径直接用smb的路径，类似于smb://fdse:smbcloudfdse@10.141.221.80/Share/test/testRepo/test.java
        return true;
    }

    private boolean mapping(String repo_id,String current_commit_id){
        return true;
    }



    private void startScan(String repoId,String repoName,String repoPath,Scan scan){
        String scanId=scan.getUuid();
        String commitId=scan.getCommit_id();
        logger.info("start to invoke clone tool to scan......");
        if(!invokeCloneTool(repoName, repoPath)){
            send(repoId,commitId,"failed","tool invoke failed!");
            logger.error("tool invoke failed!");
            return;
        }
        logger.info("tool invoke complete!");
        logger.info("start to analyze result file");
        String resultFilePath=resultFileHome+repoName+".xml";
        if(!analyzeResultFile(scanId,commitId,resultFilePath)){
            send(repoId,commitId,"failed","file analyze failed!");
            logger.error("file analyze failed!");
            return;
        }
        logger.info("file analyze success!");
        logger.info("start to mapping......");
        if(!mapping(repoId,commitId)){
            send(repoId,commitId,"failed","mapping failed!");
            logger.error("mapping failed!");
            return;
        }
        logger.info("mapping success!");
        logger.info("start to insert scan.....");
        scan.setStatus("done");//设为结束状态
        scan.setEnd_time(DateTimeUtil.formatedDate(new Date()));
        HttpEntity<String> entity=new HttpEntity<>(httpHeaders);
        JSONObject response =restTemplate.exchange(innerServicePath+"/inner/scan", HttpMethod.POST,entity,JSONObject.class,scan).getBody();
        if(response==null||response.getIntValue("code")!=200){
            send(repoId,commitId,"failed","scan add failed!");
            logger.error("scan add failed!");
            return;
        }
        send(repoId,commitId,"success","scan success");
        logger.error("scan complete!");
    }

    @Async
    public Future<String> run(String repoId, String repoName, String repoPath, Scan scan) {
        //获取分布式锁，一个repo同一时间只能有一个线程在扫
        //15min恰好是一个整个Scan操作的超时时间，如果某个线程获得锁之后Scan过程卡死导致锁没有释放
        //如果那个锁成功设置了过期时间，那么key过期后，其他线程自然可以获取到锁
        //如果那个锁并没有成功地设置过期时间
        //那么等待获取同一个锁的线程会因为15s的超时而强行获取到锁，并设置自己的identifier和key的过期时间
        String identifier = redisLock.acquireLock(repoId, 15, 15, TimeUnit.SECONDS);
        try {
            startScan(repoId, repoName, repoPath, scan);
        } finally {
            if (redisLock.releaseLock(repoId, identifier)) {
                logger.error("repo->" + repoId + " release lock failed!");
            }
        }
        return new AsyncResult<>("complete");

    }

    @SuppressWarnings("unchecked")
    private void send(String repoId, String commitId ,String status, String description) {
        ScanResult scanResult = new ScanResult(repoId, commitId,"clone" ,status, description);
        kafkaTemplate.send("ScanResult", JSONObject.toJSONString(scanResult));
    }


}