package cn.edu.fudan.cloneservice.thread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author zyh
 * @date 2020/5/29
 *
 * 自定义锁，可调整访问共享资源的线程数
 */
public class CloneMeasureLock implements Lock {

    /**
     * 访问共享资源的线程数
     */
    private int threadNum;

    public CloneMeasureLock(int threadNum){
        this.threadNum = threadNum;
    }

    @Override
    public void lock() {

    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {

    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
