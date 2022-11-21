package cn.matcheasy.framework.aspectj;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.matcheasy.framework.log.MatcheasyLog;
import cn.matcheasy.framework.utils.ComU;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @class LogAspect
 * @author: wangjing
 * @date: 2020/11/6/0006
 * @desc: 接口统一日志, 环绕方法->方法执行前->方法执行后->方法返回后->环绕方法,开启环绕方法时,异常方法无效
 */
@Slf4j
@Aspect
@Component
@Order(1)
public class LogAspect
{

    /**
     * 配置切入点execution表达式
     * 第一个 * 表示匹配任何返回值,第二个 * 表示匹配任何类,第三个 * 表示匹配任何方法
     * 第一个  ..(两个点) 表示当前包及其子包 , 第二个  ..(两个点) 表示多个参数
     */
    @Pointcut("execution(public * cn.matcheasy..controller.*.*(..))"
            + " || execution(public * cn.matcheasy..api.*.*(..))"
            + " || execution(public * cn.matcheasy..rest.*.*(..))")
    public void doOperation()
    {
    }

    @Before("doOperation()")
    //@Before(value="execution(* cn.matcheasy.project..*.*(..))")
    public void doBefore(JoinPoint joinPoint) throws Throwable
    {
        String className = joinPoint.getSignature().getDeclaringTypeName();//获取代理类的名字
        String methodName = joinPoint.getSignature().getName();//获取代理方法的名字
        MatcheasyLog.info(StrUtil.format("请求接口: {}", className + "." + methodName + "()"));
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (ObjectUtil.isNotEmpty(servletRequestAttributes))
        {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            MatcheasyLog.info(StrUtil.format("请求URL: {}", request.getRequestURL()));
            MatcheasyLog.info(StrUtil.format("请求TYPE: {}", request.getMethod()));
            MatcheasyLog.info(StrUtil.format("请求Header/Query参数: {}", getRequstParams(request)));
            MatcheasyLog.info(StrUtil.format("请求Body参数: {}", getBodyParams(joinPoint)));

            //HttpSession session = (HttpSession) servletRequestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
            //log.info("SESSION ID：" + session.getId());
        }
    }

    @After("doOperation()")
    //@After(value="execution(* cn.matcheasy.project..*.*(..))")
    public void doAfter(JoinPoint joinPoint)
    {
    }

    @AfterReturning(returning = "result", pointcut = "doOperation()")
    //@AfterReturning(value="execution(* cn.matcheasy.project..*.*(..))" ,returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Object result)
    {
    }

    @Around("doOperation()")
    // @Around(value="execution(* cn.matcheasy.project..*.*(..))")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable, Exception
    {
        //long start = System.currentTimeMillis();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object proceed = null;
        try
        {
            Object[] args = joinPoint.getArgs();
            proceed = joinPoint.proceed(args);
            String resultStr = JSON.toJSONString(proceed);
            MatcheasyLog.info(StrUtil.format("响应结果: {}", resultStr));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        //long end = System.currentTimeMillis();
        stopWatch.stop();
        MatcheasyLog.info(StrUtil.format("执行时间: {}", stopWatch.getTotalTimeMillis() / 1000 + "s"));
        return proceed;
    }

    //@AfterThrowing(throwing = "e", pointcut = "doOperation()")
    //@AfterThrowing(value="execution(* cn.matcheasy.project..*.*(..))", throwing = "e")
    //public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
    //}

    private String getRequstParams(HttpServletRequest request)
    {
        return JSON.toJSONString(ComU.getRequestMap(request));
    }

    private String getBodyParams(JoinPoint joinPoint)
    {
        StringBuffer params = new StringBuffer();
        if (joinPoint.getArgs() != null && joinPoint.getArgs().length > 0)
        {
            int size = joinPoint.getArgs().length;
            for (int i = 0; i < size; i++)
            {
                Object arg = joinPoint.getArgs()[i];
                if ((arg instanceof HttpServletResponse) || (arg instanceof HttpServletRequest)
                        || (arg instanceof MultipartFile) || (arg instanceof MultipartFile[]))
                {
                    continue;
                }
                try
                {
                    params.append(JSONObject.toJSONString(arg));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        return params.toString();
    }

}