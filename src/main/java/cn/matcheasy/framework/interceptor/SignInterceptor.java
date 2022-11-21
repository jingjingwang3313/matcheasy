package cn.matcheasy.framework.interceptor;

import cn.matcheasy.framework.constant.ProjectConst;
import com.github.lianjiatech.retrofit.spring.boot.interceptor.BasePathMatchInterceptor;
import lombok.Data;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @class: SignInterceptor
 * @author: wangjing
 * @date: 2021/2/20/0020
 * @desc: 在指定url的请求上, 拦截注解动态传入一些参数
 * <p>
 * 接口上使用@Sign就能在指定url的请求上，自动加上签名信息.
 * <p>
 * accessKeyId和accessKeySecret字段值会依据@Sign注解的accessKeyId()和accessKeySecret()值自动注入
 * 如果@Sign指定的是占位符形式的字符串，则会取配置属性值进行注入
 * accessKeyId和accessKeySecret字段必须提供setter方法(加注解 @Data)
 */
@Data
@Component
public class SignInterceptor extends BasePathMatchInterceptor
{

    private String access_key;

    private String access_secret;

    @Override
    public Response doIntercept(Chain chain) throws IOException
    {
        Request request = chain.request();
        Request newReq = request.newBuilder()
                .addHeader(ProjectConst.ACCESS_KEY, access_key)
                .addHeader(ProjectConst.ACCESS_SECRET, access_secret)
                .build();
        return chain.proceed(newReq);
    }

}
