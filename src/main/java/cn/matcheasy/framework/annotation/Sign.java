package cn.matcheasy.framework.annotation;

import cn.matcheasy.framework.interceptor.SignInterceptor;
import com.github.lianjiatech.retrofit.spring.boot.annotation.InterceptMark;
import com.github.lianjiatech.retrofit.spring.boot.interceptor.BasePathMatchInterceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @class: Sign
 * @author: wangjing
 * @date: 2021/2/20/0020
 * @desc: retrofit 扩展实现自定义拦截注解
 *
 * 自定义拦截注解 1.必须使用@InterceptMark标记.  2.注解中必须包括include()、exclude()、handler()属性信息。
 * 使用的步骤主要分为3步：
 * 1. 自定义拦截注解
 * 2. 继承BasePathMatchInterceptor编写拦截处理器
 * 3. 接口上使用自定义拦截注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@InterceptMark
public @interface Sign {

    /**
     * 密钥key
     * 支持占位符形式配置。
     * @return
     */
    String access_key();

    /**
     * 密钥
     * 支持占位符形式配置。
     * @return
     */
    String access_secret();

    /**
     * 拦截器匹配路径
     * @return
     */
    String[] include() default {"/**"};

    /**
     * 拦截器排除匹配，排除指定路径拦截
     * @return
     */
    String[] exclude() default {};

    /**
     * 处理该注解的拦截器类
     * 优先从spring容器获取对应的Bean，如果获取不到，则使用反射创建一个！
     * @return
     */
    Class<? extends BasePathMatchInterceptor> handler() default SignInterceptor.class;
}
