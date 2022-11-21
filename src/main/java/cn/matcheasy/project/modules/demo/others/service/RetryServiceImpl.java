package cn.matcheasy.project.modules.demo.others.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

/**
 * @class: RetryServiceImpl
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 * @Retryable：标记重试方法(加上事务后会存在问题: 1. maxAttempts*maxAttempts 次调用 ; 2. 事务超时错误)
 * value: 重试的触发机制,当遇到Exception异常的时候触发
 * maxAttempts: 重试次数(包括第一次调用)
 * delay: 重试的延迟时间,也就是距离上一次重试方法调用的间隔,单位毫秒
 * multiplier: delay重试时间的间隔倍数,每次执行间隔加倍延迟
 * maxDelay: 重试次数之间的最大时间间隔,默认为0,如果小于delay的设置,则默认为30000L
 * @Recover: 标记回调方法, 该回调方法与重试方法写在同一个实现类里面,
 * 传参与@Retryable的value值需一致(特殊场景应用,一般还是抛异常进行事务控制,若采用回调则不再向上层抛异常)
 */
@Slf4j
@Service
public class RetryServiceImpl implements RetryService
{

    //@Transactional(rollbackFor = Exception.class)
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 3000L, multiplier = 1, maxDelay = 10000L))
    @Override
    public void retry() throws Exception
    {
        log.warn("执行重试机制!");
        int a = 1 / 0;
    }

    @Recover
    public void recover(Exception e)
    {
        log.error("回调方法执行!" + e.getMessage());
    }

}