package cn.matcheasy.es;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONUtil;
import cn.matcheasy.MatcheasyApplicationTests;
import cn.matcheasy.framework.utils.UUIDUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.metrics.ParsedStats;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.BufferedInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * @class: EsTest
 * @author: wangjing
 * @date: 2021/2/23/0023
 * @desc: 索引创建/查询测试
 */
@Slf4j
public class EsTest extends MatcheasyApplicationTests
{
    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    /**
     * 创建索引
     */
    @Test
    void createEsIndex() throws Exception
    {
        String indexName = "es_test";

        //判断索引是否存在
        boolean boo = restHighLevelClient.indices().exists(new GetIndexRequest(indexName), RequestOptions.DEFAULT);
        if (boo)
        {
            DeleteIndexRequest request = new DeleteIndexRequest(indexName);
            AcknowledgedResponse acknowledgedResponse = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
            boolean siDeleted = acknowledgedResponse.isAcknowledged();
            System.out.println("删除索引：" + siDeleted);
        }

        String filePath = "templates/es/" + indexName + ".json";
        //读取配置文件下的文件
        //InputStream inputStream = ClassUtils.getDefaultClassLoader().getResourceAsStream("templates/es_test.json");
        BufferedInputStream inputStream = FileUtil.getInputStream(filePath);
        String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        Map source = JSONUtil.toBean(content, Map.class);

        //索引名称
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        request.source(source);
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        //所有节点确认请求
        boolean acknowledged = createIndexResponse.isAcknowledged();
        //分片确认
        boolean shardsAcknowledged = createIndexResponse.isShardsAcknowledged();
        System.out.println("创建索引：" + shardsAcknowledged);
    }

    @Test
    void createEsDoc() throws Exception
    {
        String uuid = UUIDUtil.fastSimpleUUID();
        Dict dict = new Dict()
                .set("id", uuid)
                .set("username", "阿尔法狗")
                .set("age", 20)
                .set("gender", "男")
                .set("department", "研发部")
                .set("createTime", DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN))
                .set("updateTime", new Date())
                .set("salary", 500.54);
        IndexRequest indexRequest = new IndexRequest("es_test")
                .id(uuid).routing(uuid)
                .source(JSONUtil.toJsonStr(dict), XContentType.JSON);
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println("结果：" + indexResponse.status());
    }

    @Test
    void selectEsDoc() throws Exception
    {
        // 获取请求对象
        GetRequest getRequest = new GetRequest("es_test", "84649f62c2c94077876bf3fa330024ac").routing("84649f62c2c94077876bf3fa330024ac");
        // 获取文档信息
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        // 将 JSON 转换成对象
        if (getResponse.isExists())
        {
            Map map = JSON.parseObject(getResponse.getSourceAsBytes(), Map.class);
            System.out.println("查询结果：" + JSONUtil.toJsonStr(map));
        }
    }

    @Test
    void matchQuery() throws Exception
    {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder qb1 = QueryBuilders.termQuery("createTime", "2021-11-20 17:19:51");
        QueryBuilder qb2 = QueryBuilders.wildcardQuery("username", "*法*");
        QueryBuilder qb = QueryBuilders.boolQuery().must(qb1).must(qb2);
        searchSourceBuilder.query(qb);

        SearchRequest searchRequest = new SearchRequest("es_test");
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        if (RestStatus.OK.equals(searchResponse.status()) && searchResponse.getHits().getTotalHits().value > 0)
        {
            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits)
            {
                // 将 JSON 转换成对象
                Dict dict = JSON.parseObject(hit.getSourceAsString(), Dict.class);
                // 输出查询信息
                System.out.println("查询结果：" + JSONUtil.toJsonStr(dict));
            }
        }
    }

    @Test
    void aggregationStats() throws Exception
    {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder qb1 = QueryBuilders.rangeQuery("updateTime").format("yyyy-MM-dd").gte("2021-11-20").lte("2021-11-20");
        QueryBuilder qb = QueryBuilders.boolQuery().filter(qb1);
        searchSourceBuilder.query(qb);

        AggregationBuilder aggr = AggregationBuilders.stats("stats").field("salary");
        searchSourceBuilder.aggregation(aggr);
        searchSourceBuilder.size(0);
        SearchRequest searchRequest = new SearchRequest("es_test");
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        Aggregations aggregations = response.getAggregations();
        if (RestStatus.OK.equals(response.status()) || aggregations != null)
        {
            ParsedStats aggregation = aggregations.get("stats");
            System.out.println("聚合信息:");
            System.out.println("count：" + aggregation.getCount());
            System.out.println("avg：" + aggregation.getAvg());
            System.out.println("max：" + aggregation.getMax());
            System.out.println("min：" + aggregation.getMin());
            System.out.println("sum：" + aggregation.getSum());
        }
    }

    /**
     * 时间格式化范围查询
     */
    @Test
    void rangeTest() throws Exception
    {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder qb1 = QueryBuilders.rangeQuery("createTime").format("yyyy-MM-dd HH:mm:ss").gte("2021-10-01 00:00:00").lte("2021-11-01 00:00:00");
        QueryBuilder qb2 = QueryBuilders.termQuery("shopId", "1CUTGNNLRM0TK802SL80C843ED0012N9");
        QueryBuilder qb = QueryBuilders.boolQuery().filter(qb1).must(qb2);
        searchSourceBuilder.query(qb);

        SearchRequest searchRequest = new SearchRequest("es_test");
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        if (RestStatus.OK.equals(searchResponse.status()) && searchResponse.getHits().getTotalHits().value > 0)
        {
            SearchHits hits = searchResponse.getHits();
            TotalHits totalHits = hits.getTotalHits();
            System.out.println("查询结果总数：" + totalHits);
        }
    }
}