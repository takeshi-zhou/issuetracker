package cn.edu.fudan.cloneservice.task;

import cn.edu.fudan.cloneservice.domain.Scan;
import cn.edu.fudan.cloneservice.domain.ScanResult;
import cn.edu.fudan.cloneservice.lock.RedisLuaLock;
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
    @Value("${repoHome}")
    private String repoHome;
    @Value("${inner.service.path}")
    private String innerServicePath;
    @Value("${commit.service.path}")
    private String commitServicePath;
    private KafkaTemplate kafkaTemplate;
    private RestTemplate restTemplate;
    private RedisLuaLock redisLock;
    private HttpHeaders httpHeaders;

    @Autowired
    public void setKafkaTemplate(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    @Autowired
    public void setRedisLock(RedisLuaLock redisLock) {
        this.redisLock = redisLock;
    }
    @Autowired
    public void setHttpHeaders(HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    @SuppressWarnings("unchecked")
    private boolean analyzeResultFile(String scanId,String commitId,String resultFilePath){
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
                    String cloneRawIssueId= UUID.randomUUID().toString();
                    JSONObject cloneRawIssue=new JSONObject();
                    cloneRawIssue.put("uuid",cloneRawIssueId);
                    cloneRawIssue.put("type",group_id);
                    cloneRawIssue.put("category","clone");
                    cloneRawIssue.put("detail",null);
                    cloneRawIssue.put("file_name",filePath);
                    cloneRawIssue.put("scan_id",scanId);
                    cloneRawIssue.put("commit_id",commitId);

                    JSONObject cloneLocation=new JSONObject();
                    cloneLocation.put("uuid",UUID.randomUUID().toString());
                    int startLine=Integer.parseInt(cloneInstance.attributeValue("startLine"));
                    int endLine=Integer.parseInt(cloneInstance.attributeValue("endLine"));
                    cloneLocation.put("start_line",startLine);
                    cloneLocation.put("end_line",endLine);
                    cloneLocation.put("start_token",Integer.parseInt(cloneInstance.attributeValue("startToken")));
                    cloneLocation.put("end_token",Integer.parseInt(cloneInstance.attributeValue("endToken")));
                    cloneLocation.put("file_path",filePath);
                    cloneLocation.put("rawIssue_id",cloneRawIssueId);
                    cloneLocation.put("code",ASTUtil.getCode(startLine,endLine,repoHome+filePath));
                    cloneLocations.add(cloneLocation);

                    cloneRawIssue.put("locations",cloneLocations);
                    cloneRawIssues.add(cloneRawIssue);
                }
            }
            if(!cloneRawIssues.isEmpty()){
                //插入所有的rawIssue
                HttpEntity<Object> requestEntity = new HttpEntity<>(cloneRawIssues, httpHeaders);
                restTemplate.exchange(innerServicePath + "/inner/raw-issue", HttpMethod.POST, requestEntity, JSONObject.class);
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

    private boolean mapping(String repo_id,String current_commit_id,String category){
        HttpEntity<Object> entity=new HttpEntity<>(httpHeaders);
        String pre_commit_id = restTemplate.exchange( innerServicePath+"/inner/scan/last-commit?repo_id="+repo_id+"&category="+category, HttpMethod.GET, entity, String.class).getBody();
        JSONObject requestParam = new JSONObject();
        requestParam.put("repo_id", repo_id);
        if (pre_commit_id != null)
            requestParam.put("pre_commit_id", pre_commit_id);
        else
            requestParam.put("pre_commit_id", current_commit_id);
        requestParam.put("current_commit_id", current_commit_id);
        requestParam.put("category",category);
        logger.info("mapping between " + requestParam.toJSONString());
        HttpEntity<Object> newEntity = new HttpEntity<>(requestParam, httpHeaders);
        JSONObject result = restTemplate.exchange(innerServicePath + "/inner/issue/mapping", HttpMethod.POST, newEntity, JSONObject.class).getBody();
        return result != null && result.getIntValue("code") == 200;
    }

    private boolean checkOut(String repoId, String commitId) {
        JSONObject response = restTemplate.getForObject(commitServicePath + "/checkout?repo_id=" + repoId + "&commit_id=" + commitId, JSONObject.class);
        return response != null && response.getJSONObject("data").getString("status").equals("Successful");
    }

    private void startScan(String repoId,String repoName,String repoPath,Scan scan){
        String scanId=scan.getUuid();
        String commitId=scan.getCommit_id();
        logger.info("start to checkout -> " + commitId);
        //checkout,如果失败发送错误消息，直接返回
        if (!checkOut(repoId, commitId)) {
            send(repoId, commitId, "failed", "check out failed");
            logger.error("Check Out Failed!");
            return;
        }
        logger.info("checkout complete -> start the scan operation......");
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
        if(!mapping(repoId,commitId,scan.getCategory())){
            send(repoId,commitId,"failed","mapping failed!");
            logger.error("mapping failed!");
            return;
        }
        logger.info("mapping success!");
        logger.info("start to insert scan.....");
        scan.setStatus("done");//设为结束状态
        scan.setEnd_time(DateTimeUtil.formatedDate(new Date()));
        HttpEntity<Object> entity=new HttpEntity<>(scan,httpHeaders);
        JSONObject response =restTemplate.exchange(innerServicePath+"/inner/scan", HttpMethod.POST,entity,JSONObject.class).getBody();
        if(response==null||response.getIntValue("code")!=200){
            send(repoId,commitId,"failed","scan add failed!");
            logger.error("scan add failed!");
            return;
        }
        send(repoId,commitId,"success","scan success");
        logger.info("scan complete!");
    }

    @Async
    public Future<String> run(String repoId, String repoName, String repoPath, Scan scan) {
        //获取分布式锁，一个repo同一时间只能有一个线程在扫
        //15min恰好是一个整个Scan操作的超时时间，如果某个线程获得锁之后Scan过程卡死导致锁没有释放
        //如果那个锁成功设置了过期时间，那么key过期后，其他线程自然可以获取到锁
        //如果那个锁并没有成功地设置过期时间
        //那么等待获取同一个锁的线程会因为60s的超时而强行获取到锁，并设置自己的identifier和key的过期时间
        String identifier = redisLock.acquireLockWithTimeOut(repoId, 60, 60, TimeUnit.SECONDS);
        try {
            startScan(repoId, repoName, repoPath, scan);
        } finally {
            if (redisLock.releaseLock(repoId, identifier)) {
                logger.error("repo->" + repoId + " release lock success!");
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
