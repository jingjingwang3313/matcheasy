package cn.matcheasy.project.common.api.dwz.controller;

import cn.hutool.core.map.MapUtil;
import cn.matcheasy.framework.result.Result;
import cn.matcheasy.framework.utils.ComU;
import cn.matcheasy.project.common.api.dwz.service.DwzService;
import cn.yueshutong.springbootstartercurrentlimiting.annotation.CurrentLimiter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @class: DwzCtl
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
@Api(tags = "短链接")
@Slf4j
@Controller
@RequestMapping("/dwz")
public class DwzCtl {

    @Value("${server.port}")
    private String port;

    @Autowired
    private DwzService dwzService;

    @GetMapping("/toDwz")
    public String toDWZ() {
        log.info("跳转到 templates 目录下的 dwz.html");
        return "dwz";
    }

    /**
     * 获取短链接
     */
    @ApiOperation(value = "转换短链接")
    @ApiImplicitParam(name = "long_url", value = "请求转换的长URL", required = true, dataType = "String")
    @CrossOrigin //指定方法接收跨域请求
    @CurrentLimiter(QPS = 100) //接口每秒QPS
    @GetMapping(value = "/getDWZ")
    @ResponseBody
    public Result getDWZ(HttpServletRequest request) throws Exception {
        Map retMap = new HashMap();
        HashMap requestMap = (HashMap) ComU.getRequestMap(request);
        String long_url = MapUtil.getStr(requestMap, "long_url");
        log.info("请求的 long_url: 【{}】", long_url);
        if (StringUtils.isEmpty(long_url)) {
            return Result.error("请求转换的 long_url 不能为空!");
        }
        if (!StringUtils.startsWith(long_url, "http")) {
            //long_url = "http://" + long_url;
            return Result.error("请求转换的 long_url 协议(http 或 https)不正确!");
        }
        String short_url = "";
        List<Map> dwzList = dwzService.selectDwzByCodeOrLongUrl(requestMap);
        if (!CollectionUtils.isEmpty(dwzList)) {
            // 存在
            short_url = MapUtil.getStr(dwzList.get(0), "short_url");
        } else {
            // 不存在,生成短链接
            String api_code = ComU.getRandom(8, 4);
            requestMap.put("api_code", api_code);
            while (!CollectionUtils.isEmpty(dwzService.selectDwzByCodeOrLongUrl(requestMap))) {
                api_code = ComU.getRandom(8, 4);
                requestMap.put("api_code", api_code);
            }
            if (StringUtils.equals(port, "80")) {
                short_url = request.getScheme() + "://" + request.getServerName() + "/dwz/" + api_code;
            } else {
                short_url = request.getScheme() + "://" + request.getServerName() + ":" + port + "/dwz/" + api_code;
            }
            requestMap.put("short_url", short_url);
            requestMap.put("call_count", "0");
            requestMap.put("status", "1");
            dwzService.insertDWZ(requestMap);
        }
        log.info("生成的 short_url: 【{}】", short_url);
        retMap.put("long_url", long_url);
        retMap.put("short_url", short_url);
        return Result.success(retMap);
    }

    /**
     * 访问短链接
     */
    @ApiOperation(value = "访问短链接")
    @CrossOrigin //指定方法接收跨域请求
    @CurrentLimiter(QPS = 100) //接口每秒QPS
    @GetMapping(value = "/{api_code}")
    @ResponseBody
    public void accessDWZ(HttpServletResponse response, @PathVariable(value = "api_code", required = true) String api_code) throws Exception {
        log.info("生成短链接码 api_code: 【{}】", api_code);
        String longUrl = "";
        HashMap paramMap = new HashMap();
        paramMap.put("api_code", api_code);
        List<Map> dwzList = dwzService.selectDwzByCodeOrLongUrl(paramMap);
        if (!CollectionUtils.isEmpty(dwzList)) {
            longUrl = MapUtil.getStr(dwzList.get(0), "long_url");
            dwzService.increaseCallCount(paramMap);
        } else {
            throw new Exception("不可访问的短链接!");
        }
        log.info("要访问的 long_url: 【{}】", longUrl);
        response.sendRedirect(longUrl);
    }

}
