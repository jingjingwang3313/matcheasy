package cn.matcheasy.framework.filter;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.matcheasy.framework.config.shiro.JWTToken;
import cn.matcheasy.framework.constant.ProjectConst;
import cn.matcheasy.framework.log.MatcheasyLog;
import cn.matcheasy.framework.result.ResultEnum;
import cn.matcheasy.framework.utils.ComU;
import cn.matcheasy.framework.utils.ThreadValueUtil;
import cn.matcheasy.framework.utils.UUIDUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * @class JwtFilter
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: JWT过滤器
 */
@Slf4j
public class JwtFilter extends BasicHttpAuthenticationFilter
{
    /**
     * 如果带有 access_token，则对 access_token 进行检查，否则直接通过
     */
    @SneakyThrows
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object object) throws UnauthorizedException
    {
        ThreadValueUtil.set(ProjectConst.TRACE_ID, Convert.toStr(ThreadValueUtil.get(ProjectConst.TRACE_ID), UUIDUtil.fastSimpleUUID()));

        HttpServletRequest req = (HttpServletRequest) request;
        MatcheasyLog.info(StrUtil.format("Shiro 拦截 URI: {}", req.getRequestURI()));
        if (isLoginAttempt(request, response))
        {
            //如果存在，则进入 executeLogin 方法执行登入，检查 access_token 是否正确
            try
            {
                executeLogin(request, response);
                return true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                responseError(response, ResultEnum.TOKEN_EXPIRED.getCode(), ResultEnum.TOKEN_EXPIRED.getMsg());
            }
        }
        else
        {
            responseError(response, ResultEnum.TOKEN_LOSE.getCode(), ResultEnum.TOKEN_LOSE.getMsg());
            MatcheasyLog.error(ResultEnum.TOKEN_LOSE.getMsg());
        }
        return false;
    }

    /**
     * 检测request里面是否包含 access_token 字段
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response)
    {
        HttpServletRequest req = (HttpServletRequest) request;
        String access_token = req.getHeader(ProjectConst.ACCESS_TOKEN);
        if (StringUtils.isEmpty(access_token))
        {
            access_token = req.getParameter(ProjectConst.ACCESS_TOKEN);
        }
        if (StringUtils.isEmpty(access_token))
        {
            access_token = MapUtil.getStr(ComU.getRequestMap(req), ProjectConst.ACCESS_TOKEN);
        }
        return StringUtils.isNotEmpty(access_token);
    }

    /**
     * 执行登陆操作
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception
    {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String access_token = httpServletRequest.getHeader(ProjectConst.ACCESS_TOKEN);
        if (StringUtils.isEmpty(access_token))
        {
            access_token = httpServletRequest.getParameter(ProjectConst.ACCESS_TOKEN);
        }
        if (StringUtils.isEmpty(access_token))
        {
            access_token = MapUtil.getStr(ComU.getRequestMap(httpServletRequest), ProjectConst.ACCESS_TOKEN);
        }
        String uri = httpServletRequest.getRequestURI();
        JWTToken jwtToken = null;
        if (StringUtils.contains(uri, ProjectConst.ADMIN))
        {
            jwtToken = new JWTToken(access_token, ProjectConst.ADMIN);
        }
        else if (StringUtils.contains(uri, ProjectConst.COMPANY))
        {
            jwtToken = new JWTToken(access_token, ProjectConst.COMPANY);
        }
        else if (StringUtils.contains(uri, ProjectConst.PERSONAL))
        {
            jwtToken = new JWTToken(access_token, ProjectConst.PERSONAL);
        }
        else
        {
            jwtToken = new JWTToken(access_token, ProjectConst.OTHERS);
        }
        // 提交给realm进行登入，错误会抛出异常并被捕获
        getSubject(request, response).login(jwtToken);
        // 如果没有抛出异常则代表登入成功，返回true
        return true;
    }

    /**
     * 对跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception
    {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name()))
        {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }

    /**
     * 返回值为false表示过滤器的工作结束
     */
    @Override
    public boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception
    {
        return false;
    }

    private void responseError(ServletResponse response, String code, String message)
    {
        try
        {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            message = URLEncoder.encode(message, "UTF-8");
            httpServletResponse.sendRedirect("/anon/error/" + code + "/" + message);
        }
        catch (IOException e)
        {
            MatcheasyLog.error(e.getMessage(), e);
        }
    }

}