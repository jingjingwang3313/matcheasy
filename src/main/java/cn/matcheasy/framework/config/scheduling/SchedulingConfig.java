package cn.matcheasy.framework.config.scheduling;

import cn.hutool.core.date.DateUtil;
import cn.matcheasy.project.common.task.schedule.service.CronService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

/**
 * @class: SchedulingConfig
 * @author: wangjing
 * @date: 2021/5/19/0019
 * @desc: 定时任务, Scheduled Task不适用于服务器集群,在集群环境中,每个节点都会执行一次定时任务,会造成任务的重复执行.
 * 解决方案: 1. 使用分布式定时任务框架 2. 使用分布式锁给定时任务加锁
 */
@Slf4j
//@EnableScheduling
//@Configuration
public class SchedulingConfig implements SchedulingConfigurer
{

    @Autowired
    CronService cronService;

    /**
     * 动态获取数据库配置定时任务执行
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar)
    {

        /**
         * 可创建多个 taskRegistrar 执行动态定时任务
         */
        taskRegistrar.addTriggerTask(
                () ->
                {
                    // 添加任务内容(Runnable)
                    log.info("【{}】执行动态定时任务", DateUtil.now());
                },
                triggerContext ->
                {
                    // 设置执行周期(Trigger)
                    String cron = cronService.getCron();//查库动态获取cron表达式,不能写在此方法外面
                    return new CronTrigger(cron).nextExecutionTime(triggerContext);
                }
        );

    }

}
