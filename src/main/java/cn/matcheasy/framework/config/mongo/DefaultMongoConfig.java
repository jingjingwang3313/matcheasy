package cn.matcheasy.framework.config.mongo;

import com.mongodb.MongoClientURI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @class: DefaultMongoConfig
 * @author: wangjing
 * @date: 2021/6/28/0022
 * @desc: TODO
 */
@Slf4j
@Configuration
@EnableMongoRepositories(basePackages = "cn.matcheasy", mongoTemplateRef = DefaultMongoConfig.MONGO_TEMPLATE)
public class DefaultMongoConfig extends MongoConfig
{
    protected static final String MONGO_TEMPLATE = "defaultMongoTemplate";

    @Autowired
    @Qualifier("defaultMongoProperties")
    private MongoProperties mongoProperties;

    @Primary
    @Bean(name = DefaultMongoConfig.MONGO_TEMPLATE)
    public MongoTemplate defaultMongoTemplate()
    {
        MongoDbFactory factory = defaultFactory(this.mongoProperties);
        MappingMongoConverter converter = new MappingMongoConverter(factory, new MongoMappingContext());
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return new MongoTemplate(factory, converter);
    }

    @Primary
    @Bean(name = "defaultGridFs")
    public GridFsTemplate defaultGridFsTemplate() throws Exception
    {
        MongoDbFactory factory = defaultFactory(this.mongoProperties);
        MappingMongoConverter converter = new MappingMongoConverter(factory, new MongoMappingContext());
        return new GridFsTemplate(factory, converter);
    }

    @Bean
    @Primary
    public MongoDbFactory defaultFactory(MongoProperties mongoProperties)
    {
        return new SimpleMongoDbFactory(new MongoClientURI(mongoProperties.getUri()));
    }

    //使用事务必须添加
    @Primary
    @Bean(name = "defaultTransactionManager")
    public MongoTransactionManager defaultTransactionManager()
    {
        MongoDbFactory factory = defaultFactory(this.mongoProperties);
        return new MongoTransactionManager(factory);
    }

}
