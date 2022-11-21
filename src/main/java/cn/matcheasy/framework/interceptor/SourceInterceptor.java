package cn.matcheasy.framework.interceptor;

import cn.hutool.core.map.MapUtil;
import cn.matcheasy.framework.constant.ProjectConst;
import cn.matcheasy.framework.utils.ComU;
import com.github.lianjiatech.retrofit.spring.boot.interceptor.BaseGlobalInterceptor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * @class SourceInterceptor
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: Retrofit全局http请求执行统一的拦截处理
 */
@Slf4j
@Component
public class SourceInterceptor extends BaseGlobalInterceptor
{

    @Value("${retrofit.source}")
    private String source;

    @Override
    public Response doIntercept(Chain chain) throws IOException
    {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        Map<String, Object> requestMap = ComU.getRequestMap(request);
        return chain.proceed(chain.request().newBuilder()
                .addHeader(ProjectConst.SOURCE, source)
                .addHeader(ProjectConst.ACCESS_TOKEN, MapUtil.getStr(requestMap, ProjectConst.ACCESS_TOKEN))
                .build());
    }

}
