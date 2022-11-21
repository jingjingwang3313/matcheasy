package cn.matcheasy.framework.aspectj;

import cn.hutool.core.util.StrUtil;
import cn.matcheasy.framework.log.MatcheasyLog;
import cn.matcheasy.framework.result.Result;
import cn.yueshutong.springbootstartercurrentlimiting.annotation.CurrentLimiter;
import cn.yueshutong.springbootstartercurrentlimiting.handler.CurrentAspectHandler;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @class CurrentLimitAspect
 * @author: wangjing
 * @date: 2020/11/10/0010
 * @desc: 限流的访问返回处理
 */
@Slf4j
@Component
public class CurrentLimitAspect implements CurrentAspectHandler
{

    @Override
    public Object around(ProceedingJoinPoint pjp, CurrentLimiter rateLimiter)
    {
        //ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //HttpServletResponse response = servletRequestAttributes.getResponse();
        //HttpServletRequest request = servletRequestAttributes.getRequest();
        String className = pjp.getSignature().getDeclaringTypeName();
        String methodName = pjp.getSignature().getName();
        String args = Arrays.toString(pjp.getArgs());
        long qps = rateLimiter.QPS();
        MatcheasyLog.error(StrUtil.format("{}", "API " + className + "." + methodName + "()  Access Limited ! Allow QPS :  " + qps + "/s 。"));
        return Result.error("API " + className + "." + methodName + "()  Access Limited ! Allow QPS :  " + qps + "/s 。");
    }

}