package cn.edu.fudan.scanservice.task;

import cn.edu.fudan.scanservice.domain.ScanInitialInfo;
import cn.edu.fudan.scanservice.domain.ScanResult;
import cn.edu.fudan.scanservice.lock.RedisLock;
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
import java.util.concurrent.TimeUnit;

/**
 * @author WZY
 * @version 1.0
 **/
@Component
public class CloneScanTask {
    private Logger logger = LoggerFactory.getLogger(CloneScanTask.class);

    //这边注入的是GPUClone的扫描的实现方式，如果是其它工具，可以换作其它实现
    @Resource(name = "GPUClone")
    private ScanOperation scanOperation;

    private KafkaTemplate kafkaTemplate;

    @Autowired
    public void setKafkaTemplate(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @SuppressWarnings("unchecked")
    private void send(String repoId, String commitId,String status, String description) {
        ScanResult scanResult = new ScanResult(repoId, commitId,"clone" ,status, description);
        kafkaTemplate.send("ScanResult", JSONObject.toJSONString(scanResult));
    }

    private void scan(String repoId, String commitId) {
        logger.info("start to checkout -> " + commitId);
        //checkout,如果失败发送错误消息，直接返回
        if (!scanOperation.checkOut(repoId, commitId)) {
            send(repoId, commitId, "failed", "check out failed");
            logger.error("Check Out Failed!");
            return;
        }
        logger.info("checkout complete -> start the scan operation......");
        ScanInitialInfo scanInitialInfo = scanOperation.initialScan(repoId, commitId);
        scanOperation.doScan(scanInitialInfo);
    }

    @Async
    public void run(String repoId, String commitId) {
        scan(repoId, commitId);
    }

}
