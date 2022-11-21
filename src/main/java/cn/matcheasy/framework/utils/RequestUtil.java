package cn.matcheasy.framework.utils;

import cn.hutool.core.lang.Dict;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @class: RequestUtil
 * @author: wangjing
 * @date: 2022/2/8/0008
 * @desc: Request工具类
 */
public class RequestUtil
{
    /**
     * 获取 request
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest()
    {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null)
        {
            return null;
        }
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    /**
     * 获取参数值
     *
     * @param name 参数key
     * @return String
     */
    public static String getParameter(String name)
    {
        HttpServletRequest request = getRequest();
        return getParameter(request, name);
    }

    /**
     * 获取参数值
     *
     * @param request request
     * @param name    name
     * @return String
     */
    public static String getParameter(HttpServletRequest request, String name)
    {
        if (request == null)
        {
            return null;
        }
        return request.getParameter(name);
    }

    /**
     * 将request的参数转化为map
     *
     * @return Dict
     */
    public static Dict requestParamToDict()
    {
        HttpServletRequest request = getRequest();
        return requestParamToDict(request);
    }

    /**
     * 将request的参数转化为map
     *
     * @param request request
     * @return Dict
     */
    public static Dict requestParamToDict(HttpServletRequest request)
    {
        Dict map = Dict.create();
        if (request == null)
        {
            return map;
        }
        Enumeration<String> parameterNames = request.getParameterNames();
        if (parameterNames != null)
        {
            String key, value;
            while (parameterNames.hasMoreElements())
            {
                key = parameterNames.nextElement();
                value = request.getParameter(key);
                map.set(key, value);
            }
        }
        return map;
    }

    /**
     * 将request的参数转化为map
     *
     * @return Map
     */
    public static Map<String, Object> requestParamToMap()
    {
        HttpServletRequest request = getRequest();
        return requestParamToMap(request);
    }

    /**
     * 将request的参数转化为map
     *
     * @param request request
     * @return Map
     */
    public static Map<String, Object> requestParamToMap(HttpServletRequest request)
    {
        Map<String, Object> map = new HashMap<>();
        if (request == null)
        {
            return map;
        }
        Enumeration<String> parameterNames = request.getParameterNames();
        if (parameterNames != null)
        {
            String key, value;
            while (parameterNames.hasMoreElements())
            {
                key = parameterNames.nextElement();
                value = request.getParameter(key);
                map.put(key, value);
            }
        }
        return map;
    }
}
