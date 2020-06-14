package cn.edu.fudan.cloneservice.thread;


import cn.edu.fudan.cloneservice.scan.task.ScanTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;

/**
 * @author zyh
 * @date 2020/5/5
 */
public class ScanSynchronouslyTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ScanSynchronouslyTask.class);

    private String commitId;
    private String repoId;
    private BlockingQueue<String> measureQueue;

    private ScanTask scanTask;

    public ScanSynchronouslyTask(String commitId, String repoId, BlockingQueue<String> measureQueue){

        this.commitId = commitId;
        this.repoId = repoId;
        this.measureQueue = measureQueue;

        scanTask = new ScanTask();
    }


    @Override
    public void run() {
        logger.info("{}-> start scan", Thread.currentThread().getName());
        scanTask.runSynchronously(repoId, commitId, "snippet");
        logger.info("{}-> scan complete,measureQueue size = {}", Thread.currentThread().getName(), measureQueue.size());
        //判断repoId，commitId对应的scanResult是否成功，若成功，将repoId和commitId对应的measure任务加入度量阻塞队列
        measureQueue.offer(commitId);
    }
}
