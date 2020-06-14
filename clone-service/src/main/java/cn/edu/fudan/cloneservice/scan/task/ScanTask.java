package cn.edu.fudan.cloneservice.scan.task;

import cn.edu.fudan.cloneservice.component.RestInterfaceManager;
import cn.edu.fudan.cloneservice.lock.RedisLuaLock;
import cn.edu.fudan.cloneservice.scan.domain.CloneScanInitialInfo;
import cn.edu.fudan.cloneservice.scan.domain.CloneScanResult;
import cn.edu.fudan.cloneservice.scan.lock.CloneScanLock;
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
import java.util.concurrent.TimeUnit;

/**
 * @author zyh
 * @date 2020/5/25
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
    private CloneScanLock cloneScanLock;

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
    private void send(String repoId, String commitId,String type,String status, String description) {
//        CloneScanResult scanResult = new CloneScanResult(repoId, commitId, type,status, description);
//        kafkaTemplate.send("ScanResult", JSONObject.toJSONString(scanResult));
    }

    private void scan(ScanOperation scanOperation, String repoId, String commitId, String type) throws RuntimeException{
        //没有共享资源，不需要锁
        //判断当前repoId, commitId, type是否扫描过
        if (scanOperation.isScanned(repoId,commitId,type)) {
            logger.info("{} -> this commit has been scanned", Thread.currentThread().getName());
            send(repoId, commitId, type,"success", "scan success!");
            return;
        }

        logger.info("{} -> this commit ---> {} has not been scanned,start the scan initialization......", Thread.currentThread().getName(), commitId);

//        String identifier= UUID.randomUUID().toString();
//        Boolean lockResult = false;

        CloneScanResult cloneScanResult;

        CloneScanInitialInfo cloneScanInitialInfo = new CloneScanInitialInfo();

        //redis锁
//        while(!lockResult){
//            lockResult =  redisLock.tryLock(repoId, identifier, 600, 600);
//        }

        cloneScanLock.lock();
        try {
            cloneScanInitialInfo = scanOperation.initialScan(repoId, commitId, type);
            if(!cloneScanInitialInfo.isSuccess()){
                send(repoId, commitId, type,"failed", "initial failed");
                logger.error("{} -> Initial Failed!", Thread.currentThread().getName());
                return;
            }
            cloneScanResult = scanOperation.doScan(cloneScanInitialInfo);
        } finally {
            //释放资源
            restInterfaceManager.freeRepoPath(cloneScanInitialInfo.getRepoId(),cloneScanInitialInfo.getRepoPath());
            //释放redis锁
//            if (!redisLock.releaseLockNew(repoId, identifier)) {
//                logger.error("repo->" + repoId + " release lock failed!");
//            }
            cloneScanLock.unlock();
        }

        if ("failed".equals(cloneScanResult.getStatus())) {
            send(repoId, commitId, type,"failed", cloneScanResult.getDescription());
            scanOperation.updateScan(cloneScanInitialInfo);
            logger.error(cloneScanResult.getDescription());
            return;
        }

        logger.info("{} -> scan complete ->" + cloneScanResult.getDescription(), Thread.currentThread().getName());
        logger.info("{} -> start to update scan status", Thread.currentThread().getName());
        cloneScanInitialInfo.getCloneScan().setStatus("done");
        if (!scanOperation.updateScan(cloneScanInitialInfo)) {
            send(repoId, commitId, type,"failed", "scan update failed");
            logger.error("{} -> Scan Update Failed!", Thread.currentThread().getName());
            return;
        }
        logger.info("{} -> scan update complete", Thread.currentThread().getName());
        send(repoId, commitId, type,"success", "all complete");

    }

    private void run(String repoId, String commitId, String category) throws RuntimeException{

        scan(scanOperation,repoId, commitId,category);
    }

    @Async("forRequest")
    public Future<String> runAsync(String repoId, String commitId, String type){
        run(repoId, commitId, type);
        return new AsyncResult<>("complete");
    }

    public void runSynchronously(String repoId,String commitId,String type) throws RuntimeException{
        run(repoId, commitId, type);
    }

}
