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

    private RedisLock redisLock;

    @Autowired
    public void setRedisLock(RedisLock redisLock) {
        this.redisLock = redisLock;
    }

    private KafkaTemplate kafkaTemplate;

    @Autowired
    public void setKafkaTemplate(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @SuppressWarnings("unchecked")
    private void send(String repoId, String commitId, String type, String status, String description) {
        ScanResult scanResult = new ScanResult(repoId, commitId,type ,status, description);
        kafkaTemplate.send("ScanResult", JSONObject.toJSONString(scanResult));
    }

    private void scan(String repoId, String commitId) {
        logger.info("start to checkout -> " + commitId);
        //checkout,如果失败发送错误消息，直接返回
        if (!scanOperation.checkOut(repoId, commitId)) {
            send(repoId, commitId, "clone","failed", "check out failed");
            logger.error("Check Out Failed!");
            return;
        }
        logger.info("checkout complete -> start the scan operation......");
        ScanInitialInfo scanInitialInfo = scanOperation.initialScan(repoId, commitId);
        scanOperation.doScan(scanInitialInfo);
    }

    @Async
    public Future<String> run(String repoId, String commitId) {
        //获取分布式锁，一个repo同一时间只能有一个线程在扫
        //15min恰好是一个整个Scan操作的超时时间，如果某个线程获得锁之后Scan过程卡死导致锁没有释放
        //如果那个锁成功设置了过期时间，那么key过期后，其他线程自然可以获取到锁
        //如果那个锁并没有成功地设置过期时间
        //那么等待获取同一个锁的线程会因为15min的超时而强行获取到锁，并设置自己的identifier和key的过期时间
        String identifier = redisLock.acquireLock(repoId, 15, 15, TimeUnit.SECONDS);
        try {
            scan(repoId, commitId);
        } finally {
            if (redisLock.releaseLock(repoId, identifier)) {
                logger.error("repo->" + repoId + " release lock failed!");
            }
        }
        return new AsyncResult<>("complete");
    }

}
