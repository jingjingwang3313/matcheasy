package cn.matcheasy.framework.config.druid.dynamic;

import cn.hutool.core.util.StrUtil;
import cn.matcheasy.framework.log.MatcheasyLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @class DataSourceContext
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
@Slf4j
public class DataSourceContext extends AbstractRoutingDataSource
{

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    public DataSourceContext(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources)
    {
        super.setDefaultTargetDataSource(defaultTargetDataSource);
        super.setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }

    //设置数据源
    public static void setDataSource(String value)
    {
        contextHolder.set(value);
        MatcheasyLog.info(StrUtil.format("Switch DataSource {} successfully!", DataSourceContext.getDataSource()));
    }

    //获取数据源
    public static String getDataSource()
    {
        return contextHolder.get();
    }

    //重置数据源
    public static void clearDataSource()
    {
        MatcheasyLog.info(StrUtil.format("DataSource {} is used and reset!", DataSourceContext.getDataSource()));
        contextHolder.remove();
    }

    @Override
    protected Object determineCurrentLookupKey()
    {
        return getDataSource();
    }
}
