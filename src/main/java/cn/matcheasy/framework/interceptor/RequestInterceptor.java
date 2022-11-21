package cn.matcheasy.framework.interceptor;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.matcheasy.framework.annotation.CheckToken;
import cn.matcheasy.framework.constant.ProjectConst;
import cn.matcheasy.framework.log.MatcheasyLog;
import cn.matcheasy.framework.result.ResultEnum;
import cn.matcheasy.framework.utils.ComU;
import cn.matcheasy.framework.utils.RedisService;
import cn.matcheasy.framework.utils.ThreadValueUtil;
import cn.matcheasy.framework.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @class RequestInterceptor
 * @author: wangjing
 * @date: 2020/11/6/0006
 * @desc: TODO
 */
@Slf4j
@Component
public class RequestInterceptor implements HandlerInterceptor
{
    /**
     * 过期时间,单位为秒
     **/
    @Value("${access_token.expireTime}")
    private int expireTime;

    @Autowired
    private RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception
    {
        ThreadValueUtil.set(ProjectConst.TRACE_ID, Convert.toStr(ThreadValueUtil.get(ProjectConst.TRACE_ID), UUIDUtil.fastSimpleUUID()));

        // 取出http请求中access_token,存在就刷新
        String access_token = httpServletRequest.getHeader(ProjectConst.ACCESS_TOKEN);
        if (StringUtils.isEmpty(access_token))
        {
            access_token = httpServletRequest.getParameter(ProjectConst.ACCESS_TOKEN);
        }
        if (StringUtils.isEmpty(access_token))
        {
            access_token = MapUtil.getStr(ComU.getRequestMap(httpServletRequest), ProjectConst.ACCESS_TOKEN);
        }
        if (!StringUtils.isEmpty(access_token))
        {
            // 是否认证都刷新access_token过期时间
            redisService.expire(access_token, expireTime, TimeUnit.SECONDS);
        }

        if (!(object instanceof HandlerMethod))
        {
            // 如果不是映射到方法直接通过
            return true;
        }
        else
        {
            HandlerMethod handlerMethod = (HandlerMethod) object;
            Method method = handlerMethod.getMethod();
            //检查有没有CheckToken的注解
            if (method.isAnnotationPresent(CheckToken.class))
            {
                CheckToken CheckToken = method.getAnnotation(CheckToken.class);
                if (CheckToken.required())
                {
                    if (StringUtils.isEmpty(access_token))
                    {
                        MatcheasyLog.error(ResultEnum.TOKEN_LOSE.getMsg());
                        throw new RuntimeException(ResultEnum.TOKEN_LOSE.getMsg());
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse,
                           Object o, ModelAndView modelAndView) throws Exception
    {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse,
                                Object o, Exception e) throws Exception
    {
    }
}
