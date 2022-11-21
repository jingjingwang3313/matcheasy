package cn.matcheasy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.PropertySources;
import org.springframework.util.StopWatch;

/**
 * @class Matcheasy
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: 启动类
 */
// 加载属性配置文件,可直接使用@Value("${keyName}")获取
@PropertySources({
        /*@PropertySource(value = {"file:/home/resource/mysql.properties",
                "file:/home/resource/mysql.properties"}, ignoreResourceNotFound = true),
        @PropertySource(value = "classpath:params.properties", ignoreResourceNotFound = true)*/
})
// 禁用自动装配
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
        // RedisAutoConfiguration.class,
        // RedisRepositoriesAutoConfiguration.class,
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class
})
public class Matcheasy implements ApplicationRunner
{

    private static final Logger logger = LoggerFactory.getLogger(Matcheasy.class);

    public static void main(String[] args)
    {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        SpringApplication.run(Matcheasy.class, args);
        stopWatch.stop();
        logger.info("【Matcheasy 启动成功】,耗时：" + stopWatch.getTotalTimeMillis() + "ms");
    }

    @Override
    public void run(ApplicationArguments args) throws Exception
    {
        logger.info("【Matcheasy 启动完成,执行系统初始化的操作...】");
    }

}