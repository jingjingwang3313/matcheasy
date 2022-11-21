package cn.matcheasy.framework.aspectj;

import cn.hutool.core.util.StrUtil;
import cn.matcheasy.framework.annotation.DataSourceRoute;
import cn.matcheasy.framework.config.druid.dynamic.DataSourceContext;
import cn.matcheasy.framework.config.druid.dynamic.DataSourceKey;
import cn.matcheasy.framework.log.MatcheasyLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @class DynamicDataSourceAspect
 * @author: wangjing
 * @date: 2020/11/6/0006
 * @desc: TODO
 */
@Slf4j
@Aspect
@Component
@Order(2)
public class DynamicDataSourceAspect
{
    //切点
    @Pointcut("execution(public * cn.matcheasy..service..*.*(..))")
    public void dsPointCut()
    {
    }

    //数据源切换设置
    @Before("dsPointCut()")
    public void before(JoinPoint joinPoint)
    {

        //指定默认数据源
        DataSourceContext.setDataSource(DataSourceKey.DEFAULT_CLUSTER);

        // 第一步判断类上注解
        Class<?> clazz = joinPoint.getTarget().getClass();
        if (clazz.isAnnotationPresent(DataSourceRoute.class))
        {
            DataSourceRoute dataSourceRoute = clazz.getAnnotation(DataSourceRoute.class);
            String type = dataSourceRoute.name();
            if (StringUtils.isNotBlank(type))
            {
                try
                {
                    DataSourceContext.setDataSource(type);
                }
                catch (Exception e)
                {
                    MatcheasyLog.error(StrUtil.format("切换数据源: {} 失败!", type), e);
                }
            }
        }

        // 第二步判断方法上注解
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        if (method.isAnnotationPresent(DataSourceRoute.class))
        {
            DataSourceRoute dataSourceRoute = method.getAnnotation(DataSourceRoute.class);
            String type = dataSourceRoute.name();
            if (StringUtils.isNotBlank(type))
            {
                try
                {
                    DataSourceContext.setDataSource(type);
                }
                catch (Exception e)
                {
                    MatcheasyLog.error(StrUtil.format("切换数据源: {} 失败!", type), e);
                }
            }
        }

    }

    //方法调用完之后恢复默认数据源
    @After("dsPointCut()")
    public void after(JoinPoint joinPoint)
    {
        DataSourceContext.clearDataSource();
    }

}
