package cn.edu.fudan.scanservice.service.impl;

import cn.edu.fudan.scanservice.domain.ScanInitialInfo;
import cn.edu.fudan.scanservice.domain.ScanMessage;
import cn.edu.fudan.scanservice.domain.ScanResult;
import cn.edu.fudan.scanservice.service.KafkaService;
import cn.edu.fudan.scanservice.service.ScanOperation;
import cn.edu.fudan.scanservice.util.DateTimeUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author WZY
 * @version 1.0
 **/
@Service
public class KafkaServiceImpl implements KafkaService{

    private Logger logger= LoggerFactory.getLogger(ScanServiceImpl.class);

    @Value("${project.service.path}")
    private String projectServicePath;

    @Value("${commit.service.path}")
    private String commitServicePath;

    //这边注入的是findBug的扫描的实现方式，如果是其它工具，可以换作其它实现
    @Resource(name="findBug")
    private ScanOperation scanOperation;

    private KafkaTemplate kafkaTemplate;

    @Autowired
    public void setKafkaTemplate(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    //初始化project的一些状态,表示目前正在scan
    private void initialProject(String projectId){
        JSONObject postData = new JSONObject();
        postData.put("uuid", projectId);
        postData.put("scan_status","Scanning");
        postData.put("last_scan_time", DateTimeUtil.format(new Date()));
        updateProject(postData);
    }

    @SuppressWarnings("unchecked")
    private void updateProject(JSONObject projectParam){
        //JSONObject json = restTemplate.postForEntity(projectServicePath+"/update", projectParam, JSONObject.class).getBody();
        try{
            restTemplate.put(projectServicePath,projectParam);
        }catch (Exception e){
            throw new RuntimeException("project status initial failed!");
        }
    }

    @SuppressWarnings("unchecked")
    private void send(String projectId,String commitId,String status,String description){
        ScanResult scanResult=new ScanResult(projectId,commitId,status,description);
        kafkaTemplate.send("ScanResult", JSONObject.toJSONString(scanResult));
    }

    private void scan(String projectId,String commitId){
        if(scanOperation.isScanned(commitId)){
            //如果当前commit已经扫描过，直接结束
            logger.info("this commit has been scanned");
            send(projectId,commitId,"success","scan success!");
            logger.info("Scan Success!");
            return;
        }
        logger.info("this commit has not been scanned");
        logger.info("start to checkout -> "+commitId);
        //checkout,如果失败发送错误消息，直接返回
        if(!scanOperation.checkOut(projectId,commitId)){
            // updateCommitScanStatus listen message
            send(projectId,commitId,"failed","check out failed");
            logger.error("Check Out Failed!");
            return;
        }
        logger.info("checkout complete -> start the scan operation......");

        ScanInitialInfo scanInitialInfo= scanOperation.initialScan(projectId,commitId);
        ScanResult scanResult=scanOperation.doScan(scanInitialInfo);
        if(scanResult.getStatus().equals("failed")){
            send(projectId,commitId,"failed",scanResult.getDescription());
            logger.error(scanResult.getDescription());
            return;
        }
        logger.info("scan complete ->"+scanResult.getDescription());
        logger.info("start to mapping ......");
        /**
         * 扫描成功，开始映射
         * 拆分成消息服务 unifying service
         */
        if(!scanOperation.mapping(projectId,commitId)){
            send(projectId,commitId,"failed","check out failed");
            logger.error("Mapping Failed!");
            return;
        }
        logger.info("mapping complete");
        //映射结束，更新当前scan
        logger.info("start to update scan status");
        if(!scanOperation.updateScan(scanInitialInfo)){
            send(projectId,commitId,"failed","scan update failed");
            logger.error("Scan Update Failed!");
            return;
        }
        logger.info("scan update complete");
        send(projectId,commitId,"success","all complete");
    }

    /**
     * 通过前台请求触发scan操作,注意是异步操作
     * @author WZY
     */
    @SuppressWarnings("unchecked")
    @Async
    @Override
    public void scanByRequest(JSONObject requestParam) {
        String projectId=requestParam.getString("projectId");
        String commitId=requestParam.getString("commitId");
        initialProject(projectId);
        scan(projectId,commitId);
    }

    /**
     * 监听消息队列中project的scan消息触发scan操作
     * @author WZY
     */
    @Override
    @KafkaListener(id="projectScan",topics = {"Scan"},groupId = "scan")
    public void scanByMQ(ConsumerRecord<String, String> consumerRecord) {
        String msg=consumerRecord.value();
        logger.info("received message from topic -> "+consumerRecord.topic()+" : "+msg);
        ScanMessage scanMessage= JSONObject.parseObject(msg,ScanMessage.class);
        String projectId=scanMessage.getProjectId();
        String commitId=scanMessage.getCommitId();
        initialProject(projectId);
        scan(projectId,commitId);
    }


    /**
     * 根据扫描的结果更新project的状态
     * @author WZY
     */
    @Override
    @KafkaListener(id="scanResult",topics = {"ScanResult"},groupId = "scanResult")
    public void updateCommitScanStatus(ConsumerRecord<String, String> consumerRecord) {
        String msg=consumerRecord.value();
        logger.info("received message from topic -> "+consumerRecord.topic()+" : "+msg);
        ScanResult scanResult=JSONObject.parseObject(msg,ScanResult.class);
        String projectId=scanResult.getProjectId();
        JSONObject projectParam=new JSONObject();
        projectParam.put("uuid",projectId);
        if(scanResult.getStatus().equals("success")){
            projectParam.put("scan_status","Scanned");
        }else{
            projectParam.put("scan_status",scanResult.getDescription());
        }
        updateProject(projectParam);
    }
}
