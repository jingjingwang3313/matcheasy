package cn.matcheasy.framework.config.mongo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @class: MongoConfig
 * @author: wangjing
 * @date: 2021/9/11/0011
 * @desc: TODO
 */
@Slf4j
@Configuration
public class MongoConfig
{
    public static final String MONGO_PROP_PREFIX = "spring.data.mongodb.";

    @Bean(name = "defaultMongoProperties")
    @Primary
    @ConfigurationProperties(prefix = MongoConfig.MONGO_PROP_PREFIX + DataSourceKey.DEFAULT_MONGO)
    public MongoProperties defaultMongoProperties()
    {
        log.info("defaultMongoProperties init !");
        return new MongoProperties();
    }

    @Bean(name = "otherMongoProperties")
    @ConfigurationProperties(prefix = MongoConfig.MONGO_PROP_PREFIX + DataSourceKey.OTHER_MONGO)
    public MongoProperties otherMongoProperties()
    {
        log.info("otherMongoProperties init !");
        return new MongoProperties();
    }

}
