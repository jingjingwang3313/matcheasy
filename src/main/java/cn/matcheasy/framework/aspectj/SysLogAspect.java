package cn.matcheasy.framework.aspectj;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.matcheasy.framework.annotation.SysLog;
import cn.matcheasy.framework.commonbean.LogBean;
import cn.matcheasy.framework.constant.ProjectConst;
import cn.matcheasy.framework.exception.Try;
import cn.matcheasy.framework.utils.IpUtil;
import cn.matcheasy.framework.utils.RequestUtil;
import cn.matcheasy.framework.utils.ThreadValueUtil;
import cn.matcheasy.framework.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @class: SysLogAspect
 * @author: wangjing
 * @date: 2022/2/8/0008
 * @desc: SysLog注解日志处理
 */
@Slf4j
@Aspect
@Component
public class SysLogAspect
{
    @Around("@annotation(sysLog)")
    public Object doAround(ProceedingJoinPoint joinPoint, SysLog sysLog) throws Throwable
    {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        HttpServletRequest request = RequestUtil.getRequest();

        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();

        // 日志对象
        LogBean logEntity = new LogBean();

        // 是否打印入参
        if (sysLog.req())
        {
            String reqStr = this.getReqStr(request, joinPoint);
            log.info("[{}.{}]{}", className, methodName, reqStr);
            logEntity.setReq(reqStr);
        }

        String traceId = Convert.toStr(ThreadValueUtil.get(ProjectConst.TRACE_ID), UUIDUtil.fastSimpleUUID());
        logEntity.setTraceId(traceId);
        logEntity.setClassName(className);
        logEntity.setMethodName(methodName);
        logEntity.setLevel(sysLog.level());
        logEntity.setIp(IpUtil.getIpAddress(request));
        logEntity.setUrl(this.getUrl(request));
        logEntity.setCreateTime(new Date());

        Object result = null;
        try
        {
            result = joinPoint.proceed();
        }
        finally
        {
            stopWatch.stop();
            long totalTimeMillis = stopWatch.getTotalTimeMillis();
            logEntity.setTimeCost(totalTimeMillis);

            // 是否打印出参
            if (sysLog.resp())
            {
                String respStr = this.getRespStr(result);
                log.info("[{}.{}]{} --- {} ms", className, methodName, respStr, totalTimeMillis);
                logEntity.setResp(respStr);
            }
            // 是否日志保存
            if (sysLog.save())
            {
            }
        }
        return result;
    }

    /**
     * 获取url
     */
    private String getUrl(HttpServletRequest request)
    {
        try
        {
            return StrUtil.trim(request.getRequestURI());
        }
        catch (Exception e)
        {
            log.error("获取url异常", e);
            return StrUtil.EMPTY;
        }
    }

    /**
     * 获取参数
     */
    private String getReqStr(HttpServletRequest request, ProceedingJoinPoint joinPoint)
    {
        try
        {
            Map<String, Object> map = new HashMap<>();
            if (request != null)
            {
                Map<String, Object> requestParam = RequestUtil.requestParamToMap(request);
                map.put("argRequest", requestParam);
            }
            if (joinPoint.getArgs().length > 0)
            {
                // 获取请求参数集合并进行遍历拼接
                for (int i = 0; i < joinPoint.getArgs().length; i++)
                {
                    Object object = joinPoint.getArgs()[i];
                    if (!(object instanceof MultipartFile)
                            && !(object instanceof ServletRequest)
                            && !(object instanceof ServletResponse))
                    {
                        Try ret = Try.run(() -> JSONUtil.toJsonStr(object));
                        if (ret.isSuccess())
                        {
                            map.put("arg" + i, object);
                        }

                    }
                }
            }
            if (map.size() == 1)
            {
                for (Map.Entry<String, Object> entry : map.entrySet())
                {
                    Object value = entry.getValue();
                    if (value == null)
                    {
                        continue;
                    }
                    return subString(JSONUtil.toJsonStr(value));
                }
            }
            return subString(JSONUtil.toJsonStr(map));
        }
        catch (Exception e)
        {
            log.error("获取参数异常", e);
            return StrUtil.EMPTY;
        }
    }

    /**
     * 返回值
     */
    private String getRespStr(Object result)
    {
        if (result == null)
        {
            return StrUtil.EMPTY;
        }
        try
        {
            String resp = JSONUtil.toJsonStr(result);
            return subString(resp);
        }
        catch (Exception e)
        {
            log.error("获取结果异常", e);
            return StrUtil.EMPTY;
        }
    }

    /**
     * 截取字符串
     */
    private String subString(String str)
    {
        if (StrUtil.isBlank(str) || str.length() <= 3000)
        {
            return str;
        }
        return str.substring(0, 1500) + "..." + str.substring(str.length() - 1500);
    }
}
