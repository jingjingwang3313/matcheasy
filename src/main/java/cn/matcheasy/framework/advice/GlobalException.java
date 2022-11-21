package cn.matcheasy.framework.advice;

import cn.hutool.core.util.StrUtil;
import cn.matcheasy.framework.exception.BusinessException;
import cn.matcheasy.framework.log.MatcheasyLog;
import cn.matcheasy.framework.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @class GlobalException
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
@Slf4j
@RestControllerAdvice(basePackages = {"cn.matcheasy"})
public class GlobalException
{
    /**
     * 权限异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = AuthorizationException.class)
    public Result authorizationException(HttpServletRequest req, AuthorizationException e)
    {
        String apiName = StrUtil.trim(req.getRequestURI());
        MatcheasyLog.error(StrUtil.format("{}_接口访问异常!", apiName), e);
        return Result.error("AuthorizationException异常！原因是: " + e.getMessage());
    }

    /**
     * Shiro异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = ShiroException.class)
    public Result shiroException(HttpServletRequest req, ShiroException e)
    {
        String apiName = StrUtil.trim(req.getRequestURI());
        MatcheasyLog.error(StrUtil.format("{}_接口访问异常!", apiName), e);
        return Result.error("ShiroException异常！原因是: " + e.getMessage());
    }

    /**
     * 运行期异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = RuntimeException.class)
    public Result runtimeException(HttpServletRequest req, RuntimeException e)
    {
        String apiName = StrUtil.trim(req.getRequestURI());
        MatcheasyLog.error(StrUtil.format("{}_接口访问异常!", apiName), e);
        return Result.error("RuntimeException异常！原因是: " + e.getMessage());
    }

    /**
     * 参数校验异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = {MethodArgumentNotValidException.class, BindException.class})
    public Result bindException(HttpServletRequest req, MethodArgumentNotValidException e)
    {
        String apiName = StrUtil.trim(req.getRequestURI());
        MatcheasyLog.error(StrUtil.format("{}_接口访问异常!", apiName), e);
        BindingResult result = e.getBindingResult();
        if (result.hasErrors())
        {
            FieldError fieldError = result.getFieldError();
            if (fieldError != null)
            {
                return Result.error("MethodArgumentNotValidException异常！原因是: " + fieldError.getDefaultMessage());
            }
        }
        return Result.error("MethodArgumentNotValidException异常！原因是: " + e.getMessage());
    }

    /**
     * 业务异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = {BusinessException.class})
    public Result businessException(HttpServletRequest req, BusinessException e)
    {
        String apiName = StrUtil.trim(req.getRequestURI());
        MatcheasyLog.error(StrUtil.format("{}_接口访问异常!", apiName), e);
        return Result.error("BusinessException异常！原因是: " + e.getMessage());
    }

    /**
     * 其他异常
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public Result exception(HttpServletRequest req, Exception e)
    {
        String apiName = StrUtil.trim(req.getRequestURI());
        MatcheasyLog.error(StrUtil.format("{}_接口访问异常!", apiName), e);
        return Result.error("Exception异常！原因是: " + e.getMessage());
    }

}