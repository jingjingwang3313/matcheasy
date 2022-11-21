package cn.matcheasy.framework.config.filter;

import cn.matcheasy.framework.filter.RequestFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @class FilterConfig
 * @author: wangjing
 * @date: 2020/11/6/0006
 * @desc: TODO
 */
@Slf4j
@Configuration
public class FilterConfig implements WebMvcConfigurer
{

    @Bean
    public RequestFilter getFirstFilter()
    {
        return new RequestFilter();
    }

    @Bean
    public FilterRegistrationBean<RequestFilter> requestFilter(RequestFilter requestFilter)
    {
        FilterRegistrationBean<RequestFilter> registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(requestFilter);
        registrationBean.addUrlPatterns("/**"); //url拦截
        registrationBean.setOrder(1);
        registrationBean.setAsyncSupported(true);
        log.info("RequestFilter Regist！");
        return registrationBean;
    }

    /**
     * 一个http请求,先走filter,到达servlet后才进行拦截器的处理
     * 如果我们把cors放在filter里,就可以优先于权限拦截器执行
     */
    @Bean
    public CorsFilter corsFilter()
    {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}
