package cn.matcheasy.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @class CheckToken
 * @author: wangjing
 * @date: 2020/11/6/0006
 * @desc: TODO
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckToken {

    // 是否验证api携带token
    boolean required() default true;

}
