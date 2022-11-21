package cn.matcheasy.framework.config.threadpool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @class ThreadPoolConfig
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 * <p>
 * 当需要实现并发、异步等操作时，通常都会使用到多线程
 * 1、当任务是非必要的时候。比如记录操作日志、通知第三方服务非必要信息等，可以使用线程池处理非阻塞任务
 * 2、当任务非常耗时时候，可以采用线程池技术
 * 3、当请求并发很高时，可以采用线程池技术优化处理
 */
@Slf4j
@EnableAsync
@Configuration
//@ConfigurationProperties(prefix = "project.threadpool")//前缀
//@PropertySource(value = "classpath:project.properties")//配置文件路径
public class ThreadPoolConfig implements AsyncConfigurer
{

    // 核心线程池大小
    @Value("${project.threadpool.corePoolSize}")
    private int corePoolSize;

    // 最大可创建的线程数
    @Value("${project.threadpool.maxPoolSize}")
    private int maxPoolSize;

    // 队列最大长度
    @Value("${project.threadpool.queueCapacity}")
    private int queueCapacity;

    // 线程池维护线程所允许的空闲时间
    @Value("${project.threadpool.keepAliveSeconds}")
    private int keepAliveSeconds;

    // 设置线程池中任务的等待时间
    @Value("${project.threadpool.awaitTerminationSeconds}")
    private int awaitTerminationSeconds;

    @Bean("threadPoolExecutor")
    public ThreadPoolTaskExecutor taskExecutor()
    {
        ThreadPoolTaskExecutor executor = new MonitorThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setThreadNamePrefix("<threadPoolExecutor>");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(awaitTerminationSeconds);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        log.info("ThreadPool init ! ");
        return executor;
    }

    @Override
    public Executor getAsyncExecutor()
    {
        return taskExecutor();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler()
    {
        return (ex, method, params) -> log.error(String.format("执行异步任务'%s' ,异常:  ", method) + "【{}】", ex);
    }

}
