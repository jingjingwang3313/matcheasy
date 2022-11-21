package cn.matcheasy.framework.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @class ApiRequestAdvice
 * @author: wangjing
 * @date: 2020/11/6/0006
 * @desc: 对请求参数操作
 */
@Slf4j
@ControllerAdvice(basePackages = {"cn.matcheasy"})//basePackages设置需要当前Advice执行的域,省略默认全局生效
public class ApiRequestAdvice implements RequestBodyAdvice {

    /**
     * 此处如果返回false,则不执行当前Advice的业务
     * @param methodParameter
     * @param targetType
     * @param converterType
     * @return
     */
    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        //选择哪些类或哪些方法处理
        if (methodParameter.getMethod().getDeclaringClass().getName().contains("test")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 读取请求体参数前执行
     * @param inputMessage
     * @param methodParameter
     * @param targetType
     * @param selectedConverterType
     * @return
     * @throws IOException
     */
    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> selectedConverterType) throws IOException {
        return inputMessage;
    }

    /**
     * 读取请求体参数后执行
     * @param body
     * @param inputMessage
     * @param parameter
     * @param targetType
     * @param converterType
     * @return
     */
    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    /**
     * 无请求体时的处理
     * @param var1
     * @param var2
     * @param var3
     * @param var4
     * @param var5
     * @return
     */
    @Override
    public Object handleEmptyBody(@Nullable Object var1, HttpInputMessage var2, MethodParameter var3, Type var4, Class<? extends HttpMessageConverter<?>> var5) {
        return var1;
    }

}