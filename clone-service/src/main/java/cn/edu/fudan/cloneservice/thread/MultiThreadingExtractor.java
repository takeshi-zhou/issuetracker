package cn.edu.fudan.cloneservice.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.*;

/**
 * @author zyh
 * @date 2020/5/5
 */
@Component
public class MultiThreadingExtractor {

    private static final Logger logger = LoggerFactory.getLogger(MultiThreadingExtractor.class);

    /**
     * clone扫描阶段线程池
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

    private int corePoolSize;

    public MultiThreadingExtractor(){

        corePoolSize = 10;

        scanQueue = new LinkedTransferQueue<>();

        measureQueue = new LinkedTransferQueue<>();

        CloneThreadFactory cloneScanThreadFactory = new CloneThreadFactory("cloneScan");

        CloneThreadFactory cloneMeasureThreadFactory = new CloneThreadFactory("cloneMeasure");

        scanThreadPool = new ThreadPoolExecutor(corePoolSize, corePoolSize, 10L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(200), cloneScanThreadFactory);

        measureThreadPool = new ThreadPoolExecutor(corePoolSize, corePoolSize, 10L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(200), cloneMeasureThreadFactory);

    }

    @Async("forRequest")
    public void extract(String repoId, List<String> commitIds){

        logger.info("start clone scan........");

        long start = System.currentTimeMillis();

        //可以建立线程池来管理
        Thread addCommits = new Thread(()->
                extractCommitIds(commitIds)
                ,"addCommits");

        Thread scan = new Thread(()->
                scanSynchronously(repoId, measureQueue)
                ,"addCommits");

        Thread measure = new Thread(()->
                measureSynchronously(repoId)
                ,"addCommits");

        addCommits.start();
        scan.start();
        measure.start();

        try {
            addCommits.join();
            scan.join();
            measure.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();

        long cost = (end - start)/1000;

        logger.info("repo:{} -> took {} minutes to complete the clone scan and measure scan", repoId, cost);

    }

    private void extractCommitIds(List<String> commitIds){

        if(commitIds == null){
            return;
        }

        for(String commitId : commitIds){
            scanQueue.offer(commitId);
        }

    }

    /**
     * commit列表的消费者，同时也是measure列表的生产者
     */
    private void scanSynchronously(String repoId, BlockingQueue<String> measureQueue){

        while (true){

            try {
                //一分钟内取不出就退出
                String commitId = scanQueue.poll(60, TimeUnit.MINUTES);
                if(commitId == null){
                    break;
                }
                scanThreadPool.submit(new ScanSynchronouslyTask(commitId, repoId, measureQueue));

                //measureQueue.offer(commitId);
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
        while (true){

            try {
                //10分钟内取不出就退出
                String commitId = measureQueue.poll(600, TimeUnit.MINUTES);
                if(commitId == null){
                    break;
                }
                measureThreadPool.submit(new MeasureTask(commitId, repoId));

            } catch (InterruptedException e) {
                logger.info("");
                e.printStackTrace();
                break;
            }


        }
    }


}
