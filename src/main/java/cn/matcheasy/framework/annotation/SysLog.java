package cn.matcheasy.framework.annotation;

import cn.hutool.core.util.StrUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @class: SysLog
 * @author: wangjing
 * @date: 2022/2/8/0008
 * @desc: 标记次注解的方法进行系统日记记录
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SysLog
{
    /**
     * sys(),module(),content(),operation()可直接简写到value中，支持的分隔符【- , : ; # 】
     * 格式：（系统名称-模块名称-操作内容-操作类型）
     */
    String value() default StrUtil.EMPTY;

    /**
     * 系统名称
     */
    String sys() default StrUtil.EMPTY;

    /**
     * 模块名称
     */
    String module() default StrUtil.EMPTY;

    /**
     * 操作内容
     */
    String content() default StrUtil.EMPTY;

    /**
     * 操作类型
     */
    String operation() default StrUtil.EMPTY;

    /**
     * 日志级别 info,debug,warning,error
     */
    String level() default "info";

    /**
     * 是否打印入参
     */
    boolean req() default true;

    /**
     * 是否打印出参
     */
    boolean resp() default true;

    /**
     * 是否保存日志,false时可以只打印不保存
     */
    boolean save() default false;
}
