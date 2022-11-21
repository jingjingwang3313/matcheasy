package cn.matcheasy.framework.config.security;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @EnableWebSecurity
 * 1: 加载了WebSecurityConfiguration配置类, 配置安全认证策略。
 * 2: 加载了AuthenticationConfiguration, 配置了认证信息。
 *
 * @class SpringSecurity
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: 使用springSecurity保证actuator, swagger的安全
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    Environment env;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String contextPath = env.getProperty("management.endpoints.web.base-path");
        if (StringUtils.isEmpty(contextPath)) {
            contextPath = "/actuator";
        }
		// 屏蔽CSRF控制,Spring Security为防止CSRF(Cross-site requetst forgery跨站请求伪造)的发生,限制了除了get以外的大多数方法。
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/**" + contextPath + "/**").authenticated()
                .antMatchers("/swagger-ui.html").authenticated()
                .antMatchers("/docs.html").authenticated()
				.antMatchers("/doc.html").authenticated()
                .anyRequest().permitAll()
                .and().httpBasic();
    }

}
