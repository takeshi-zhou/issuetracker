package cn.edu.fudan.cloneservice.task;

import cn.edu.fudan.cloneservice.component.RestInterfaceManager;
import cn.edu.fudan.cloneservice.domain.ScanInitialInfo;
import cn.edu.fudan.cloneservice.domain.ScanResult;
import cn.edu.fudan.cloneservice.lock.RedisLuaLock;
import cn.edu.fudan.cloneservice.tools.ScanOperation;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.Future;

/**
 * @author zyh
 * @date 2020/4/15
 */

@Component
public class ScanTask {

    private Logger logger = LoggerFactory.getLogger(ScanTask.class);

    RedisLuaLock redisLock;

    @Autowired
    public void setRedisLock(RedisLuaLock redisLock) {
        this.redisLock = redisLock;
    }

    private KafkaTemplate kafkaTemplate;

    @Resource(name = "CPUClone")
    private ScanOperation scanOperation;

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

    private void scan(ScanOperation scanOperation, String repoId, String commitId, String category) throws RuntimeException{
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
//
//        logger.info("start to mapping ......");
//        if (!scanOperation.mapping(repoId, commitId,category)) {
//            send(repoId, commitId, category,"failed", "Mapping failed");
//            scanInitialInfo.getScan().setStatus("Mapping Failed!");
//            scanOperation.updateScan(scanInitialInfo);
//            logger.error("Mapping Failed!");
//            restInterfaceManager.freeRepoPath(scanInitialInfo.getRepoId(),scanInitialInfo.getRepoPath());
//            return;
//        }
//        logger.info("mapping complete");

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


    private void run(String repoId, String commitId, String category) throws RuntimeException{
        String identifier= UUID.randomUUID().toString();
        Boolean lockResult = false;
        while(!lockResult){
            lockResult =  redisLock.tryLock(repoId, identifier, 600, 600);
        }

        logger.info("redis lock identifier id --> {}",identifier);
        try {
            scan(scanOperation,repoId, commitId,category);
        } finally {
            if (!redisLock.releaseLockNew(repoId, identifier)) {
                logger.error("repo->" + repoId + " release lock failed!");
            }
        }

    }

    @Async("forRequest")
    public Future<String> runAsync(String repoId, String commitId, String category){
        run(repoId, commitId, category);
        return new AsyncResult<>("complete");
    }

    public void runSynchronously(String repoId,String commitId,String category) throws RuntimeException{
        run(repoId, commitId, category);
    }
}
