package cn.edu.fudan.scanservice.task;

import cn.edu.fudan.scanservice.domain.ScanInitialInfo;
import cn.edu.fudan.scanservice.domain.ScanResult;
import cn.edu.fudan.scanservice.service.ScanOperation;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.Future;

/**
 * 异步的scan任务
 * @author WZY
 * @version 1.0
 **/
@Component
public class ScanTask {

    private Logger logger= LoggerFactory.getLogger(ScanTask.class);

    //这边注入的是findBug的扫描的实现方式，如果是其它工具，可以换作其它实现
    @Resource(name="findBug")
    private ScanOperation scanOperation;

    private KafkaTemplate kafkaTemplate;

    @Autowired
    public void setKafkaTemplate(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
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
        if(!scanOperation.mapping(projectId,commitId)){
            send(projectId,commitId,"failed","Mapping failed");
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

    @Async
    public Future<String> run(String projectId,String commitId){
        scan(projectId,commitId);
        return new AsyncResult<>("complete");
    }
}
