package cn.edu.fudan.cloneservice.thread;

import cn.edu.fudan.cloneservice.task.ScanTask;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.BlockingQueue;

/**
 * @author zyh
 * @date 2020/5/5
 */
public class ScanSynchronouslyTask implements Runnable {

    private String commitId;
    private String repoId;
    private BlockingQueue<String> measureQueue;

    @Autowired
    private ScanTask scanTask;

    public ScanSynchronouslyTask(String commitId, String repoId, BlockingQueue<String> measureQueue){

        this.commitId = commitId;
        this.repoId = repoId;
        this.measureQueue = measureQueue;
    }

    @Override
    public void run() {
        scanTask.runSynchronously(repoId, commitId, "clone");

        //判断repoId，commitId对应的scanResult是否成功，若成功，将repoId和commitId对应的measure任务加入度量阻塞队列
        measureQueue.offer(commitId);
    }
}
