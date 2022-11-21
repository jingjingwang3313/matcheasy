package cn.matcheasy.project.common.base.dao.es;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.PageUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.matcheasy.framework.commonbean.PageBean;
import cn.matcheasy.framework.utils.AggregationUtil;
import cn.matcheasy.framework.utils.MyAssert;
import cn.matcheasy.framework.utils.PageUtils;
import cn.matcheasy.framework.utils.ReflectionUtil;
import cn.matcheasy.framework.validate.Preconditions;
import cn.matcheasy.project.common.wrapper.EsWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkItemResponse.Failure;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
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
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.SumAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @class: AbstractEsCommonDao
 * @author: wangjing
 * @date: 2021/11/26/0026
 * @desc: 继承此类操作es
 */
@Slf4j
public abstract class AbstractEsCommonDao<T extends AbstractEsBaseEntity>
{
    /**
     * 高版本客户端
     *
     * @return RestHighLevelClient
     */
    public abstract RestHighLevelClient getRestHighLevelClient();

    /**
     * 客户端
     *
     * @return RestClient
     */
    public abstract RestClient getRestClient();

    /**
     * es索引名称
     *
     * @return String
     */
    public String getIndexName()
    {
        return StrUtil.toUnderlineCase(entityClass.getSimpleName());
    }

    /**
     * 对应的实体类
     *
     * @return Class
     */
    public Class<T> getEntityClass()
    {
        return (Class<T>) entityClass;
    }

    protected Class<?> entityClass = this.currentEntityClass();

    protected Class<?> currentEntityClass()
    {
        return ReflectionUtil.getSuperClassGenericType(this.getClass(), 0);
    }

    /**
     * es执行出现错误时，是否catch掉返回默认值，默认true抛出异常
     *
     * @return boolean
     */
    public boolean exceptionThrow()
    {
        return true;
    }

    /**
     * 插入一条数据
     *
     * @param data 数据
     * @return boolean
     */
    public boolean insert(T data)
    {

        String indexName = Preconditions.checkNotNull(getIndexName(), "indices must not null");
        RestHighLevelClient client = getRestHighLevelClient();

        IndexRequest indexRequest = new IndexRequest(indexName);

        String idVal = data.getEsEntityId();
        if (StringUtils.isNotBlank(idVal))
        {
            indexRequest.id(idVal);
        }
        String routing = data.getRouting();
        if (StringUtils.isNotBlank(routing))
        {
            indexRequest.routing(routing);
        }

        String esEntityJson = data.toJson();
        indexRequest.source(esEntityJson, XContentType.JSON);

        try
        {
            IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
            return response.status() == RestStatus.CREATED;

        }
        catch (IOException e)
        {
            log.error("es insert error", e);
            if (exceptionThrow())
            {
                MyAssert.throwException("es insert error");
            }
        }
        return false;
    }

    /**
     * 批量插入数据
     *
     * @param list 数据list
     * @return boolean
     */
    public boolean insertBatch(List<T> list)
    {

        String indexName = Preconditions.checkNotNull(getIndexName(), "indices must not null");
        Preconditions.checkNotEmpty(list, "list is empty");
        RestHighLevelClient client = getRestHighLevelClient();

        BulkRequest bulkRequest = new BulkRequest();
        for (T esEntity : list)
        {

            String esEntityJson = esEntity.toJson();
            IndexRequest indexRequest = new IndexRequest(indexName);

            String idVal = esEntity.getEsEntityId();
            if (StringUtils.isNotBlank(idVal))
            {
                indexRequest.id(idVal);
            }
            String routing = esEntity.getRouting();
            if (StringUtils.isNotBlank(routing))
            {
                indexRequest.routing(routing);
            }
            indexRequest.source(esEntityJson, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        try
        {
            BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            return !bulkResponse.hasFailures();
        }
        catch (IOException e)
        {
            log.error("es insertBatch error", e);
            if (exceptionThrow())
            {
                MyAssert.throwException("es insertBatch error");
            }
        }
        return false;
    }

    /**
     * 更新一条数据
     *
     * @param data 数据
     * @return boolean
     */
    public boolean updateById(T data)
    {

        String indexName = Preconditions.checkNotNull(getIndexName(), "indices must not null");
        RestHighLevelClient client = getRestHighLevelClient();

        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(indexName);

        String idVal = data.getEsEntityId();
        Preconditions.checkNotBlank(idVal, "id must not null");
        updateRequest.id(idVal);

        String routing = data.getRouting();
        if (StringUtils.isNotBlank(routing))
        {
            updateRequest.routing(routing);
        }

        updateRequest.doc(data.toJson(), XContentType.JSON);

        try
        {
            UpdateResponse response = client.update(updateRequest, RequestOptions.DEFAULT);
            return response.status() == RestStatus.OK;

        }
        catch (IOException e)
        {
            log.error("es updateById error", e);
            if (exceptionThrow())
            {
                MyAssert.throwException("es updateById error");
            }
        }
        return false;
    }

    /**
     * 批量更新数据
     *
     * @param list 数据list
     * @return boolean
     */
    public boolean updateBatchById(List<T> list)
    {

        String indexName = Preconditions.checkNotNull(getIndexName(), "indices must not null");
        Preconditions.checkNotEmpty(list, "list is empty");

        RestHighLevelClient client = getRestHighLevelClient();

        BulkRequest bulkRequest = new BulkRequest();
        for (T esEntity : list)
        {

            String esEntityJson = esEntity.toJson();

            String idVal = esEntity.getEsEntityId();
            if (StringUtils.isBlank(idVal))
            {
                continue;
            }

            UpdateRequest updateRequest = new UpdateRequest(indexName, idVal);

            String routing = esEntity.getRouting();
            if (StringUtils.isNotBlank(routing))
            {
                updateRequest.routing(routing);
            }

            updateRequest.doc(esEntityJson, XContentType.JSON);
            bulkRequest.add(updateRequest);
        }

        try
        {
            BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            return !bulkResponse.hasFailures();

        }
        catch (IOException e)
        {
            log.error("es updateBatchById error", e);
            if (exceptionThrow())
            {
                MyAssert.throwException("es updateBatchById error");
            }
        }
        return false;
    }

    /**
     * 插入更新一条数据
     *
     * @param data 数据
     * @return boolean
     */
    public boolean upsert(T data)
    {

        String indexName = Preconditions.checkNotNull(getIndexName(), "indices must not null");
        RestHighLevelClient client = getRestHighLevelClient();

        String esEntityJson = data.toJson();
        IndexRequest indexRequest = new IndexRequest(indexName);

        String idVal = data.getEsEntityId();
        if (StringUtils.isNotBlank(idVal))
        {
            indexRequest.id(idVal);
        }
        String routing = data.getRouting();
        if (StringUtils.isNotBlank(routing))
        {
            indexRequest.routing(routing);
        }

        indexRequest.source(esEntityJson, XContentType.JSON);
        UpdateRequest updateRequest = new UpdateRequest();

        updateRequest.index(indexName);
        updateRequest.id(idVal);

        if (StringUtils.isNotBlank(routing))
        {
            updateRequest.routing(routing);
        }

        updateRequest.doc(esEntityJson, XContentType.JSON);
        updateRequest.upsert(indexRequest);

        try
        {
            UpdateResponse response = client.update(updateRequest, RequestOptions.DEFAULT);
            return response.status() == RestStatus.OK;

        }
        catch (IOException e)
        {
            log.error("es upsert error", e);
            if (exceptionThrow())
            {
                MyAssert.throwException("es upsert error");
            }
        }
        return false;
    }

    /**
     * 插入更新多条条数据
     *
     * @param list 数据
     * @return boolean
     */
    public boolean upsertBatch(List<T> list)
    {

        String indexName = Preconditions.checkNotNull(getIndexName(), "indices must not null");
        RestHighLevelClient client = getRestHighLevelClient();

        BulkRequest bulkRequest = new BulkRequest();
        for (T esEntity : list)
        {

            String esEntityJson = esEntity.toJson();
            IndexRequest indexRequest = new IndexRequest(indexName);

            String idVal = esEntity.getEsEntityId();
            if (StringUtils.isNotBlank(idVal))
            {
                indexRequest.id(idVal);
            }
            String routing = esEntity.getRouting();
            if (StringUtils.isNotBlank(routing))
            {
                indexRequest.routing(routing);
            }

            indexRequest.source(esEntityJson, XContentType.JSON);
            UpdateRequest updateRequest = new UpdateRequest(indexName, idVal);

            if (StringUtils.isNotBlank(routing))
            {
                updateRequest.routing(routing);
            }

            updateRequest.doc(esEntityJson, XContentType.JSON);
            updateRequest.upsert(indexRequest);
            bulkRequest.add(updateRequest);
        }

        try
        {
            BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            return !bulkResponse.hasFailures();

        }
        catch (IOException e)
        {
            log.error("es upsertBatch error", e);
            if (exceptionThrow())
            {
                MyAssert.throwException("es upsertBatch error");
            }
        }
        return false;
    }

    /**
     * 删除一条数据
     *
     * @param id      id
     * @param routing routing
     * @return boolean
     */
    public boolean deleteById(String id, String routing)
    {

        String indexName = Preconditions.checkNotNull(getIndexName(), "indices must not null");
        DeleteRequest deleteRequest = new DeleteRequest(indexName, id);

        if (StringUtils.isNotBlank(routing))
        {
            deleteRequest.routing(routing);
        }

        try
        {
            DeleteResponse response = getRestHighLevelClient().delete(deleteRequest, RequestOptions.DEFAULT);
            return response.status() == RestStatus.OK;

        }
        catch (IOException e)
        {
            log.error("es deleteById error", e);
            if (exceptionThrow())
            {
                MyAssert.throwException("es deleteById error");
            }
        }
        return false;
    }

    /**
     * 删除一条数据
     *
     * @param id id
     * @return boolean
     */
    public boolean deleteById(String id)
    {
        return deleteById(id, null);
    }

    /**
     * 删除多条数据
     *
     * @param idList 数据
     * @return boolean
     */
    public boolean deleteBatchByIds(Collection<String> idList)
    {

        String indexName = Preconditions.checkNotNull(getIndexName(), "indices must not null");

        BulkRequest bulkRequest = new BulkRequest();
        for (String id : idList)
        {
            DeleteRequest deleteRequest = new DeleteRequest(indexName, id);
            bulkRequest.add(deleteRequest);
        }

        try
        {
            BulkResponse bulkResponse = getRestHighLevelClient().bulk(bulkRequest, RequestOptions.DEFAULT);
            return !bulkResponse.hasFailures();

        }
        catch (IOException e)
        {
            log.error("es deleteBatchByIds error", e);
            if (exceptionThrow())
            {
                MyAssert.throwException("es deleteBatchByIds error");
            }
        }
        return false;
    }

    /**
     * 查询删除
     *
     * @param wrapper wrapper
     * @return boolean
     */
    public boolean deleteByWrapper(EsWrapper wrapper)
    {

        String indexName = Preconditions.checkNotNull(getIndexName(), "indices must not null");
        RestHighLevelClient client = getRestHighLevelClient();

        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest();
        deleteByQueryRequest.indices(indexName);

        if (StringUtils.isNotBlank(wrapper.getRouting()))
        {
            deleteByQueryRequest.setRouting(wrapper.getRouting());
        }
        deleteByQueryRequest.setQuery(wrapper.buildQueryBuilder());

        try
        {
            BulkByScrollResponse response = client.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
            List<Failure> bulkFailures = response.getBulkFailures();

            return CollUtil.isEmpty(bulkFailures);

        }
        catch (IOException e)
        {
            log.error("es deleteByWrapper error", e);
            if (exceptionThrow())
            {
                MyAssert.throwException("es deleteByWrapper error");
            }
        }
        return false;
    }

    /**
     * 查询删除
     *
     * @param queryBuilder queryBuilder
     * @param routing      routing
     * @return boolean
     */
    public boolean deleteByQuery(QueryBuilder queryBuilder, String routing)
    {

        String indexName = Preconditions.checkNotNull(getIndexName(), "indices must not null");
        RestHighLevelClient client = getRestHighLevelClient();

        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest();
        deleteByQueryRequest.indices(indexName);

        if (StringUtils.isNotBlank(routing))
        {
            deleteByQueryRequest.setRouting(routing);
        }

        deleteByQueryRequest.setQuery(queryBuilder);

        try
        {
            BulkByScrollResponse response = client.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
            List<Failure> bulkFailures = response.getBulkFailures();

            return CollUtil.isEmpty(bulkFailures);

        }
        catch (IOException e)
        {
            log.error("es deleteByQuery error", e);
            if (exceptionThrow())
            {
                MyAssert.throwException("es deleteByQuery error");
            }
        }
        return false;
    }

    /**
     * 查询删除
     *
     * @param queryBuilder queryBuilder
     * @return boolean
     */
    public boolean deleteByQuery(QueryBuilder queryBuilder)
    {
        return deleteByQuery(queryBuilder, null);
    }

    /**
     * 根据id判断数据是否存在
     *
     * @param id      id
     * @param routing routing
     * @return boolean
     */
    public boolean exists(String id, String routing)
    {

        String indexName = Preconditions.checkNotNull(getIndexName(), "indices must not null");
        GetRequest getRequest = new GetRequest(indexName, id);

        if (StringUtils.isNotBlank(routing))
        {
            getRequest.routing(routing);
        }
        try
        {
            return getRestHighLevelClient().exists(getRequest, RequestOptions.DEFAULT);

        }
        catch (IOException e)
        {
            log.error("es exists error", e);
            if (exceptionThrow())
            {
                MyAssert.throwException("es exists error");
            }
        }
        return false;
    }

    /**
     * 根据id判断数据是否存在
     *
     * @param id id
     * @return boolean
     */
    public boolean exists(String id)
    {
        return exists(id, null);
    }

    /**
     * 根据id查询数据
     *
     * @param id      id
     * @param routing routing
     * @return T
     */
    public T selectById(String id, String routing)
    {
        String indexName = Preconditions.checkNotNull(getIndexName(), "indices must not null");
        RestHighLevelClient client = getRestHighLevelClient();

        GetRequest getRequest = new GetRequest(indexName, id);
        if (StringUtils.isNotBlank(routing))
        {
            getRequest.routing(routing);
        }
        try
        {
            GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);

            String json = getResponse.getSourceAsString();
            return JSONUtil.toBean(json, getEntityClass());
        }
        catch (IOException e)
        {
            log.error("es selectById error", e);
            if (exceptionThrow())
            {
                MyAssert.throwException("es selectById error");
            }
        }
        return null;
    }

    /**
     * 根据id查询数据
     *
     * @param id id
     * @return T
     */
    public T selectById(String id)
    {
        return selectById(id, null);
    }

    /**
     * 查询list
     *
     * @param queryBuilder queryBuilder
     * @param routing      路由
     * @param from         from
     * @param size         size
     * @param includes     包含字段
     * @param excludes     过滤字段
     * @return List
     */
    public List<T> selectByQuery(QueryBuilder queryBuilder, String routing, int from, int size, String[] includes, String[] excludes)
    {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(queryBuilder);
        //设置查询，可以是任何类型的QueryBuilder。
        sourceBuilder.from(from);
        //设置确定结果要从哪个索引开始搜索的from选项，默认为0
        sourceBuilder.size(size);
        //设置确定搜素命中返回数的size选项，默认为10
        sourceBuilder.fetchSource(includes, excludes);
        //第一个是获取字段，第二个是过滤的字段，默认获取全部
        SearchRequest searchRequest = new SearchRequest(getIndexName());
        //索引
        searchRequest.routing(routing);
        searchRequest.source(sourceBuilder);

        RestHighLevelClient client = getRestHighLevelClient();

        try
        {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            return searchResponseToEsEntity(searchResponse, getEntityClass());

        }
        catch (IOException e)
        {
            log.error("es selectByQuery error", e);
            if (exceptionThrow())
            {
                MyAssert.throwException("es selectByQuery error");
            }
        }
        return ListUtil.empty();
    }

    /**
     * 查询list
     *
     * @param queryBuilder queryBuilder
     * @param from         from
     * @param size         size
     * @param includes     包含字段
     * @param excludes     过滤字段
     * @return List
     */
    public List<T> selectByQuery(QueryBuilder queryBuilder, int from, int size, String[] includes, String[] excludes)
    {
        return selectByQuery(queryBuilder, null, from, size, includes, excludes);
    }

    /**
     * 根据 EsWrapper 查询list
     *
     * @param wrapper wrapper
     * @return List
     */
    public List<T> selectByWrapper(EsWrapper wrapper)
    {
        String indexName = Preconditions.checkNotNull(getIndexName(), "indices must not null");
        wrapper.indices(indexName);

        SearchRequest searchRequest = wrapper.buildSearchRequest();
        RestHighLevelClient client = getRestHighLevelClient();

        try
        {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            return searchResponseToEsEntity(searchResponse, getEntityClass());

        }
        catch (IOException e)
        {
            log.error("es selectByWrapper error", e);
            if (exceptionThrow())
            {
                MyAssert.throwException("es selectByWrapper error");
            }
        }
        return ListUtil.empty();
    }

    /**
     * 分页查询
     *
     * @param queryBuilder 查询条件
     * @param routing      路由
     * @param pageNum      pageNum
     * @param pageSize     pageSize
     * @param includes     字段
     * @param excludes     过滤字段
     * @return PageBean
     */
    public PageBean<T> selectPage(QueryBuilder queryBuilder, String routing, Integer pageNum, Integer pageSize, String[] includes, String[] excludes)
    {

        String indexName = Preconditions.checkNotNull(getIndexName(), "indices must not null");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //设置查询，可以是任何类型的QueryBuilder。
        sourceBuilder.query(queryBuilder);

        //设置确定结果要从哪个索引开始搜索的from选项，默认为0
        int start = PageUtils.getStart(pageNum, pageSize);
        sourceBuilder.from(start);
        sourceBuilder.size(pageSize);

        //设置确定搜素命中返回数的size选项，默认为10
        sourceBuilder.fetchSource(includes, excludes);

        //第一个是获取字段，第二个是过滤的字段，默认获取全部
        SearchRequest searchRequest = new SearchRequest(indexName);

        //索引
        searchRequest.routing(routing);
        searchRequest.source(sourceBuilder);

        try
        {
            SearchResponse searchResponse = getRestHighLevelClient().search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();

            long totalHits = hits.getTotalHits().value;
            List<T> list = searchResponseToEsEntity(searchResponse, getEntityClass());

            long pageTotal = PageUtil.totalPage(Convert.toInt(totalHits), pageSize);
            return PageBean.build(pageNum, pageSize, pageTotal, totalHits, list);

        }
        catch (IOException e)
        {
            log.error("es selectPage error", e);
            if (exceptionThrow())
            {
                MyAssert.throwException("es selectPage error");
            }
        }
        return PageUtils.empty(pageNum, pageSize);
    }

    /**
     * 分页查询
     *
     * @param queryBuilder 查询条件
     * @param from         from
     * @param size         size
     * @param includes     字段
     * @param excludes     过滤字段
     * @return PageBean
     */
    public PageBean<T> selectPage(QueryBuilder queryBuilder, int from, int size, String[] includes, String[] excludes)
    {
        return selectPage(queryBuilder, null, from, size, includes, excludes);
    }

    /**
     * 分页查询
     *
     * @param wrapper wrapper
     * @return page
     */
    public PageBean<T> selectPageByWrapper(EsWrapper wrapper)
    {

        String indexName = Preconditions.checkNotNull(getIndexName(), "indices must not null");
        wrapper.indices(indexName);

        RestHighLevelClient client = getRestHighLevelClient();
        SearchRequest searchRequest = wrapper.buildSearchRequest();

        try
        {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();

            long totalHits = hits.getTotalHits().value;
            List<T> list = searchResponseToEsEntity(searchResponse, getEntityClass());

            long pageTotal = PageUtil.totalPage(Convert.toInt(totalHits), wrapper.getPageSize());
            long pageNum = wrapper.getpageNum();

            return PageBean.build(pageNum, wrapper.getPageSize(), pageTotal, totalHits, list);

        }
        catch (IOException e)
        {
            log.error("es selectPageByWrapper error", e);
            if (exceptionThrow())
            {
                MyAssert.throwException("es selectPageByWrapper error");
            }
        }
        return PageBean.empty(wrapper.getpageNum(), wrapper.getPageSize());
    }

    /**
     * 聚合count查询
     *
     * @param queryBuilder queryBuilder
     * @return Long
     */
    public Long count(QueryBuilder queryBuilder)
    {
        return count(queryBuilder, "_id");
    }

    /**
     * 聚合count查询
     *
     * @param queryBuilder queryBuilder
     * @param field        count字段
     * @return Long
     */
    public Long count(QueryBuilder queryBuilder, String field)
    {

        String indexName = Preconditions.checkNotNull(getIndexName(), "indices must not null");
        RestHighLevelClient client = getRestHighLevelClient();

        Preconditions.checkNotBlank(field, "聚合字段不可为空");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        AggregationBuilder agg = AggregationBuilders.count(field).field(field);
        searchSourceBuilder.aggregation(agg);
        searchSourceBuilder.size(0);
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(searchSourceBuilder);

        try
        {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            return AggregationUtil.getRootValueCountValue(searchResponse, field);

        }
        catch (IOException e)
        {
            log.error("es count error", e);
            if (exceptionThrow())
            {
                MyAssert.throwException("es count error");
            }
        }
        return 0L;
    }

    /**
     * 聚合count查询
     *
     * @param wrapper EsWrapper
     * @return Long
     */
    public Long countByWrapper(EsWrapper wrapper)
    {
        String indexName = Preconditions.checkNotNull(getIndexName(), "indices must not null");
        wrapper.indices(indexName);
        return count(wrapper.buildQueryBuilder(), "_id");
    }

    /**
     * 聚合count查询
     *
     * @param wrapper EsWrapper
     * @param field   聚合字段
     * @return Long
     */
    public Long countByWrapper(EsWrapper wrapper, String field)
    {
        String indexName = Preconditions.checkNotNull(getIndexName(), "indices must not null");
        wrapper.indices(indexName);
        return count(wrapper.buildQueryBuilder(), field);
    }

    /**
     * 去重count查询
     *
     * @param queryBuilder queryBuilder
     * @param field        去重field
     * @return Long
     */
    public Long distinctCount(QueryBuilder queryBuilder, String field)
    {

        String indexName = Preconditions.checkNotNull(getIndexName(), "indices must not null");
        RestHighLevelClient client = getRestHighLevelClient();

        Preconditions.checkNotBlank(field, "聚合字段不可为空");

        // 1. 去重信息
        CardinalityAggregationBuilder cardinalityAggregationBuilder = AggregationBuilders.cardinality(field).field(field);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.aggregation(cardinalityAggregationBuilder);
        searchSourceBuilder.size(0);
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(searchSourceBuilder);

        try
        {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            return AggregationUtil.getRootCardinalityValue(searchResponse, field);

        }
        catch (IOException e)
        {
            log.error("es distinctCount error", e);
            if (exceptionThrow())
            {
                MyAssert.throwException("es distinctCount error");
            }
        }
        return 0L;
    }

    /**
     * 去重count查询
     *
     * @param wrapper EsWrapper
     * @param field   聚合字段
     * @return Long
     */
    public Long distinctCountByWrapper(EsWrapper wrapper, String field)
    {
        String indexName = Preconditions.checkNotNull(getIndexName(), "indices must not null");
        wrapper.indices(indexName);
        return distinctCount(wrapper.buildQueryBuilder(), field);
    }

    /**
     * 聚合sum查询
     *
     * @param queryBuilder queryBuilder
     * @param field        sum字段
     * @return Long
     */
    public Long sum(QueryBuilder queryBuilder, String field)
    {

        String indexName = Preconditions.checkNotNull(getIndexName(), "indices must not null");
        RestHighLevelClient client = getRestHighLevelClient();

        Preconditions.checkNotBlank(field, "聚合字段不可为空");

        // 1. 求和信息
        SumAggregationBuilder sumAggregationBuilder = AggregationBuilders.sum(field).field(field);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.aggregation(sumAggregationBuilder);
        searchSourceBuilder.size(0);
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(searchSourceBuilder);

        try
        {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            return AggregationUtil.getRootSumValue(searchResponse, field);

        }
        catch (IOException e)
        {
            log.error("es sum error", e);
            if (exceptionThrow())
            {
                MyAssert.throwException("es sum error");
            }
        }
        return 0L;
    }

    /**
     * 聚合sum查询
     *
     * @param wrapper EsWrapper
     * @param field   聚合字段
     * @return Long
     */
    public Long sumByWrapper(EsWrapper wrapper, String field)
    {
        String indexName = Preconditions.checkNotNull(getIndexName(), "indices must not null");
        wrapper.indices(indexName);
        return count(wrapper.buildQueryBuilder(), field);
    }

    /**
     * es结果转化为实体类list
     *
     * @param searchResponse es response
     * @param clazz          泛型类
     * @param <T>            泛型
     * @return T
     */
    public static <T> List<T> searchResponseToEsEntity(SearchResponse searchResponse, Class<T> clazz)
    {
        SearchHits hits = searchResponse.getHits();
        List<T> list = new ArrayList<>();
        for (SearchHit hit : hits.getHits())
        {
            String json = hit.getSourceAsString();
            T obj = JSONUtil.toBean(json, clazz);
            list.add(obj);
        }
        return list;
    }

}
