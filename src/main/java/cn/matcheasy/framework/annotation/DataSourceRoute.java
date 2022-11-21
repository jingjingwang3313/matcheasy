package cn.matcheasy.framework.annotation;

import cn.matcheasy.framework.config.druid.dynamic.DataSourceKey;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @class DataSourceRoute
 * @author: wangjing
 * @date: 2020/11/6/0006
 * @desc: TODO
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSourceRoute {

    // 指定数据源key
    String name() default DataSourceKey.DEFAULT_CLUSTER;

}
