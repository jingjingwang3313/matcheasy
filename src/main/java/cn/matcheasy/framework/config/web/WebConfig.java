package cn.matcheasy.framework.config.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @class WebConfig
 * @author: wangjing
 * @date: 2020/11/6/0006
 * @desc: TODO
 */
@Slf4j
@Configuration
//添加注解EnableWebMvc会导致JacksonConfig全局数据处理失效
//@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter implements WebMvcConfigurer {

    @Value("${uploadPath}")
    private String uploadPath;

    @Value("${virtualPath}")
    private String virtualPath;

    @Value("${spring.messages.encoding}")
    private String encoding;

    /**
     * Handled by wangjing at 2021/2/1/0001 9:41
     * @Desc 配置静态资源映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置swagger2映射
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("docs.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        // 配置静态资源路径映射
        //registry.addResourceHandler("/static/**", "/**").addResourceLocations("classpath:/static/");
        //配置文件虚拟路径映射
        registry.addResourceHandler(virtualPath + "**").addResourceLocations("file:" + uploadPath);
        super.addResourceHandlers(registry);
    }

    /**
     * Handled by wangjing at 2021/2/1/0001 9:40
     * @Desc 解决@ResponseBody返回String中文乱码
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        StringHttpMessageConverter stringHttpMessageConverter = (StringHttpMessageConverter) converters.get(1);
        stringHttpMessageConverter.setDefaultCharset(Charset.forName(encoding));
    }

    /**
     * Handled by wangjing at 2021/2/1/0001 9:40
     * @Desc springMVC默认是没有静态资源放行的, 比如 .css .js 文件默认也会被 dispatchServelt / 形式的拦截到
     */
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        // 处理静态资源不拦截
        configurer.enable();
    }

}
