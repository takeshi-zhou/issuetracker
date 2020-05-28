package cn.edu.fudan.cloneservice.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

/**
 * @author zyh
 * @date 2020/5/4
 */
public class CloneMsgQueueManager implements CloneMsgQueue {

    private static final Logger logger = LoggerFactory.getLogger(CloneMsgQueueManager.class);

    /**
     * 任务总队列，即需要处理的commitId队列
     */
    public final BlockingQueue<String> cloneMsgQueue;

    private CloneMsgQueueManager(){
        cloneMsgQueue = new LinkedTransferQueue<>();
    }

    @Override
    public void put(String commitId) {
        try{
            cloneMsgQueue.put(commitId);
            logger.info("commit : {} -> put success",commitId);
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public String take() {
        try{
            String commitId = cloneMsgQueue.take();
            logger.info("commit : {} -> take success",commitId);
            return commitId;
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }

        return null;
    }
}
