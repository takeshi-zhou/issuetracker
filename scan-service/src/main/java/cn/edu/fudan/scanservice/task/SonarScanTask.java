package cn.edu.fudan.scanservice.task;

import cn.edu.fudan.scanservice.service.ScanOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
@Component
public class SonarScanTask extends BaseScanTask {

    private Logger logger = LoggerFactory.getLogger(SonarScanTask.class);

    //这边注入的是findBug的扫描的实现方式，如果是其它工具，可以换作其它实现
    @Resource(name = "sonar")
    private ScanOperation scanOperation;



    private void run(String repoId, String commitId,String category) throws RuntimeException{
        //获取分布式锁，一个repo同一时间只能有一个线程在扫
        //15min恰好是一个整个Scan操作的超时时间，如果某个线程获得锁之后Scan过程卡死导致锁没有释放
        //如果那个锁成功设置了过期时间，那么key过期后，其他线程自然可以获取到锁
        //如果那个锁并没有成功地设置过期时间
        //那么等待获取同一个锁的线程会因为10min的超时而强行获取到锁，并设置自己的identifier和key的过期时间
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
        run(repoId,commitId,category);
        return new AsyncResult<>("complete");
    }


    public void runSynchronously(String repoId,String commitId,String category) throws  RuntimeException{
        run(repoId,commitId,category);
    }
}
