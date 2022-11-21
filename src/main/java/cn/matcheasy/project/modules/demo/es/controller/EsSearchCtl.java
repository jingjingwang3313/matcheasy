package cn.matcheasy.project.modules.demo.es.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import cn.matcheasy.framework.constant.EsIndexConst;
import cn.matcheasy.framework.constant.ProjectConst;
import cn.matcheasy.framework.result.Result;
import cn.matcheasy.framework.utils.ComU;
import cn.matcheasy.project.common.base.controller.BaseController;
import cn.matcheasy.project.modules.sysuser.entity.SysUser;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @class: EsSearchCtl
 * @author: wangjing
 * @date: 2021/5/25/0025
 * @desc: TODO
 */
@Slf4j
@RestController
@RequestMapping("/anon/es/search")
public class EsSearchCtl extends BaseController
{
    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    @Qualifier("restHighLevelClient2")
    private RestHighLevelClient restHighLevelClient2;

    /**
     * 条件分页查询
     */
    @GetMapping("/matchQueryPage")
    public Result matchQueryPage(HttpServletRequest request) throws Exception
    {
        Map resultMap = new HashMap();
        // 构建查询条件
        QueryBuilder qb1 = QueryBuilders.wildcardQuery("name", "*模糊查询*");
        QueryBuilder qb2 = QueryBuilders.termQuery("mobile", "18340088315");
        QueryBuilder qb3 = QueryBuilders.rangeQuery("createTime").format("yyyy-MM-dd HH:mm:ss").gte("2020-05-09 14:35:40").lte("2020-05-30 14:35:50");
        QueryBuilder qb = QueryBuilders.boolQuery().must(qb1).must(qb2).filter(qb3);
        // 创建查询源构造器,设置分页
        Long pageNum = getPageNum(MapUtil.getLong(ComU.getRequestMap(request), "pageNum"));
        Long pageSize = getPageSize(MapUtil.getLong(ComU.getRequestMap(request), "pageSize"));
        SearchSourceBuilder searchSourceBuilder = startEsPage(pageNum, pageSize);
        searchSourceBuilder.query(qb);
        // 设置排序
        searchSourceBuilder.sort("createTime", SortOrder.DESC);
        // 创建查询请求对象
        SearchRequest searchRequest = getSearchRequest(EsIndexConst.SYS_USER);
        searchRequest.source(searchSourceBuilder);
        // 执行查询,处理响应结果
        SearchResponse searchResponse = getSearchResponse(restHighLevelClient, searchRequest);
        List list = getEsSearchResult(searchResponse, SysUser.class);
        if (CollectionUtils.isNotEmpty(list))
        {
            Long totalCount = searchResponse.getHits().getTotalHits().value;
            resultMap.put(ProjectConst.RESULT, returnEsPage(pageNum, pageSize, totalCount, list));
        }
        return Result.success(resultMap);
    }

    /**
     * 条件统计
     */
    @GetMapping("/aggregationStats")
    public Result aggregationStats(HttpServletRequest request) throws Exception
    {
        // 查询条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder qb1 = QueryBuilders.wildcardQuery("name", "*模糊查询*");
        QueryBuilder qb2 = QueryBuilders.termQuery("mobile", "18340088315");
        QueryBuilder qb3 = QueryBuilders.rangeQuery("createTime").format("yyyy-MM-dd HH:mm:ss").gte("2020-05-09 14:35:40").lte("2020-05-30 14:35:50");
        QueryBuilder qb = QueryBuilders.boolQuery().must(qb1).must(qb2).filter(qb3);
        searchSourceBuilder.query(qb);

        AggregationBuilder aggr = AggregationBuilders.stats("stats").field("billAmount");
        searchSourceBuilder.aggregation(aggr);
        searchSourceBuilder.size(0);
        SearchRequest searchRequest = new SearchRequest(EsIndexConst.SYS_USER);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        Aggregations aggregations = response.getAggregations();
        if (RestStatus.OK.equals(response.status()) || aggregations != null)
        {
            ParsedStats aggregation = aggregations.get("stats");
            log.info("聚合信息:");
            log.info("count：" + aggregation.getCount());
            log.info("avg：" + aggregation.getAvg());
            log.info("max：" + aggregation.getMax());
            log.info("min：" + aggregation.getMin());
            log.info("sum：" + aggregation.getSum());
            return Result.success(Dict.create().set(ProjectConst.RESULT, aggregation));
        }
        return Result.error("统计错误");
    }

    //  --------------------------------1、精确查询(term)-----------------------------------------

    /**
     * 精确查询（查询条件不会进行分词，但是查询内容可能会分词，导致查询不到）
     */
    public void termQuery()
    {
        try
        {
            // 构建查询条件（注意：termQuery 支持多种格式查询，如 boolean、int、double、string 等，这里使用的是 string 的查询）
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.termQuery("address.keyword", "北京市通州区"));
            // 创建查询请求对象，将查询对象配置到其中
            SearchRequest searchRequest = new SearchRequest("mydlq-user");
            searchRequest.source(searchSourceBuilder);
            // 执行查询，然后处理响应结果
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 根据状态和数据条数验证是否返回了数据
            if (RestStatus.OK.equals(searchResponse.status()) && searchResponse.getHits().getTotalHits().value > 0)
            {
                SearchHits hits = searchResponse.getHits();
                for (SearchHit hit : hits)
                {
                    // 将 JSON 转换成对象
                    SysUser userInfo = JSON.parseObject(hit.getSourceAsString(), SysUser.class);
                    // 输出查询信息
                    log.info(userInfo.toString());
                }
            }
        }
        catch (IOException e)
        {
            log.error("", e);
        }
    }

    /**
     * 多个内容在一个字段中进行查询
     */
    public void termsQuery()
    {
        try
        {
            // 构建查询条件（注意：termsQuery 支持多种格式查询，如 boolean、int、double、string 等，这里使用的是 string 的查询）
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.termsQuery("address.keyword", "北京市丰台区", "北京市昌平区", "北京市大兴区"));
            // 创建查询请求对象，将查询对象配置到其中
            SearchRequest searchRequest = new SearchRequest("mydlq-user");
            searchRequest.source(searchSourceBuilder);
            // 执行查询，然后处理响应结果
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 根据状态和数据条数验证是否返回了数据
            if (RestStatus.OK.equals(searchResponse.status()) && searchResponse.getHits().getTotalHits().value > 0)
            {
                SearchHits hits = searchResponse.getHits();
                for (SearchHit hit : hits)
                {
                    // 将 JSON 转换成对象
                    SysUser userInfo = JSON.parseObject(hit.getSourceAsString(), SysUser.class);
                    // 输出查询信息
                    log.info(userInfo.toString());
                }
            }
        }
        catch (IOException e)
        {
            log.error("", e);
        }
    }

    //  --------------------------------2、匹配查询(match)-----------------------------------------

    /**
     * 匹配查询符合条件的所有数据，并设置分页
     */
    public void matchAllQuery()
    {
        try
        {
            // 构建查询条件
            MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
            // 创建查询源构造器
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(matchAllQueryBuilder);
            // 设置分页
            searchSourceBuilder.from(0);
            searchSourceBuilder.size(3);
            // 设置排序
            searchSourceBuilder.sort("salary", SortOrder.ASC);
            // 创建查询请求对象，将查询对象配置到其中
            SearchRequest searchRequest = new SearchRequest("mydlq-user");
            searchRequest.source(searchSourceBuilder);
            // 执行查询，然后处理响应结果
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 根据状态和数据条数验证是否返回了数据
            if (RestStatus.OK.equals(searchResponse.status()) && searchResponse.getHits().getTotalHits().value > 0)
            {
                SearchHits hits = searchResponse.getHits();
                for (SearchHit hit : hits)
                {
                    // 将 JSON 转换成对象
                    SysUser userInfo = JSON.parseObject(hit.getSourceAsString(), SysUser.class);
                    // 输出查询信息
                    log.info(userInfo.toString());
                }
            }
        }
        catch (IOException e)
        {
            log.error("", e);
        }
    }

    /**
     * 匹配查询数据
     */
    public void matchQuery()
    {
        try
        {
            // 构建查询条件
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchQuery("address", "*通州区"));
            // 创建查询请求对象，将查询对象配置到其中
            SearchRequest searchRequest = new SearchRequest("mydlq-user");
            searchRequest.source(searchSourceBuilder);
            // 执行查询，然后处理响应结果
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 根据状态和数据条数验证是否返回了数据
            if (RestStatus.OK.equals(searchResponse.status()) && searchResponse.getHits().getTotalHits().value > 0)
            {
                SearchHits hits = searchResponse.getHits();
                for (SearchHit hit : hits)
                {
                    // 将 JSON 转换成对象
                    SysUser userInfo = JSON.parseObject(hit.getSourceAsString(), SysUser.class);
                    // 输出查询信息
                    log.info(userInfo.toString());
                }
            }
        }
        catch (IOException e)
        {
            log.error("", e);
        }
    }

    /**
     * 词语匹配查询
     */
    public void matchPhraseQuery()
    {
        try
        {
            // 构建查询条件
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchPhraseQuery("address", "北京市通州区"));
            // 创建查询请求对象，将查询对象配置到其中
            SearchRequest searchRequest = new SearchRequest("mydlq-user");
            searchRequest.source(searchSourceBuilder);
            // 执行查询，然后处理响应结果
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 根据状态和数据条数验证是否返回了数据
            if (RestStatus.OK.equals(searchResponse.status()) && searchResponse.getHits().getTotalHits().value > 0)
            {
                SearchHits hits = searchResponse.getHits();
                for (SearchHit hit : hits)
                {
                    // 将 JSON 转换成对象
                    SysUser userInfo = JSON.parseObject(hit.getSourceAsString(), SysUser.class);
                    // 输出查询信息
                    log.info(userInfo.toString());
                }
            }
        }
        catch (IOException e)
        {
            log.error("", e);
        }
    }

    /**
     * 内容在多字段中进行查询
     */
    public void matchMultiQuery()
    {
        try
        {
            // 构建查询条件
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.multiMatchQuery("北京市", "address", "remark"));
            // 创建查询请求对象，将查询对象配置到其中
            SearchRequest searchRequest = new SearchRequest("mydlq-user");
            searchRequest.source(searchSourceBuilder);
            // 执行查询，然后处理响应结果
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 根据状态和数据条数验证是否返回了数据
            if (RestStatus.OK.equals(searchResponse.status()) && searchResponse.getHits().getTotalHits().value > 0)
            {
                SearchHits hits = searchResponse.getHits();
                for (SearchHit hit : hits)
                {
                    // 将 JSON 转换成对象
                    SysUser userInfo = JSON.parseObject(hit.getSourceAsString(), SysUser.class);
                    // 输出查询信息
                    log.info(userInfo.toString());
                }
            }
        }
        catch (IOException e)
        {
            log.error("", e);
        }
    }

    //  --------------------------------3、模糊查询(fuzzy)-----------------------------------------

    /**
     * 模糊查询所有以 “三” 结尾的姓名
     */
    public void fuzzyQuery()
    {
        try
        {
            // 构建查询条件
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.fuzzyQuery("name", "三").fuzziness(Fuzziness.AUTO));
            // 创建查询请求对象，将查询对象配置到其中
            SearchRequest searchRequest = new SearchRequest("mydlq-user");
            searchRequest.source(searchSourceBuilder);
            // 执行查询，然后处理响应结果
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 根据状态和数据条数验证是否返回了数据
            if (RestStatus.OK.equals(searchResponse.status()) && searchResponse.getHits().getTotalHits().value > 0)
            {
                SearchHits hits = searchResponse.getHits();
                for (SearchHit hit : hits)
                {
                    // 将 JSON 转换成对象
                    SysUser userInfo = JSON.parseObject(hit.getSourceAsString(), SysUser.class);
                    // 输出查询信息
                    log.info(userInfo.toString());
                }
            }
        }
        catch (IOException e)
        {
            log.error("", e);
        }
    }

    //  --------------------------------4、范围查询(range)-----------------------------------------

    /**
     * 查询岁数 ≥ 30 岁的员工数据
     */
    public void rangeQuery()
    {
        try
        {
            // 构建查询条件
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.rangeQuery("age").gte(30));
            // 创建查询请求对象，将查询对象配置到其中
            SearchRequest searchRequest = new SearchRequest("mydlq-user");
            searchRequest.source(searchSourceBuilder);
            // 执行查询，然后处理响应结果
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 根据状态和数据条数验证是否返回了数据
            if (RestStatus.OK.equals(searchResponse.status()) && searchResponse.getHits().getTotalHits().value > 0)
            {
                SearchHits hits = searchResponse.getHits();
                for (SearchHit hit : hits)
                {
                    // 将 JSON 转换成对象
                    SysUser userInfo = JSON.parseObject(hit.getSourceAsString(), SysUser.class);
                    // 输出查询信息
                    log.info(userInfo.toString());
                }
            }
        }
        catch (IOException e)
        {
            log.error("", e);
        }
    }

    /**
     * 查询距离现在 30 年间的员工数据
     * [年(y)、月(M)、星期(w)、天(d)、小时(h)、分钟(m)、秒(s)]
     * 例如：
     * now-1h 查询一小时内范围
     * now-1d 查询一天内时间范围
     * now-1y 查询最近一年内的时间范围
     */
    public void dateRangeQuery()
    {
        try
        {
            // 构建查询条件
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            // includeLower（是否包含下边界）、includeUpper（是否包含上边界）
            searchSourceBuilder.query(QueryBuilders.rangeQuery("birthDate")
                    .gte("now-30y").includeLower(true).includeUpper(true));
            // 创建查询请求对象，将查询对象配置到其中
            SearchRequest searchRequest = new SearchRequest("mydlq-user");
            searchRequest.source(searchSourceBuilder);
            // 执行查询，然后处理响应结果
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 根据状态和数据条数验证是否返回了数据
            if (RestStatus.OK.equals(searchResponse.status()) && searchResponse.getHits().getTotalHits().value > 0)
            {
                SearchHits hits = searchResponse.getHits();
                for (SearchHit hit : hits)
                {
                    // 将 JSON 转换成对象
                    SysUser userInfo = JSON.parseObject(hit.getSourceAsString(), SysUser.class);
                    // 输出查询信息
                    log.info(userInfo.toString());
                }
            }
        }
        catch (IOException e)
        {
            log.error("", e);
        }
    }

    //  --------------------------------5、通配符查询(wildcard)-----------------------------------------

    /**
     * 查询所有以 “三” 结尾的姓名
     * <p>
     * *：表示多个字符（0个或多个字符）
     * ?：表示单个字符
     */
    public void wildcardQuery()
    {
        try
        {
            // 构建查询条件
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.wildcardQuery("name.keyword", "*三"));
            // 创建查询请求对象，将查询对象配置到其中
            SearchRequest searchRequest = new SearchRequest("mydlq-user");
            searchRequest.source(searchSourceBuilder);
            // 执行查询，然后处理响应结果
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 根据状态和数据条数验证是否返回了数据
            if (RestStatus.OK.equals(searchResponse.status()) && searchResponse.getHits().getTotalHits().value > 0)
            {
                SearchHits hits = searchResponse.getHits();
                for (SearchHit hit : hits)
                {
                    // 将 JSON 转换成对象
                    SysUser userInfo = JSON.parseObject(hit.getSourceAsString(), SysUser.class);
                    // 输出查询信息
                    log.info(userInfo.toString());
                }
            }
        }
        catch (IOException e)
        {
            log.error("", e);
        }
    }

    //  --------------------------------6、布尔查询(bool)-----------------------------------------

    public void boolQuery()
    {
        try
        {
            // 创建 Bool 查询构建器
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            // 构建查询条件
            boolQueryBuilder.must(QueryBuilders.termsQuery("address.keyword", "北京市昌平区", "北京市大兴区", "北京市房山区"))
                    .filter().add(QueryBuilders.rangeQuery("birthDate").format("yyyy").gte("1990").lte("1995"));
            // 构建查询源构建器
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(boolQueryBuilder);
            // 创建查询请求对象，将查询对象配置到其中
            SearchRequest searchRequest = new SearchRequest("mydlq-user");
            searchRequest.source(searchSourceBuilder);
            // 执行查询，然后处理响应结果
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 根据状态和数据条数验证是否返回了数据
            if (RestStatus.OK.equals(searchResponse.status()) && searchResponse.getHits().getTotalHits().value > 0)
            {
                SearchHits hits = searchResponse.getHits();
                for (SearchHit hit : hits)
                {
                    // 将 JSON 转换成对象
                    SysUser userInfo = JSON.parseObject(hit.getSourceAsString(), SysUser.class);
                    // 输出查询信息
                    log.info(userInfo.toString());
                }
            }
        }
        catch (IOException e)
        {
            log.error("", e);
        }
    }

    //  --------------------------------7、Metric 聚合分析-----------------------------------------

    /**
     * stats 统计员工总数、员工工资最高值、员工工资最低值、员工平均工资、员工工资总和
     */
    public Object aggregationStats()
    {
        String responseResult = "";
        try
        {
            // 设置聚合条件
            AggregationBuilder aggr = AggregationBuilders.stats("salary_stats").field("salary");
            // 查询源构建器
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.aggregation(aggr);
            // 设置查询结果不返回，只返回聚合结果
            searchSourceBuilder.size(0);
            // 创建查询请求对象，将查询条件配置到其中
            SearchRequest request = new SearchRequest("mydlq-user");
            request.source(searchSourceBuilder);
            // 执行请求
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            // 获取响应中的聚合信息
            Aggregations aggregations = response.getAggregations();
            // 输出内容
            if (RestStatus.OK.equals(response.status()) || aggregations != null)
            {
                // 转换为 Stats 对象
                ParsedStats aggregation = aggregations.get("salary_stats");
                log.info("-------------------------------------------");
                log.info("聚合信息:");
                log.info("count：{}", aggregation.getCount());
                log.info("avg：{}", aggregation.getAvg());
                log.info("max：{}", aggregation.getMax());
                log.info("min：{}", aggregation.getMin());
                log.info("sum：{}", aggregation.getSum());
                log.info("-------------------------------------------");
            }
            // 根据具体业务逻辑返回不同结果，这里为了方便直接将返回响应对象Json串
            responseResult = response.toString();
        }
        catch (IOException e)
        {
            log.error("", e);
        }
        return responseResult;
    }

    /**
     * min 统计员工工资最低值
     */
    public Object aggregationMin()
    {
        String responseResult = "";
        try
        {
            // 设置聚合条件
            AggregationBuilder aggr = AggregationBuilders.min("salary_min").field("salary");
            // 查询源构建器
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.aggregation(aggr);
            searchSourceBuilder.size(0);
            // 创建查询请求对象，将查询条件配置到其中
            SearchRequest request = new SearchRequest("mydlq-user");
            request.source(searchSourceBuilder);
            // 执行请求
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            // 获取响应中的聚合信息
            Aggregations aggregations = response.getAggregations();
            // 输出内容
            if (RestStatus.OK.equals(response.status()) || aggregations != null)
            {
                // 转换为 Min 对象
                ParsedMin aggregation = aggregations.get("salary_min");
                log.info("-------------------------------------------");
                log.info("聚合信息:");
                log.info("min：{}", aggregation.getValue());
                log.info("-------------------------------------------");
            }
            // 根据具体业务逻辑返回不同结果，这里为了方便直接将返回响应对象Json串
            responseResult = response.toString();
        }
        catch (IOException e)
        {
            log.error("", e);
        }
        return responseResult;
    }

    /**
     * max 统计员工工资最高值
     */
    public Object aggregationMax()
    {
        String responseResult = "";
        try
        {
            // 设置聚合条件
            AggregationBuilder aggr = AggregationBuilders.max("salary_max").field("salary");
            // 查询源构建器
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.aggregation(aggr);
            searchSourceBuilder.size(0);
            // 创建查询请求对象，将查询条件配置到其中
            SearchRequest request = new SearchRequest("mydlq-user");
            request.source(searchSourceBuilder);
            // 执行请求
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            // 获取响应中的聚合信息
            Aggregations aggregations = response.getAggregations();
            // 输出内容
            if (RestStatus.OK.equals(response.status()) || aggregations != null)
            {
                // 转换为 Max 对象
                ParsedMax aggregation = aggregations.get("salary_max");
                log.info("-------------------------------------------");
                log.info("聚合信息:");
                log.info("max：{}", aggregation.getValue());
                log.info("-------------------------------------------");
            }
            // 根据具体业务逻辑返回不同结果，这里为了方便直接将返回响应对象Json串
            responseResult = response.toString();
        }
        catch (IOException e)
        {
            log.error("", e);
        }
        return responseResult;
    }

    /**
     * avg 统计员工工资平均值
     */
    public Object aggregationAvg()
    {
        String responseResult = "";
        try
        {
            // 设置聚合条件
            AggregationBuilder aggr = AggregationBuilders.avg("salary_avg").field("salary");
            // 查询源构建器
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.aggregation(aggr);
            searchSourceBuilder.size(0);
            // 创建查询请求对象，将查询条件配置到其中
            SearchRequest request = new SearchRequest("mydlq-user");
            request.source(searchSourceBuilder);
            // 执行请求
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            // 获取响应中的聚合信息
            Aggregations aggregations = response.getAggregations();
            // 输出内容
            if (RestStatus.OK.equals(response.status()) || aggregations != null)
            {
                // 转换为 Avg 对象
                ParsedAvg aggregation = aggregations.get("salary_avg");
                log.info("-------------------------------------------");
                log.info("聚合信息:");
                log.info("avg：{}", aggregation.getValue());
                log.info("-------------------------------------------");
            }
            // 根据具体业务逻辑返回不同结果，这里为了方便直接将返回响应对象Json串
            responseResult = response.toString();
        }
        catch (IOException e)
        {
            log.error("", e);
        }
        return responseResult;
    }

    /**
     * sum 统计员工工资总值
     */
    public Object aggregationSum()
    {
        String responseResult = "";
        try
        {
            // 设置聚合条件
            SumAggregationBuilder aggr = AggregationBuilders.sum("salary_sum").field("salary");
            // 查询源构建器
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.aggregation(aggr);
            searchSourceBuilder.size(0);
            // 创建查询请求对象，将查询条件配置到其中
            SearchRequest request = new SearchRequest("mydlq-user");
            request.source(searchSourceBuilder);
            // 执行请求
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            // 获取响应中的聚合信息
            Aggregations aggregations = response.getAggregations();
            // 输出内容
            if (RestStatus.OK.equals(response.status()) || aggregations != null)
            {
                // 转换为 Sum 对象
                ParsedSum aggregation = aggregations.get("salary_sum");
                log.info("-------------------------------------------");
                log.info("聚合信息:");
                log.info("sum：{}", String.valueOf((aggregation.getValue())));
                log.info("-------------------------------------------");
            }
            // 根据具体业务逻辑返回不同结果，这里为了方便直接将返回响应对象Json串
            responseResult = response.toString();
        }
        catch (IOException e)
        {
            log.error("", e);
        }
        return responseResult;
    }

    /**
     * count 统计员工总数
     */
    public Object aggregationCount()
    {
        String responseResult = "";
        try
        {
            // 设置聚合条件
            AggregationBuilder aggr = AggregationBuilders.count("employee_count").field("salary");
            // 查询源构建器
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.aggregation(aggr);
            searchSourceBuilder.size(0);
            // 创建查询请求对象，将查询条件配置到其中
            SearchRequest request = new SearchRequest("mydlq-user");
            request.source(searchSourceBuilder);
            // 执行请求
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            // 获取响应中的聚合信息
            Aggregations aggregations = response.getAggregations();
            // 输出内容
            if (RestStatus.OK.equals(response.status()) || aggregations != null)
            {
                // 转换为 ValueCount 对象
                ParsedValueCount aggregation = aggregations.get("employee_count");
                log.info("-------------------------------------------");
                log.info("聚合信息:");
                log.info("count：{}", aggregation.getValue());
                log.info("-------------------------------------------");
            }
            // 根据具体业务逻辑返回不同结果，这里为了方便直接将返回响应对象Json串
            responseResult = response.toString();
        }
        catch (IOException e)
        {
            log.error("", e);
        }
        return responseResult;
    }

    /**
     * percentiles 统计员工工资百分位
     */
    public Object aggregationPercentiles()
    {
        String responseResult = "";
        try
        {
            // 设置聚合条件
            AggregationBuilder aggr = AggregationBuilders.percentiles("salary_percentiles").field("salary");
            // 查询源构建器
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.aggregation(aggr);
            searchSourceBuilder.size(0);
            // 创建查询请求对象，将查询条件配置到其中
            SearchRequest request = new SearchRequest("mydlq-user");
            request.source(searchSourceBuilder);
            // 执行请求
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            // 获取响应中的聚合信息
            Aggregations aggregations = response.getAggregations();
            // 输出内容
            if (RestStatus.OK.equals(response.status()) || aggregations != null)
            {
                // 转换为 Percentiles 对象
                ParsedPercentiles aggregation = aggregations.get("salary_percentiles");
                log.info("-------------------------------------------");
                log.info("聚合信息:");
                for (Percentile percentile : aggregation)
                {
                    log.info("百分位：{}：{}", percentile.getPercent(), percentile.getValue());
                }
                log.info("-------------------------------------------");
            }
            // 根据具体业务逻辑返回不同结果，这里为了方便直接将返回响应对象Json串
            responseResult = response.toString();
        }
        catch (IOException e)
        {
            log.error("", e);
        }
        return responseResult;
    }

    //  --------------------------------8、Bucket 聚合分析-----------------------------------------

    /**
     * 按岁数进行聚合分桶
     */
    public void aggrBucketTerms()
    {
        try
        {
            AggregationBuilder aggr = AggregationBuilders.terms("age_bucket").field("age");
            // 查询源构建器
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.size(10);
            searchSourceBuilder.aggregation(aggr);
            // 创建查询请求对象，将查询条件配置到其中
            SearchRequest request = new SearchRequest("mydlq-user");
            request.source(searchSourceBuilder);
            // 执行请求
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            // 获取响应中的聚合信息
            Aggregations aggregations = response.getAggregations();
            // 输出内容
            if (RestStatus.OK.equals(response.status()))
            {
                // 分桶
                Terms byCompanyAggregation = aggregations.get("age_bucket");
                List<? extends Terms.Bucket> buckets = byCompanyAggregation.getBuckets();
                // 输出各个桶的内容
                log.info("-------------------------------------------");
                log.info("聚合信息:");
                for (Terms.Bucket bucket : buckets)
                {
                    log.info("桶名：{} | 总数：{}", bucket.getKeyAsString(), bucket.getDocCount());
                }
                log.info("-------------------------------------------");
            }
        }
        catch (IOException e)
        {
            log.error("", e);
        }
    }

    /**
     * 按工资范围进行聚合分桶
     */
    public void aggrBucketRange()
    {
        try
        {
            AggregationBuilder aggr = AggregationBuilders.range("salary_range_bucket")
                    .field("salary")
                    .addUnboundedTo("低级员工", 3000)
                    .addRange("中级员工", 5000, 9000)
                    .addUnboundedFrom("高级员工", 9000);
            // 查询源构建器
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.size(0);
            searchSourceBuilder.aggregation(aggr);
            // 创建查询请求对象，将查询条件配置到其中
            SearchRequest request = new SearchRequest("mydlq-user");
            request.source(searchSourceBuilder);
            // 执行请求
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            // 获取响应中的聚合信息
            Aggregations aggregations = response.getAggregations();
            // 输出内容
            if (RestStatus.OK.equals(response.status()))
            {
                // 分桶
                Range byCompanyAggregation = aggregations.get("salary_range_bucket");
                List<? extends Range.Bucket> buckets = byCompanyAggregation.getBuckets();
                // 输出各个桶的内容
                log.info("-------------------------------------------");
                log.info("聚合信息:");
                for (Range.Bucket bucket : buckets)
                {
                    log.info("桶名：{} | 总数：{}", bucket.getKeyAsString(), bucket.getDocCount());
                }
                log.info("-------------------------------------------");
            }
        }
        catch (IOException e)
        {
            log.error("", e);
        }
    }

    /**
     * 按照时间范围进行分桶
     */
    public void aggrBucketDateRange()
    {
        try
        {
            AggregationBuilder aggr = AggregationBuilders.dateRange("date_range_bucket")
                    .field("birthDate")
                    .format("yyyy")
                    .addRange("1985-1990", "1985", "1990")
                    .addRange("1990-1995", "1990", "1995");
            // 查询源构建器
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.size(0);
            searchSourceBuilder.aggregation(aggr);
            // 创建查询请求对象，将查询条件配置到其中
            SearchRequest request = new SearchRequest("mydlq-user");
            request.source(searchSourceBuilder);
            // 执行请求
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            // 获取响应中的聚合信息
            Aggregations aggregations = response.getAggregations();
            // 输出内容
            if (RestStatus.OK.equals(response.status()))
            {
                // 分桶
                Range byCompanyAggregation = aggregations.get("date_range_bucket");
                List<? extends Range.Bucket> buckets = byCompanyAggregation.getBuckets();
                // 输出各个桶的内容
                log.info("-------------------------------------------");
                log.info("聚合信息:");
                for (Range.Bucket bucket : buckets)
                {
                    log.info("桶名：{} | 总数：{}", bucket.getKeyAsString(), bucket.getDocCount());
                }
                log.info("-------------------------------------------");
            }
        }
        catch (IOException e)
        {
            log.error("", e);
        }
    }

    /**
     * 按工资多少进行聚合分桶
     */
    public void aggrBucketHistogram()
    {
        try
        {
            AggregationBuilder aggr = AggregationBuilders.histogram("salary_histogram")
                    .field("salary")
                    .extendedBounds(0, 12000)
                    .interval(3000);
            // 查询源构建器
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.size(0);
            searchSourceBuilder.aggregation(aggr);
            // 创建查询请求对象，将查询条件配置到其中
            SearchRequest request = new SearchRequest("mydlq-user");
            request.source(searchSourceBuilder);
            // 执行请求
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            // 获取响应中的聚合信息
            Aggregations aggregations = response.getAggregations();
            // 输出内容
            if (RestStatus.OK.equals(response.status()))
            {
                // 分桶
                Histogram byCompanyAggregation = aggregations.get("salary_histogram");
                List<? extends Histogram.Bucket> buckets = byCompanyAggregation.getBuckets();
                // 输出各个桶的内容
                log.info("-------------------------------------------");
                log.info("聚合信息:");
                for (Histogram.Bucket bucket : buckets)
                {
                    log.info("桶名：{} | 总数：{}", bucket.getKeyAsString(), bucket.getDocCount());
                }
                log.info("-------------------------------------------");
            }
        }
        catch (IOException e)
        {
            log.error("", e);
        }
    }

    /**
     * 按出生日期进行分桶
     */
    public void aggrBucketDateHistogram()
    {
        try
        {
            AggregationBuilder aggr = AggregationBuilders.dateHistogram("birthday_histogram")
                    .field("birthDate")
                    .interval(1)
                    .dateHistogramInterval(DateHistogramInterval.YEAR)
                    .format("yyyy");
            // 查询源构建器
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.size(0);
            searchSourceBuilder.aggregation(aggr);
            // 创建查询请求对象，将查询条件配置到其中
            SearchRequest request = new SearchRequest("mydlq-user");
            request.source(searchSourceBuilder);
            // 执行请求
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            // 获取响应中的聚合信息
            Aggregations aggregations = response.getAggregations();
            // 输出内容
            if (RestStatus.OK.equals(response.status()))
            {
                // 分桶
                Histogram byCompanyAggregation = aggregations.get("birthday_histogram");

                List<? extends Histogram.Bucket> buckets = byCompanyAggregation.getBuckets();
                // 输出各个桶的内容
                log.info("-------------------------------------------");
                log.info("聚合信息:");
                for (Histogram.Bucket bucket : buckets)
                {
                    log.info("桶名：{} | 总数：{}", bucket.getKeyAsString(), bucket.getDocCount());
                }
                log.info("-------------------------------------------");
            }
        }
        catch (IOException e)
        {
            log.error("", e);
        }
    }

    //  --------------------------------9、Metric 与 Bucket 聚合分析-------------------------------

    /**
     * topHits 按岁数分桶、然后统计每个员工工资最高值
     */
    public void aggregationTopHits()
    {
        try
        {
            AggregationBuilder testTop = AggregationBuilders.topHits("salary_max_user")
                    .size(1)
                    .sort("salary", SortOrder.DESC);
            AggregationBuilder salaryBucket = AggregationBuilders.terms("salary_bucket")
                    .field("age")
                    .size(10);
            salaryBucket.subAggregation(testTop);
            // 查询源构建器
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.size(0);
            searchSourceBuilder.aggregation(salaryBucket);
            // 创建查询请求对象，将查询条件配置到其中
            SearchRequest request = new SearchRequest("mydlq-user");
            request.source(searchSourceBuilder);
            // 执行请求
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            // 获取响应中的聚合信息
            Aggregations aggregations = response.getAggregations();
            // 输出内容
            if (RestStatus.OK.equals(response.status()))
            {
                // 分桶
                Terms byCompanyAggregation = aggregations.get("salary_bucket");
                List<? extends Terms.Bucket> buckets = byCompanyAggregation.getBuckets();
                // 输出各个桶的内容
                log.info("-------------------------------------------");
                log.info("聚合信息:");
                for (Terms.Bucket bucket : buckets)
                {
                    log.info("桶名：{}", bucket.getKeyAsString());
                    ParsedTopHits topHits = bucket.getAggregations().get("salary_max_user");
                    for (SearchHit hit : topHits.getHits())
                    {
                        log.info(hit.getSourceAsString());
                    }
                }
                log.info("-------------------------------------------");
            }
        }
        catch (IOException e)
        {
            log.error("", e);
        }
    }

}

