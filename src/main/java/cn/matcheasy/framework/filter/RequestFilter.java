package cn.matcheasy.framework.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @class RequestFilter
 * @author: wangjing
 * @date: 2020/11/6/0006
 * @desc: TODO
 */
@Slf4j
public class RequestFilter implements Filter
{

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException
    {
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;
        response.setCharacterEncoding("UTF-8");
        // OPTIONS请求预检,类似于心跳机制,拦截器里面应该把这个请求过滤掉
        if (RequestMethod.OPTIONS.name().equals(request.getMethod()))
        {
            log.info("OPTIONS请求不做拦截操作");
            return;
        }
        else
        {
            chain.doFilter(req, res);
        }
    }

    @Override
    public void destroy()
    {
    }

}