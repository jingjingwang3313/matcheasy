package cn.matcheasy.project.common.retrofit;

import cn.matcheasy.framework.annotation.Sign;
import cn.matcheasy.framework.result.Result;
import cn.matcheasy.project.modules.sysuser.entity.SysUser;
import com.github.lianjiatech.retrofit.spring.boot.annotation.RetrofitClient;
import com.github.lianjiatech.retrofit.spring.boot.interceptor.BaseLoggingInterceptor;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.slf4j.event.Level;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

/**
 * @class: RetrofitHttpClient
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: https://blog.csdn.net/why_still_confused/article/details/108041657
 *
 * 通过@RetrofitClient的enableLog控制是否开启，通过logLevel和logStrategy，可以指定每个接口的日志打印级别以及日志打印策略
 * 支持了5种日志打印级别(ERROR, WARN, INFO, DEBUG, TRACE)，默认INFO
 * 支持了4种日志打印策略（NONE, BASIC, HEADERS, BODY），默认BASIC
 * 1. NONE：No logs.
 * 2. BASIC：Logs request and response lines.
 * 3. HEADERS：Logs request and response lines and their respective headers.
 * 4. BODY：Logs request and response lines and their respective headers and bodies (if present).
 */
@RetrofitClient(baseUrl = "${retrofit.baseUrl.server}", poolName = "retrofitPool", connectTimeoutMs = 30000, readTimeoutMs = 30000, writeTimeoutMs = 30000, enableLog = true, logLevel = Level.INFO, logStrategy = BaseLoggingInterceptor.LogStrategy.BODY)
//@Intercept(handler = TimeStampInterceptor.class, include = {"/api/list/**"}, exclude = "/api/test/**")
@Sign(access_key = "${retrofit.access_key}", access_secret = "${retrofit.access_secret}", include = {"/anon/**"}, exclude = "/anon/test/**")
public interface RetrofitHttpClient {

    // 查询参数的设置 @Query
    @GET("/anon/list")
    List<Map> list();

    @GET("/anon/test")
    Map test(@Header("authorization") String authorization);

    @POST("/anon/getList")
    List<Map> retrofit(@Query("id") Long id, @QueryMap Map<String, Object> paramMap, @Body SysUser user);

    @POST("anon/uploadFile")
    @Multipart
    Result upload(@PartMap Map<String, RequestBody> params);

    // 接口示例================================================================================


    // 动态的url访问 @PATH
    @GET("{username}")
    Call<SysUser> getUser(@Path("username") String username);

    // POST请求体的方式向服务器传入json字符串 @Body
    @POST("add")
    Call<List<SysUser>> addUser(@Body SysUser User);

    // FormBody,以表单的方式传递简单的键值对
    // 表单的方式传递键值对 @FormUrlEncoded
    @POST("login")
    @FormUrlEncoded
    Call<SysUser> login(@Field("username") String username, @Field("password") String password);


    // MultipartBody,以POST表单的方式上传文件可以携带参数
    // 单文件上传 @Multipart , 每个Part对应一个RequestBody 。
    @POST("register")
    @Multipart
    Call<SysUser> registerUser(@Part MultipartBody.Part file, @Part("username") RequestBody username, @Part("password") RequestBody password);

    // MultipartBody,以POST表单的方式上传文件可以携带参数
    // 多文件上传 @PartMap,Map的key为String类型，代表上传的键值对的key(与服务器接受的key对应),value即为RequestBody
    @POST("uploadFiles")
    @Multipart
    Call<SysUser> uploadFiles(@PartMap Map<String, RequestBody> params, @Part("password") RequestBody password);

}
