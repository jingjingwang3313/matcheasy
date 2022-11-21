package cn.matcheasy.framework.interceptor;

import com.github.lianjiatech.retrofit.spring.boot.interceptor.BasePathMatchInterceptor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @class TimeStampInterceptor
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: Retrofit请求执行统一的拦截处理逻辑
 */
@Slf4j
@Component
public class TimeStampInterceptor extends BasePathMatchInterceptor {

    @Override
    public Response doIntercept(Chain chain) throws IOException {
        Request newRequest = chain.request().newBuilder()
                .url(chain.request().url().newBuilder()
                        .addQueryParameter("timestamp", String.valueOf(System.currentTimeMillis()))
                        .build())
                .build();
        return chain.proceed(newRequest);
    }

}
