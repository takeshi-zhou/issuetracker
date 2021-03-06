package cn.edu.fudan.scanservice.task;

import cn.edu.fudan.scanservice.component.rest.RestInterfaceManager;
import cn.edu.fudan.scanservice.domain.ScanInitialInfo;
import cn.edu.fudan.scanservice.domain.ScanResult;
import cn.edu.fudan.scanservice.lock.RedisLuaLock;
import cn.edu.fudan.scanservice.service.ScanOperation;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author WZY
 * @version 1.0
 **/
@Component
public class BaseScanTask {

    private Logger logger = LoggerFactory.getLogger(BaseScanTask.class);

    RedisLuaLock redisLock;

    @Autowired
    public void setRedisLock(RedisLuaLock redisLock) {
        this.redisLock = redisLock;
    }

    private KafkaTemplate kafkaTemplate;

    @Autowired
    public void setKafkaTemplate(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private RestInterfaceManager restInterfaceManager;

    @Autowired
    public void setRestInterfaceManager(RestInterfaceManager restInterfaceManager) {
        this.restInterfaceManager = restInterfaceManager;
    }

    @SuppressWarnings("unchecked")
    private void send(String repoId, String commitId,String category,String status, String description) {
        ScanResult scanResult = new ScanResult(repoId, commitId, category,status, description);
        kafkaTemplate.send("ScanResult", JSONObject.toJSONString(scanResult));
    }

    void scan(ScanOperation scanOperation, String repoId, String commitId, String category) throws RuntimeException{
        if (scanOperation.isScanned(repoId,commitId,category)) {
            //如果当前commit已经扫描过，直接结束
            logger.info("this commit has been scanned");
            send(repoId, commitId, category,"success", "scan success!");
            logger.info("Scan Success!");
            return;
        }
        logger.info("this commit ---> {} has not been scanned,start to check commit time",commitId);
        if(!scanOperation.checkCommit(repoId, commitId, category)){
            send(repoId, commitId, category,"failed", "commit too old");
            logger.error("Current commit time is before last scanned commit!");
            return;
        }
        logger.info("this commit is valid -> start the scan initialization......");
        ScanInitialInfo scanInitialInfo = scanOperation.initialScan(repoId, commitId,category);
        if(!scanInitialInfo.isSuccess()){
            send(repoId, commitId, category,"failed", "initial failed");
            logger.error("Initial Failed!");
            restInterfaceManager.freeRepoPath(scanInitialInfo.getRepoId(),scanInitialInfo.getRepoPath());
            return;
        }
        ScanResult scanResult = scanOperation.doScan(scanInitialInfo);
        if (scanResult.getStatus().equals("failed")) {
            send(repoId, commitId, category,"failed", scanResult.getDescription());
            scanOperation.updateScan(scanInitialInfo);
            logger.error(scanResult.getDescription());
            restInterfaceManager.freeRepoPath(scanInitialInfo.getRepoId(),scanInitialInfo.getRepoPath());
            return;
        }

        logger.info("scan complete ->" + scanResult.getDescription());

        logger.info("start to mapping ......");
            if (!scanOperation.mapping(repoId, commitId,category)) {
                send(repoId, commitId, category,"failed", "Mapping failed");
                scanInitialInfo.getScan().setStatus("Mapping Failed!");
                scanOperation.updateScan(scanInitialInfo);
                logger.error("Mapping Failed!");
                restInterfaceManager.freeRepoPath(scanInitialInfo.getRepoId(),scanInitialInfo.getRepoPath());
                return;
            }
        logger.info("mapping complete");

        //映射结束，更新当前scan
        logger.info("start to update scan status");
        scanInitialInfo.getScan().setStatus("done");
        if (!scanOperation.updateScan(scanInitialInfo)) {
            send(repoId, commitId, category,"failed", "scan update failed");
            logger.error("Scan Update Failed!");
            restInterfaceManager.freeRepoPath(scanInitialInfo.getRepoId(),scanInitialInfo.getRepoPath());
            return;
        }
        logger.info("scan update complete");
        send(repoId, commitId, category,"success", "all complete");
        restInterfaceManager.freeRepoPath(scanInitialInfo.getRepoId(),scanInitialInfo.getRepoPath());
    }



}
