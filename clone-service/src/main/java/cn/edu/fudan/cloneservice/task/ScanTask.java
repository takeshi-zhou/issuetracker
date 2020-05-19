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

    private RedisLuaLock redisLock;

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
        //没有共享资源，不需要锁
        if (scanOperation.isScanned(repoId,commitId,category)) {

            logger.info("this commit has been scanned");
            send(repoId, commitId, category,"success", "scan success!");
            logger.info("Scan Success!");
            return;
        }
        logger.info("this commit ---> {} has not been scanned,start the scan initialization......",commitId);

        String identifier= UUID.randomUUID().toString();
        Boolean lockResult = false;

        ScanResult scanResult;
        ScanInitialInfo scanInitialInfo = new ScanInitialInfo();

        //redis锁
        while(!lockResult){
            lockResult =  redisLock.tryLock(repoId, identifier, 600, 600);
        }

        logger.info("redis lock identifier id --> {}",identifier);
        try {
            //锁资源
            scanInitialInfo = scanOperation.initialScan(repoId, commitId,category);
            if(!scanInitialInfo.isSuccess()){
                send(repoId, commitId, category,"failed", "initial failed");
                logger.error("Initial Failed!");
                return;
            }
            scanResult = scanOperation.doScan(scanInitialInfo);
        } finally {
            //释放资源
            restInterfaceManager.freeRepoPath(scanInitialInfo.getRepoId(),scanInitialInfo.getRepoPath());
            //释放redis锁
            if (!redisLock.releaseLockNew(repoId, identifier)) {
                logger.error("repo->" + repoId + " release lock failed!");
            }

        }

        if ("failed".equals(scanResult.getStatus())) {
            send(repoId, commitId, category,"failed", scanResult.getDescription());
            scanOperation.updateScan(scanInitialInfo);
            logger.error(scanResult.getDescription());
            return;
        }

        logger.info("scan complete ->" + scanResult.getDescription());
        logger.info("start to update scan status");
        scanInitialInfo.getScan().setStatus("done");
        if (!scanOperation.updateScan(scanInitialInfo)) {
            send(repoId, commitId, category,"failed", "scan update failed");
            logger.error("Scan Update Failed!");
            return;
        }
        logger.info("scan update complete");
        send(repoId, commitId, category,"success", "all complete");

    }


    private void run(String repoId, String commitId, String category) throws RuntimeException{

        scan(scanOperation,repoId, commitId,category);
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
