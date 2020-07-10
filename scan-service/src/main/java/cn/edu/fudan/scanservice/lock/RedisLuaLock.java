package cn.edu.fudan.scanservice.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author WZY
 * @version 1.0
 **/
@Component
public class RedisLuaLock {

    @Value("${redis.lock.expireNx}")
    private  int expiredNx;

    private final JedisPool jedisPool;

    private final DefaultRedisScript<Boolean> acquireScript;

    private final DefaultRedisScript<Boolean> releaseScript;

    private static final Long lockReleaseOK = 1L;
    static String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";// lua脚本，用来释放分布式锁


    //最好用StringRedisTemplate来执行Lua脚本
    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public RedisLuaLock(DefaultRedisScript<Boolean> acquireScript,
                        DefaultRedisScript<Boolean> releaseScript,
                        StringRedisTemplate stringRedisTemplate,
                        JedisPool jedisPool) {
        this.acquireScript=acquireScript;
        this.releaseScript=releaseScript;
        this.stringRedisTemplate=stringRedisTemplate;
        this.jedisPool=jedisPool;
    }

    public String acquireLockWithTimeOut(String lockName, long acquireTimeOut, long lockTimeOut, TimeUnit timeUnit){
        String identifier= UUID.randomUUID().toString();
        lockName="lock:"+lockName;
        int lockExpire = (int)timeUnit.toMillis(lockTimeOut);
        long end=System.currentTimeMillis()+timeUnit.toMillis(acquireTimeOut);
        while(System.currentTimeMillis()<end){
            Boolean acquired= stringRedisTemplate.execute(acquireScript, Collections.singletonList(lockName),String.valueOf(lockExpire),identifier);
            if(acquired!=null&&acquired) {
                return identifier;
            }
            try {
                Thread.sleep(10);
            }catch(InterruptedException ie){
                Thread.currentThread().interrupt();
            }
        }
        return null;
    }

    public boolean releaseLock(String lockName,String identifier){
        lockName="lock:"+lockName;
        Boolean success=stringRedisTemplate.execute(releaseScript,Collections.singletonList(lockName),identifier);
        return success!=null&&success;
    }


    public  boolean lock(String key,String lockValue,int expire){
        if(null == key){
            return false;
        }
        try {
            Jedis jedis = jedisPool.getResource();
            String res = jedis.set(key,lockValue,"NX","EX",expire);
            jedis.close();
            return res!=null && res.equals("OK");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取一个分布式锁 , 超时则返回失败
     * @param key           锁的key
     * @param lockValue     锁的value
     * @param timeout       获取锁的等待时间，单位为 秒
     * @return              获锁成功 - true | 获锁失败 - false
     */
    public boolean tryLock(String key,String lockValue,int timeout,int expire){

        final long start = System.currentTimeMillis();
        if(timeout > expiredNx) {
            timeout = expiredNx;
        }
        final long end = start + timeout * 1000;
        boolean res = false; // 默认返回失败
        while(!(res = lock(key,lockValue,expire))){
            try {
                Thread.sleep(100);
            }catch(InterruptedException ie){
                Thread.currentThread().interrupt();
            }
            // 调用了上面的 lock方法
            if(System.currentTimeMillis() > end) {
                break;
            }
        }
        return res;
    }


    public  boolean releaseLockNew(String key ,String lockValue){
        if(key == null || lockValue == null) {
            return false;
        }
        try {
            Jedis jedis = jedisPool.getResource();
            Object res =jedis.eval(luaScript,Collections.singletonList(key),Collections.singletonList(lockValue));
            jedis.close();
            return res!=null && res.equals(lockReleaseOK);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
