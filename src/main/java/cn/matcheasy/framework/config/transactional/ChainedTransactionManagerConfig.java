package cn.matcheasy.framework.config.transactional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @class: ChainedTransactionManagerConfig
 * @author: wangjing
 * @date: 2021/9/13/0013
 * @desc: 多数据源事务统一回滚主要用了Spring的分布式事务ChainedTransactionManager
 */
@Configuration
public class ChainedTransactionManagerConfig
{
    //配置Mongo多数据源事务(mongoDB单个实例不支持事务，副本集才支持事务)
    @Bean(name = "mongoTransactionManager")
    public ChainedTransactionManager mongoTransactionManager(
            @Qualifier("defaultTransactionManager") PlatformTransactionManager ptm1,
            @Qualifier("otherTransactionManager") PlatformTransactionManager ptm2)
    {
        return new ChainedTransactionManager(ptm1, ptm2);
    }
}
