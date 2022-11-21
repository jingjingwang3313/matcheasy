package cn.matcheasy.framework.config.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.ws.rs.core.HttpHeaders;

/**
 * @CrossOrigin(origins="http://127.0.0.1:8080",allowCredentials = "true")
 * 通过@CrossOrigin注解允许跨域，origins设置了允许跨域请求的域，allowCredentials允许设置和接受Cookie
 * <p>
 * 前端设置允许发送和接受Cookie
 * $.ajax({
 * url : "http://www.b.com:8888/test/setCookie",
 * method: "get",
 * success : function (json) {
 * console.log(json);
 * },
 * xhrFields: {
 * withCredentials: true
 * }
 * });
 * @class CorsConfig
 * @author: wangjing
 * @date: 2020/11/6/0006
 * @desc: Cross-origin resource sharing
 */
@Slf4j
@Configuration
public class CorsConfig implements WebMvcConfigurer
{
    @Bean
    public WebMvcConfigurer corsConfigurer()
    {
        return new WebMvcConfigurer()
        {
            @Override
            public void addCorsMappings(CorsRegistry registry)
            {
                registry.addMapping("/**").
                        allowedOrigins("*"). //允许跨域的域名，可以用*表示允许任何域名使用
                        allowedMethods("*"). //允许任何方法（post、get等）
                        allowedHeaders("*"). //允许任何请求头
                        allowCredentials(true). //带上cookie信息
                        exposedHeaders(HttpHeaders.SET_COOKIE).maxAge(3600L); //在3600秒内，不需要再发送预检验请求，可以缓存该结果
            }
        };
    }
}
