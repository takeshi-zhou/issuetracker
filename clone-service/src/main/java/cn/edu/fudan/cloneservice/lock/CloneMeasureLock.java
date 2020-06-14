package cn.edu.fudan.cloneservice.lock;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author zyh
 * @date 2020/6/3
 * 自定义同步组件，根据基础服务的特点，同时允许多个线程访问repo资源资源
 * 锁的获取与释放都是通过队列同步器的状态（state）来判断
 */
@Component
public class CloneMeasureLock implements Lock {

    private Sync sync;

    public CloneMeasureLock(int cloneMeasureLockSize){
        sync = new Sync(cloneMeasureLockSize);
    }

    @Override
    public void lock() {
        sync.acquireShared(1);
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
        sync.releaseShared(1);
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    /**
     * 自定义队列同步器
     */
    private static final class Sync extends AbstractQueuedSynchronizer {

        private Sync(int count){
            if(count <= 0){
                throw new IllegalArgumentException("count must large than 0.");
            }
            setState(count);
        }

        /**
         * 共享式的获取锁，被acquireShared所调用
         *
         * if没有满足条件说明：当前线程获取state时，锁满足共享条件(newCount > 0)
         * 但其他线程在这期间拿到了锁 (compareAndSetState(current, newCount) = false)，此时没有返回值且自旋的尝试再次获取锁
         *
         * if满足条件说明：当前线程获取state时，锁满不足共享条件(newCount < 0)
         * acquireShared就会自旋的调用tryAcquireShared，直到拿到锁或被中断
         *
         * @param reduceCount 每个线程代表的数量，通常为 1
         * @return 大于 0，表示成功获取到了锁
         */
        @Override
        public int tryAcquireShared(int reduceCount){
            while (true){
                int current = getState();
                int newCount = current - reduceCount;
                if(newCount < 0 || compareAndSetState(current, newCount)){
                    return newCount;
                }
            }
        }

        /**
         * 共享式的释放锁，releaseShared
         *
         * @param returnCount 每个线程代表的数量，通常为 1
         * @return true表示成功释放锁
         */
        @Override
        public boolean tryReleaseShared(int returnCount){
            while (true){
                int current = getState();
                int newCount = current + returnCount;
                if(compareAndSetState(current, newCount)){
                    return true;
                }

            }
        }


    }
}
