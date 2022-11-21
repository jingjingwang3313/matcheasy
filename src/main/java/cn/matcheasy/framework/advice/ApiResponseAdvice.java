package cn.matcheasy.framework.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @class ApiResponseAdvice
 * @author: wangjing
 * @date: 2020/11/6/0006
 * @desc: 对响应参数操作
 */
@Slf4j
@ControllerAdvice(basePackages = {"cn.matcheasy"})//basePackages设置需要当前Advice执行的域,省略默认全局生效
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    /**
     * 此处如果返回false,则不执行当前Advice的业务
     * @param returnType
     * @param converterType
     * @return
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        //选择哪些类或哪些方法处理
        if (returnType.getMethod().getDeclaringClass().getName().contains("test")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 返回参数之前操作
     * @param obj
     * @param methodParameter
     * @param selectedContentType
     * @param selectedConverterType
     * @param serverHttpRequest
     * @param serverHttpResponse
     * @return
     */
    @Override
    public Object beforeBodyWrite(Object obj, MethodParameter methodParameter, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {
        //通过 ServerHttpRequest的实现类ServletServerHttpRequest 获得HttpServletRequest
        ServletServerHttpRequest httpRequest = (ServletServerHttpRequest) serverHttpRequest;
        //此处获取到request是为了取到在拦截器里面设置的一个对象
        HttpServletRequest request = httpRequest.getServletRequest();
        return obj;
    }

}