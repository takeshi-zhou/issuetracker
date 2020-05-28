package cn.edu.fudan.cloneservice.thread;

/**
 * @author zyh
 * @date 2020/5/4
 */
public interface CloneMsgQueue {

    /**
     * 向总任务队列中添加任务
     * @param commitId commit-id
     */
    void put(String commitId);

    /**
     * 从任务队列中拿数据
     * @return commit-id
     */
    String take();

}
