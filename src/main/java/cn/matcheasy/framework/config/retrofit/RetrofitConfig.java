package cn.matcheasy.framework.config.retrofit;


import com.github.lianjiatech.retrofit.spring.boot.annotation.RetrofitScan;
import org.springframework.context.annotation.Configuration;

/**
 * @class RetrofitConfig
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: 简化spring-boot项目下http接口调用开发
 *
 * 某个接口下的某些http请求执行统一的拦截处理逻辑,可以使用注解式拦截器
 * 1. 继承 BasePathMatchInterceptor 编写拦截处理器
 * 2. 接口上使用 @Intercept 进行标注
 * 参考: https://blog.csdn.net/why_still_confused/article/details/108041657
 */
@Configuration
@RetrofitScan("cn.matcheasy")
public class RetrofitConfig {

}
