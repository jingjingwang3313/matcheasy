package cn.matcheasy.project.modules.demo.es.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import cn.matcheasy.framework.constant.EsIndexConst;
import cn.matcheasy.framework.constant.ProjectConst;
import cn.matcheasy.framework.result.Result;
import cn.matcheasy.project.modules.demo.es.service.EsService;
import cn.matcheasy.project.modules.sysuser.entity.SysUser;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @class: EsController
 * @author: wangjing
 * @date: 2021/5/21/0021
 * @desc: ES数据操作
 */
@Slf4j
@RestController
@RequestMapping("/anon/es/data")
public class EsDataCtl
{
    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    @Qualifier("restHighLevelClient2")
    private RestHighLevelClient restHighLevelClient2;

    @Autowired
    private EsService esService;

    /**
     * 获取es信息
     */
    @GetMapping("/info")
    public Result getEsInfo() throws Exception
    {
        Map resultMap = new HashMap();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(searchSourceBuilder);
        //查询ES
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        resultMap.put(ProjectConst.RESULT, searchResponse);
        return Result.success(resultMap);
    }

    /**
     * 获取es信息
     */
    @GetMapping("/info2")
    public Result getEsInfo2() throws Exception
    {
        Map resultMap = new HashMap();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(searchSourceBuilder);
        //查询ES
        SearchResponse searchResponse = restHighLevelClient2.search(searchRequest, RequestOptions.DEFAULT);
        resultMap.put(ProjectConst.RESULT, searchResponse);
        return Result.success(resultMap);
    }

    /**
     * 获取文档信息
     */
    @GetMapping("/getDoc/{id}")
    public Result getDoc(@PathVariable("id") String id) throws Exception
    {
        Map resultMap = new HashMap();
        // 获取请求对象
        GetRequest getRequest = new GetRequest(EsIndexConst.SYS_USER, id);
        // 获取文档信息
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        // 将 JSON 转换成对象
        if (getResponse.isExists())
        {
            SysUser userInfo = JSON.parseObject(getResponse.getSourceAsBytes(), SysUser.class);
            log.info("员工信息：{}", userInfo);
            resultMap.put(ProjectConst.RESULT, userInfo);
        }
        return Result.success(resultMap);
    }

    /**
     * 添加文档信息
     */
    @PostMapping("/addDoc")
    public Result addDoc() throws Exception
    {
        SysUser user = new SysUser();
        user.setUserId(RandomUtil.randomLong(1, 9999));
        user.setEmail("33135@qq.com");
        user.setLoginCode("test");
        user.setPassword("123456");
        user.setUserName("阿尔法狗");
        user.setPhone("18340088315");
        user.setCreateTime(new Date());
        IndexRequest indexRequest = new IndexRequest(EsIndexConst.SYS_USER)
                .id(user.getUserId().toString())
                .source(JSONUtil.toJsonStr(user), XContentType.JSON);
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        log.info("状态：{}", indexResponse.status());
        if (RestStatus.CREATED.equals(indexResponse.status()))
        {
            return Result.success("保存成功");
        }
        else
        {
            return Result.error("保存失败");
        }
    }

    /**
     * 更新文档信息
     */
    @PutMapping("/updateDoc/{id}")
    public Result updateDoc(@PathVariable("id") String id) throws Exception
    {
        // 创建索引请求对象
        UpdateRequest updateRequest = new UpdateRequest(EsIndexConst.SYS_USER, id);
        // 设置员工更新信息
        SysUser userInfo = new SysUser();
        userInfo.setUserName("测试修改");
        userInfo.setEmail("ceshi123123@qq.com");
        // 设置更新文档内容
        updateRequest.doc(JSONUtil.toJsonStr(userInfo), XContentType.JSON);
        // 执行更新文档
        UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        log.info("状态：{}", updateResponse.status());
        if (RestStatus.CREATED.equals(updateResponse.status()))
        {
            return Result.success("更新成功!");
        }
        else
        {
            return Result.error("更新失败!");
        }
    }

    /**
     * 删除文档信息
     */
    @DeleteMapping("/deleteDoc/{id}")
    public Result deleteDoc(@PathVariable("id") String id) throws Exception
    {
        // 创建删除请求对象
        DeleteRequest deleteRequest = new DeleteRequest(EsIndexConst.SYS_USER, id);
        // 执行删除文档
        DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        log.info("状态：{}", deleteResponse.status());
        if (RestStatus.OK.equals(deleteResponse.status()))
        {
            return Result.success("删除成功!");
        }
        else
        {
            return Result.error("删除失败!");
        }
    }

    @GetMapping("/testDao")
    public Result testDao() throws Exception
    {
        esService.insert();
        return Result.success("成功!");
    }

}
