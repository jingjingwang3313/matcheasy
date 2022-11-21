package cn.matcheasy.framework.utils;

import cn.matcheasy.framework.config.redisson.DistributeLocker;

import java.util.concurrent.TimeUnit;

/**
 * @class RedissonLockUtil
 * @author: wangjing
 * @date: 2020/11/7/0007
 * @desc: 分布式锁工具类
 */
public class RedissonLockUtil {

    /**
     * 避免工具类实例化
     */
    private RedissonLockUtil() {
    }

    private static DistributeLocker locker;

    public static void setLocker(DistributeLocker locker) {
        RedissonLockUtil.locker = locker;
    }

    public static void lock(String lockKey) {
        locker.lock(lockKey);
    }

    public static void unlock(String lockKey) {
        locker.unlock(lockKey);
    }

    public static void lock(String lockKey, int timeout) {
        locker.lock(lockKey, timeout);
    }

    public static void lock(String lockKey, int timeout, TimeUnit unit) {
        locker.lock(lockKey, timeout, unit);
    }

    public static boolean tryLock(String lockKey) {
        return locker.tryLock(lockKey);
    }

    public static boolean tryLock(String lockKey, long waitTime, long leaseTime,
                                  TimeUnit unit) throws InterruptedException {
        return locker.tryLock(lockKey, waitTime, leaseTime, unit);
    }

    public static boolean isLocked(String lockKey) {
        return locker.isLocked(lockKey);
    }

    public static boolean isHeldByCurrentThread(String lockKey) {
        return locker.isHeldByCurrentThread(lockKey);
    }

}
