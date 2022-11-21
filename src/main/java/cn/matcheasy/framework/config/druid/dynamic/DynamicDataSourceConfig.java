package cn.matcheasy.framework.config.druid.dynamic;

import cn.hutool.core.convert.Convert;
import cn.matcheasy.framework.config.mybatisplus.CommonFieldHandler;
import cn.matcheasy.framework.config.transactional.MultiDataSourceTransactionFactory;
import com.alibaba.druid.filter.config.ConfigTools;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @class DynamicDataSourceConfig
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
@Slf4j
@Configuration
@MapperScan(basePackages = {DynamicDataSourceConfig.PACKAGE})
public class DynamicDataSourceConfig
{

    public static final String PACKAGE = "cn.matcheasy.**.mapper";

    public static final String MAPPER_LOCATION = "classpath*:mapper/**/*.xml";

    public static final String DS_PROP_PREFIX = "spring.datasource.druid.";

    @Value("${spring.datasource.druid.type:com.alibaba.druid.pool.xa.DruidXADataSource}")
    public String xaDataSourceClassName;

    @Primary
    @Bean(name = DataSourceKey.DEFAULT_MASTER)
    //@ConfigurationProperties(prefix = "spring.datasource.druid")
    public DataSource DEFAULT_MASTER(Environment env) throws Exception
    {
        String sourceName = DataSourceKey.DEFAULT_MASTER;
        Properties prop = build(env, sourceName);
        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSourceClassName(xaDataSourceClassName);
        xaDataSource.setUniqueResourceName(sourceName);
        xaDataSource.setPoolSize(5);
        xaDataSource.setReapTimeout(30000);
        xaDataSource.setXaProperties(prop);
        xaDataSource.init();
        return xaDataSource;
    }

    @Primary
    @Bean(name = DataSourceKey.DEFAULT_CLUSTER)
    public DataSource DEFAULT_CLUSTER(Environment env) throws Exception
    {
        String sourceName = DataSourceKey.DEFAULT_CLUSTER;
        Properties prop = build(env, sourceName);
        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSourceClassName(xaDataSourceClassName);
        xaDataSource.setUniqueResourceName(sourceName);
        xaDataSource.setPoolSize(5);
        xaDataSource.setReapTimeout(30000);
        xaDataSource.setXaProperties(prop);
        xaDataSource.init();
        return xaDataSource;
    }

    @Primary
    @Bean(name = DataSourceKey.OTHER_MASTER)
    public DataSource OTHER_MASTER(Environment env) throws Exception
    {
        String sourceName = DataSourceKey.OTHER_MASTER;
        Properties prop = build(env, sourceName);
        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSourceClassName(xaDataSourceClassName);
        xaDataSource.setUniqueResourceName(sourceName);
        xaDataSource.setPoolSize(5);
        xaDataSource.setReapTimeout(30000);
        xaDataSource.setXaProperties(prop);
        xaDataSource.init();
        return xaDataSource;
    }

    @Primary
    @Bean(name = DataSourceKey.OTHER_CLUSTER)
    public DataSource OTHER_CLUSTER(Environment env) throws Exception
    {
        String sourceName = DataSourceKey.OTHER_CLUSTER;
        Properties prop = build(env, sourceName);
        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSourceClassName(xaDataSourceClassName);
        xaDataSource.setUniqueResourceName(sourceName);
        xaDataSource.setPoolSize(5);
        xaDataSource.setReapTimeout(30000);
        xaDataSource.setXaProperties(prop);
        xaDataSource.init();
        return xaDataSource;
    }

    // 配置数据源
    @Primary
    @Bean(name = "dynamicDataSource")
    public DataSourceContext dataSource(@Qualifier(DataSourceKey.DEFAULT_CLUSTER) DataSource default_cluster,
                                        @Qualifier(DataSourceKey.DEFAULT_MASTER) DataSource default_master,
                                        @Qualifier(DataSourceKey.OTHER_CLUSTER) DataSource other_cluster,
                                        @Qualifier(DataSourceKey.OTHER_MASTER) DataSource other_master)
    {
        Map<Object, Object> targetDataSources = new HashMap<>(4);
        targetDataSources.put(DataSourceKey.DEFAULT_CLUSTER, default_cluster);
        targetDataSources.put(DataSourceKey.DEFAULT_MASTER, default_master);
        targetDataSources.put(DataSourceKey.OTHER_CLUSTER, other_cluster);
        targetDataSources.put(DataSourceKey.OTHER_MASTER, other_master);

        DataSourceContext dataSourceContext = new DataSourceContext(default_cluster, targetDataSources);
        return dataSourceContext;
    }

    /**
     * @param env, sourceName
     * @return java.util.Properties
     * @desc 配置连接属性
     */
    private static Properties build(Environment env, String sourceName) throws Exception
    {
        Properties prop = buildBasic(env);
        prop.put("url", env.getProperty(DS_PROP_PREFIX + sourceName + ".url"));
        prop.put("username", env.getProperty(DS_PROP_PREFIX + sourceName + ".username"));
        Boolean enbleKey = Convert.toBool(env.getProperty(DS_PROP_PREFIX + sourceName + ".enbleKey"));
        if (enbleKey)
        {
            prop.put("password", ConfigTools.decrypt(env.getProperty(DS_PROP_PREFIX + sourceName + ".publicKey"), env.getProperty(DS_PROP_PREFIX + sourceName + ".password")));
        }
        else
        {
            prop.put("password", env.getProperty(DS_PROP_PREFIX + sourceName + ".password"));
        }
        return prop;
    }

    /**
     * @param env
     * @return java.util.Properties
     * @desc 配置基本属性
     */
    private static Properties buildBasic(Environment env) throws Exception
    {
        Properties prop = new Properties();
        prop.put("driverClassName", env.getProperty(DS_PROP_PREFIX + "driverClassName"));
        prop.put("initialSize", env.getProperty(DS_PROP_PREFIX + "initialSize", Integer.class));
        prop.put("minIdle", env.getProperty(DS_PROP_PREFIX + "minIdle", Integer.class));
        prop.put("maxActive", env.getProperty(DS_PROP_PREFIX + "maxActive", Integer.class));
        prop.put("maxWait", env.getProperty(DS_PROP_PREFIX + "maxWait", Integer.class));
        prop.put("useUnfairLock", env.getProperty(DS_PROP_PREFIX + "useUnfairLock", Boolean.class));
        prop.put("timeBetweenEvictionRunsMillis",
                env.getProperty(DS_PROP_PREFIX + "timeBetweenEvictionRunsMillis", Integer.class));
        prop.put("minEvictableIdleTimeMillis", env.getProperty(DS_PROP_PREFIX + "minEvictableIdleTimeMillis", Integer.class));
        prop.put("maxEvictableIdleTimeMillis", env.getProperty(DS_PROP_PREFIX + "maxEvictableIdleTimeMillis", Integer.class));
        prop.put("poolPreparedStatements", env.getProperty(DS_PROP_PREFIX + "poolPreparedStatements", Boolean.class));
        prop.put("maxOpenPreparedStatements", env.getProperty(DS_PROP_PREFIX + "maxOpenPreparedStatements", Integer.class));
        prop.put("maxPoolPreparedStatementPerConnectionSize",
                env.getProperty(DS_PROP_PREFIX + "maxPoolPreparedStatementPerConnectionSize", Integer.class));
        prop.put("validationQuery", env.getProperty(DS_PROP_PREFIX + "validationQuery"));
        prop.put("validationQueryTimeout", env.getProperty(DS_PROP_PREFIX + "validationQueryTimeout", Integer.class));
        prop.put("testWhileIdle", env.getProperty(DS_PROP_PREFIX + "testWhileIdle", Boolean.class));
        prop.put("testOnBorrow", env.getProperty(DS_PROP_PREFIX + "testOnBorrow", Boolean.class));
        prop.put("testOnReturn", env.getProperty(DS_PROP_PREFIX + "testOnReturn", Boolean.class));
        prop.put("removeAbandoned", env.getProperty(DS_PROP_PREFIX + "removeAbandoned", Boolean.class));
        prop.put("logAbandoned", env.getProperty(DS_PROP_PREFIX + "logAbandoned", Boolean.class));
        prop.put("filters", env.getProperty(DS_PROP_PREFIX + "filters"));
        prop.put("connectionProperties", env.getProperty(DS_PROP_PREFIX + "connectionProperties"));
        prop.put("useGlobalDataSourceStat", env.getProperty(DS_PROP_PREFIX + "useGlobalDataSourceStat"));
        return prop;
    }

    @Primary
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dynamicDataSource") DataSource multiDataSource)
            throws Exception
    {
        //mybatis-plus包与mybatis包还是有区别的, SqlSessionFactoryBean 替换成 MybatisSqlSessionFactoryBean
        final MybatisSqlSessionFactoryBean sessionFactory = new MybatisSqlSessionFactoryBean();
        sessionFactory.setDataSource(multiDataSource);
        // 开启多数据源事务会导致默认数据源失效,需再指定
        sessionFactory.setTransactionFactory(new MultiDataSourceTransactionFactory());
        sessionFactory.setPlugins(new Interceptor[]{paginationInterceptor()});
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(DynamicDataSourceConfig.MAPPER_LOCATION));

        //获取mybatis-plus全局配置
        //GlobalConfig globalConfig = GlobalConfigUtils.defaults();
        GlobalConfig globalConfig = new GlobalConfig();
        //mybatis-plus全局配置设置元数据对象处理器为自己实现的那个
        globalConfig.setMetaObjectHandler(new CommonFieldHandler());
        //sessionFactory关联设置全局配置
        sessionFactory.setGlobalConfig(globalConfig);

        return sessionFactory.getObject();
    }

    /**
     * MybatisPlus分页插件
     */
    @Bean
    public MybatisPlusInterceptor paginationInterceptor()
    {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 分页插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setDbType(DbType.MYSQL);
        paginationInnerInterceptor.setOverflow(true);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);

        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        return interceptor;
    }

    @Primary
    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate setSqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception
    {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    public StatFilter statFilter()
    {
        StatFilter statFilter = new StatFilter();
        statFilter.setLogSlowSql(true); //slowSqlMillis用来配置SQL慢的标准，执行时间超过slowSqlMillis的就是慢。
        statFilter.setMergeSql(true); //SQL合并配置
        statFilter.setSlowSqlMillis(1000);//slowSqlMillis的缺省值为3000，也就是3秒。
        return statFilter;
    }

    @Bean
    public WallFilter wallFilter()
    {
        WallFilter wallFilter = new WallFilter();
        //允许执行多条SQL
        WallConfig config = new WallConfig();
        config.setMultiStatementAllow(true);
        wallFilter.setConfig(config);
        return wallFilter;
    }

}
