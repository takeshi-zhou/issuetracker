package cn.edu.fudan.cloneservice.thread;

import cn.edu.fudan.cloneservice.component.RestInterfaceManager;
import cn.edu.fudan.cloneservice.scan.task.ScanTask;
import cn.edu.fudan.cloneservice.service.CloneMeasureService;
import cn.edu.fudan.cloneservice.util.JGitUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author zyh
 * @date 2020/5/5
 * 多生产者多消费者模式
 */
@Component
public class MultiThreadingExtractor {

    private static final Logger logger = LoggerFactory.getLogger(MultiThreadingExtractor.class);

    private RestInterfaceManager restInterfaceManager;

    @Autowired
    public void setRestInterfaceManager(RestInterfaceManager restInterfaceManager) {
        this.restInterfaceManager = restInterfaceManager;
    }

    @Autowired
    private ScanTask scanTask;

    @Autowired
    private CloneMeasureService cloneMeasureService;

    /**
     * clone扫描阶段线程池
     * 之后对线程池的选择做优化
     */
    private ThreadPoolExecutor scanThreadPool;

    /**
     * clone度量阶段线程池
     */
    private ThreadPoolExecutor measureThreadPool;

    /**
     * 扫描阻塞队列
     */
    private BlockingQueue<String> scanQueue;

    /**
     * 度量阻塞队列
     */
    private BlockingQueue<String> measureQueue;

    public MultiThreadingExtractor(int corePoolSize){

        scanQueue = new LinkedTransferQueue<>();

        measureQueue = new LinkedTransferQueue<>();

        CloneThreadFactory cloneScanThreadFactory = new CloneThreadFactory("cloneScan");

        CloneThreadFactory cloneMeasureThreadFactory = new CloneThreadFactory("cloneMeasure");

        scanThreadPool = new ThreadPoolExecutor(corePoolSize, corePoolSize, 10L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(2000), cloneScanThreadFactory);

        measureThreadPool = new ThreadPoolExecutor(corePoolSize, corePoolSize, 10L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(2000), cloneMeasureThreadFactory);

    }

    public void extract(String repoId, String startCommitId){

        logger.info("start clone scan........");

        long start = System.currentTimeMillis();

        extractCommitIds(repoId, startCommitId);
        //可以建立线程池来管理
        Thread scan = new Thread(()->
                scanSynchronously(repoId)
                ,"cloneScan");

        Thread measure = new Thread(()->
                measureSynchronously(repoId)
                ,"cloneMeasure");

        scan.start();
        measure.start();

        try {
            scan.join();
            measure.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();

        long cost = (end - start)/(1000*60);

        logger.info("repo:{} -> took {} minutes to complete the clone scan and measure scan", repoId, cost);

    }

    private void extractCommitIds(String repoId, String startCommitId) {

        String repoPath = null;
        List<String> commitList = new ArrayList<>();
        try {
            repoPath = restInterfaceManager.getRepoPath1(repoId);
            JGitUtil jGitHelper = new JGitUtil(repoPath);
            commitList = jGitHelper.getCommitListByBranchAndBeginCommit(startCommitId);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (repoPath != null) {
                restInterfaceManager.freeRepoPath(repoId, repoPath);
            }
        }
        //先执行粒度为method，仅需执行一次最近的commit
        String latestCommitId = commitList.get(commitList.size() - 1);
        scanTask.runSynchronously(repoId,latestCommitId, "method");
        for (String commitId : commitList) {
            scanQueue.offer(commitId);
        }

    }

    /**
     * commit列表的消费者，同时也是measure列表的生产者
     */
    private void scanSynchronously(String repoId){

        logger.info("start clone scan");
        while (true){

            try {
                //一分钟内取不出就退出
                String commitId = scanQueue.poll(1, TimeUnit.MINUTES);
                if(commitId == null){
                    break;
                }
                scanThreadPool.submit(()-> {
                    logger.info("{}-> start scan", Thread.currentThread().getName());
                    scanTask.runSynchronously(repoId,commitId, "snippet");
                    measureQueue.offer(commitId);
                });

            } catch (InterruptedException e) {
                logger.info("clone Multithreaded scan filed");
                e.printStackTrace();
                break;
            }
        }
    }

    /**
     * measure列表的消费者
     */
    private void measureSynchronously(String repoId){

        logger.info("start clone measure");
        while (true){

            try {
                //10分钟内取不出就退出
                String commitId = measureQueue.poll(10, TimeUnit.MINUTES);
                if(commitId == null){
                    break;
                }
                measureThreadPool.submit(()->{
                    logger.info("{}-> start measure", Thread.currentThread().getName());
                    cloneMeasureService.insertCloneMeasure(repoId, commitId);
                });

            } catch (InterruptedException e) {
                logger.info("clone Multithreaded measure filed");
                e.printStackTrace();
                break;
            }

        }
    }

}
