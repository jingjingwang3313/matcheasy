package cn.matcheasy.project.modules.demo.retrofit;

import cn.matcheasy.framework.constant.ProjectConst;
import cn.matcheasy.framework.log.MatcheasyLog;
import cn.matcheasy.framework.result.Result;
import cn.matcheasy.framework.utils.ComU;
import cn.matcheasy.project.common.retrofit.RetrofitHttpClient;
import cn.matcheasy.project.modules.sysuser.entity.SysUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * @class: RetrofitHttpCtl
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
@Slf4j
@Api(tags = "Retrofit HTTP 请求示例")
@RestController
@RequestMapping("/anon")
public class RetrofitHttpCtl
{

    @Autowired
    private RetrofitHttpClient retrofitHttpClient;

    @ApiOperation(value = "Retrofit发送http请求")
    @PostMapping("/retrofit")
    public Result retrofit(HttpServletRequest request) throws Exception
    {
        Map returnMap = new HashMap();
        Map paramMap = new HashMap();
        paramMap.put("name", "姓名");
        paramMap.put("age", "年龄");
        paramMap.put("address", "地址");
        SysUser user = new SysUser();
        user.setUserName("username");
        user.setPassword("123456");
        List<Map> list = retrofitHttpClient.retrofit(123L, paramMap, user);

        MatcheasyLog.info(list.toString());
        returnMap.put(ProjectConst.RESULT, list);
        return Result.success(returnMap);
    }

    @PostMapping("/getList")
    public List<Map> getList(HttpServletRequest request, @RequestBody SysUser user) throws Exception
    {
        Map paramMap = ComU.getRequestMap(request);
        paramMap.put("user", user);
        MatcheasyLog.info(paramMap.toString());
        List<Map> list = new ArrayList<Map>();
        list.add(paramMap);
        return list;
    }

    @ApiOperation(value = "Retrofit上传文件")
    @PostMapping("/upload")
    public Result upload(HttpServletRequest request) throws Exception
    {
        Map map = new HashMap();

        File file = new File("D:/Tencent/QQdoc/aaa.xlsx");
        okhttp3.RequestBody fileBody = okhttp3.RequestBody.create(MediaType.parse("mutipart/form-data"), file);
        map.put("file\"; filename=\"" + file.getName(), fileBody);

        File file2 = new File("D:/Tencent/QQdoc/asktrue.txt");
        okhttp3.RequestBody fileBody2 = okhttp3.RequestBody.create(MediaType.parse("mutipart/form-data"), file2);
        map.put("file2\"; filename=\"" + file2.getName(), fileBody2);

        File file3 = new File("D:/Tencent/QQdoc/redis清缓存时机.txt");
        okhttp3.RequestBody fileBody3 = okhttp3.RequestBody.create(MediaType.parse("mutipart/form-data"), file3);
        map.put("file3\"; filename=\"" + URLEncoder.encode(file3.getName(), "UTF-8"), fileBody3);//中文名需编码

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("name", "姓名");
        paramMap.put("age", "年龄");
        paramMap.put("address", "地址");
        for (String key : paramMap.keySet())
        {
            map.put(key, okhttp3.RequestBody.create(MediaType.parse("text/plain"), String.valueOf(paramMap.get(key))));
        }

        return retrofitHttpClient.upload(map);
    }

    @PostMapping("/uploadFile")
    public Result uploadFile(HttpServletRequest request) throws Exception
    {
        Map paramMap = ComU.getRequestMap(request);
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        // 上传文件name不同,获取多个文件名
        Iterator<String> it = multiRequest.getFileNames();
        if (!it.hasNext())
        {
            return Result.error("请选择上传文件!");
        }
        while (it.hasNext())
        {
            // 根据文件名获取文件
            MultipartFile multipartFile = multiRequest.getFile(it.next());
            String fileName = multipartFile.getOriginalFilename();
            MatcheasyLog.info(URLDecoder.decode(fileName, "UTF-8"));
        }
        return Result.success(paramMap);
    }

}
