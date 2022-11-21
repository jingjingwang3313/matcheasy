package cn.matcheasy.framework.aspectj;

import cn.hutool.core.util.StrUtil;
import cn.matcheasy.framework.annotation.RedissonLock;
import cn.matcheasy.framework.log.MatcheasyLog;
import cn.matcheasy.framework.utils.RedissonLockUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @class RedissonLockAspect
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: 分布式锁AOP
 */
@Slf4j
@Aspect
@Component
@Order(3)
public class RedissonLockAspect
{
    // 可使用注入bean进行分布式锁操作
    @Autowired
    private RedissonClient redissonClient;

    /**
     * 切点
     */
    @Pointcut("@annotation(cn.matcheasy.framework.annotation.RedissonLock)")
    public void redissonLockPoint()
    {
    }

    @Around("redissonLockPoint()")
    @ResponseBody
    public Object checkLock(ProceedingJoinPoint pjp) throws Throwable
    {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] objs = pjp.getArgs();
        //检查有没有RedissonLock的注解
        if (method.isAnnotationPresent(RedissonLock.class))
        {
            //当前线程
            String threadName = Thread.currentThread().getName();
            MatcheasyLog.info(StrUtil.format("线程 {} 进入分布式锁aop", threadName));
            //获取该注解的实例对象
            RedissonLock annotation = method.getAnnotation(RedissonLock.class);
            //生成分布式锁lockRedisKey
            String lockRedisKey = annotation.lockRedisKey();

            if (StringUtils.isBlank(lockRedisKey))
            {
                MatcheasyLog.info(StrUtil.format("线程 {} lockRedisKey 设置为空,不加锁", threadName));
                return pjp.proceed(objs);
            }
            else
            {
                MatcheasyLog.info(StrUtil.format("线程: {} ,锁: {} ", threadName, lockRedisKey));
                //获取锁   waitTime:等待获取锁的时间  leaseTime:获取锁后持有时间   时间单位:MILLISECONDS：毫秒,SECONDS：秒
                if (RedissonLockUtil.tryLock(lockRedisKey, 3, 30, TimeUnit.SECONDS))
                {
                    try
                    {
                        MatcheasyLog.info(StrUtil.format("线程 {} 获取锁 {} 成功", threadName, lockRedisKey));
                        return pjp.proceed(objs);
                    }
                    catch (Exception e)
                    {
                        MatcheasyLog.info(StrUtil.format(e.getMessage(), e));
                        throw e;
                    }
                    finally
                    {
                        if (RedissonLockUtil.isLocked(lockRedisKey))
                        {
                            MatcheasyLog.info(StrUtil.format("锁 {} 被线程 {} 持有", lockRedisKey, threadName));
                            if (RedissonLockUtil.isHeldByCurrentThread(lockRedisKey))
                            {
                                MatcheasyLog.info(StrUtil.format("锁 {} 被线程 {} 保持锁定", lockRedisKey, threadName));
                                RedissonLockUtil.unlock(lockRedisKey);
                                MatcheasyLog.info(StrUtil.format("线程 {} 释放锁 {}", threadName, lockRedisKey));
                            }
                        }
                    }
                }
                else
                {
                    MatcheasyLog.error(StrUtil.format("线程 {} 获取锁 {} 失败!", threadName, lockRedisKey));
                    throw new Exception("线程" + threadName + "获取锁[" + lockRedisKey + "]失败");
                }
            }
        }
        else
        {
            return pjp.proceed(objs);
        }
    }

}
