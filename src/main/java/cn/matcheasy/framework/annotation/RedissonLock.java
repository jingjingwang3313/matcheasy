package cn.matcheasy.framework.annotation;

import cn.matcheasy.framework.constant.RedissonLockKey;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @class RedissonLock
 * @author: wangjing
 * @date: 2020/11/6/0006
 * @desc: 分布式锁自定义注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedissonLock
{

    // 指定分布式锁key
    String lockRedisKey() default RedissonLockKey.DEMO_KEY_1;

}
