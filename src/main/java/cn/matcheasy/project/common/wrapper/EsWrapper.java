package cn.matcheasy.project.common.wrapper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.text.StrSpliter;
import cn.hutool.core.util.StrUtil;
import cn.matcheasy.framework.utils.PageUtils;
import cn.matcheasy.framework.validate.Preconditions;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @class: EsWrapper
 * @author: wangjing
 * @date: 2021/11/26/0026
 * @desc: 装的es wrapper
 */
public class EsWrapper implements Logic, Operator, Serializable
{

    /**
     * 查询条件
     */
    private List<EsCondition> conditions = new ArrayList<>();

    /**
     * 分组条件
     */
    private List<AggregationBuilder> aggregations = new ArrayList<>();

    /**
     * 索引
     */
    private List<String> indices;

    /**
     * routing
     */
    private String routing;

    /**
     * 排序， example：name desc
     */
    private String order;

    /**
     * 分页 from
     */
    private Integer pageNum;

    /**
     * 分页 size
     */
    private Integer pageSize;

    /**
     * 是否显示version
     */
    private Boolean version;

    /**
     * includes
     */
    private List<String> includes;

    /**
     * excludes
     */
    private List<String> excludes;

    /**
     * 用于初始化最常用的 '='
     */
    public EsWrapper eq(boolean condition, String fieldName, Object value)
    {
        if (condition)
        {
            this.conditions.add(new EsCondition(fieldName, value, EQUAL));
        }
        return this;
    }

    /**
     * 用于初始化最常用的 '!='
     */
    public EsWrapper notEq(boolean condition, String fieldName, Object value)
    {
        if (condition)
        {
            this.conditions.add(new EsCondition(fieldName, value, NOT_EQUAL));
        }
        return this;
    }

    /**
     * 用于初始化最常用的 'like'
     */
    public EsWrapper like(boolean condition, String fieldName, Object value)
    {
        if (condition)
        {
            this.conditions.add(new EsCondition(fieldName, value, LIKE));
        }
        return this;
    }

    /**
     * 用于初始化最常用的 '>'
     */
    public EsWrapper gt(boolean condition, String fieldName, Object value)
    {
        if (condition)
        {
            this.conditions.add(new EsCondition(fieldName, value, GREATER));
        }
        return this;
    }

    /**
     * 用于初始化最常用的 '<'
     */
    public EsWrapper lt(boolean condition, String fieldName, Object value)
    {
        if (condition)
        {
            this.conditions.add(new EsCondition(fieldName, value, LESS));
        }
        return this;
    }

    /**
     * 用于初始化最常用的 '>='
     */
    public EsWrapper gte(boolean condition, String fieldName, Object value)
    {
        if (condition)
        {
            this.conditions.add(new EsCondition(fieldName, value, GREATER_EQUAL));
        }
        return this;
    }

    /**
     * 用于初始化最常用的 '<='
     */
    public EsWrapper lte(boolean condition, String fieldName, Object value)
    {
        if (condition)
        {
            this.conditions.add(new EsCondition(fieldName, value, LESS_EQUAL));
        }
        return this;
    }

    /**
     * 用于初始化最常用的 'in'
     */
    public EsWrapper in(boolean condition, String fieldName, Collection<?> values)
    {
        if (condition)
        {
            this.conditions.add(new EsCondition(fieldName, null, convert(values), IN));
        }
        return this;
    }

    /**
     * 用于初始化最常用的 'not in'
     */
    public EsWrapper notIn(boolean condition, String fieldName, Collection<?> values)
    {
        if (condition)
        {
            this.conditions.add(new EsCondition(fieldName, null, convert(values), NOT_IN));
        }
        return this;
    }

    /**
     * 用于初始化最常用的 'BETWEEN'
     */
    public EsWrapper between(boolean condition, String fieldName, Collection<?> values)
    {
        if (condition)
        {
            this.conditions.add(new EsCondition(fieldName, null, convert(values), BETWEEN));
        }
        return this;
    }

    /**
     * 用于初始化最常用的 'BETWEEN'
     */
    public EsWrapper between(boolean condition, String fieldName, Object value1, Object value2)
    {
        if (condition)
        {
            this.conditions.add(new EsCondition(fieldName, null, convert(ListUtil.toList(value1, value2)), BETWEEN));
        }
        return this;
    }

    /**
     * 用于初始化 order by
     */
    public EsWrapper orderBy(boolean condition, String order)
    {
        if (condition)
        {
            this.order = order;
        }
        return this;
    }

    /**
     * 用于初始化 aggregation
     */
    public EsWrapper aggregation(boolean condition, AggregationBuilder... aggregationBuilder)
    {
        if (condition)
        {
            this.aggregations.addAll(ListUtil.toList(aggregationBuilder));
        }
        return this;
    }

    /**
     * 用于初始化 condition
     */
    public EsWrapper condition(boolean condition, EsCondition... esCondition)
    {
        if (condition)
        {
            this.conditions.addAll(ListUtil.toList(esCondition));
        }
        return this;
    }

    /**
     * 用于初始化 routing
     */
    public EsWrapper routing(boolean condition, String routing)
    {
        if (condition)
        {
            this.routing = routing;
        }
        return this;
    }

    /**
     * 用于初始化是否显示 version
     */
    public EsWrapper version(boolean condition, Boolean version)
    {
        if (condition)
        {
            this.version = version;
        }
        return this;
    }

    /**
     * 用于初始化 includes
     */
    public EsWrapper includes(boolean condition, String... includes)
    {
        if (condition)
        {
            if (this.includes == null)
            {
                this.includes = new ArrayList<>();
            }
            this.includes.addAll(ListUtil.toList(includes));
        }
        return this;
    }

    /**
     * 用于初始化 excludes
     */
    public EsWrapper excludes(boolean condition, String... excludes)
    {
        if (condition)
        {
            if (this.excludes == null)
            {
                this.excludes = new ArrayList<>();
            }
            this.excludes.addAll(ListUtil.toList(excludes));
        }
        return this;
    }

    /**
     * 用于初始化 pageNum
     */
    public EsWrapper pageNum(boolean condition, Integer pageNum)
    {
        if (condition)
        {
            this.pageNum = pageNum;
        }
        return this;
    }

    /**
     * 用于初始化 pageSize
     */
    public EsWrapper pageSize(boolean condition, Integer pageSize)
    {
        if (condition)
        {
            this.pageSize = pageSize;
        }
        return this;
    }

    /**
     * 用于初始化索引
     */
    public EsWrapper indices(String... indices)
    {
        if (this.indices == null)
        {
            this.indices = new ArrayList<>();
        }
        this.indices.addAll(ListUtil.toList(indices));
        return this;
    }

    /**
     * 用于初始化最常用的 '='
     */
    public EsWrapper eq(String fieldName, Object value)
    {
        return eq(true, fieldName, value);
    }

    /**
     * 用于初始化最常用的 '!='
     */
    public EsWrapper notEq(String fieldName, Object value)
    {
        return notEq(true, fieldName, value);
    }

    /**
     * 用于初始化最常用的 'like'
     */
    public EsWrapper like(String fieldName, Object value)
    {
        return like(true, fieldName, value);
    }

    /**
     * 用于初始化最常用的 '>'
     */
    public EsWrapper gt(String fieldName, Object value)
    {
        return gt(true, fieldName, value);
    }

    /**
     * 用于初始化最常用的 '<'
     */
    public EsWrapper lt(String fieldName, Object value)
    {
        return lt(true, fieldName, value);
    }

    /**
     * 用于初始化最常用的 '>='
     */
    public EsWrapper gte(String fieldName, Object value)
    {
        return gte(true, fieldName, value);
    }

    /**
     * 用于初始化最常用的 '<='
     */
    public EsWrapper lte(String fieldName, Object value)
    {
        return lte(true, fieldName, value);
    }

    /**
     * 用于初始化最常用的 'in'
     */
    public EsWrapper in(String fieldName, Collection<?> values)
    {
        return in(true, fieldName, values);
    }

    /**
     * 用于初始化最常用的 'not in'
     */
    public EsWrapper notIn(String fieldName, Collection<?> values)
    {
        return notIn(true, fieldName, values);
    }

    /**
     * 用于初始化最常用的 'BETWEEN'
     */
    public EsWrapper between(String fieldName, Collection<?> values)
    {
        return between(true, fieldName, values);
    }

    /**
     * 用于初始化最常用的 'BETWEEN'
     */
    public EsWrapper between(String fieldName, Object value1, Object value2)
    {
        return between(true, fieldName, value1, value2);
    }

    /**
     * 用于初始化order by
     */
    public EsWrapper orderBy(String order)
    {
        return orderBy(true, order);
    }

    /**
     * 用于初始化aggregation
     */
    public EsWrapper aggregation(AggregationBuilder... aggregationBuilder)
    {
        return aggregation(true, aggregationBuilder);
    }

    /**
     * 用于初始化conditions
     */
    public EsWrapper condition(EsCondition... esCondition)
    {
        return condition(true, esCondition);
    }

    /**
     * 用于初始化from
     */
    public EsWrapper pageNum(Integer pageNum)
    {
        return pageNum(true, pageNum);
    }

    /**
     * 用于初始化size
     */
    public EsWrapper pageSize(Integer pageSize)
    {
        return pageSize(true, pageSize);
    }

    /**
     * 用于初始化routing
     */
    public EsWrapper routing(String routing)
    {
        return routing(true, routing);
    }

    /**
     * 是否显示version
     */
    public EsWrapper version(Boolean version)
    {
        return version(true, version);
    }

    /**
     * includes
     */
    public EsWrapper includes(String... includes)
    {
        return includes(true, includes);
    }

    /**
     * excludes
     */
    public EsWrapper excludes(String... excludes)
    {
        return excludes(true, excludes);
    }

    /**
     * 生成 es 排序
     *
     * @param sourceBuilder sourceBuilder
     */
    public void buildSearchOrder(SearchSourceBuilder sourceBuilder)
    {

        if (StrUtil.isBlank(order))
        {
            return;
        }

        String[] orderArray = StrSpliter.splitToArray(order, StrUtil.C_COMMA, 0, true, true);
        for (String o : orderArray)
        {

            String[] fieldArray = StrSpliter.splitToArray(o, StrUtil.SPACE, 0, true, true);
            String field = fieldArray[0];

            String orderType = fieldArray.length > 1 ? fieldArray[1] : SortOrder.ASC.toString();
            sourceBuilder.sort(SortBuilders.fieldSort(field).order(SortOrder.ASC.toString().equalsIgnoreCase(orderType) ? SortOrder.ASC : SortOrder.DESC));
        }
    }

    /**
     * 生成es查询条件 QueryBuilder
     *
     * @return QueryBuilder
     */
    public QueryBuilder buildQueryBuilder()
    {

        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();

        //如果没有conditions查询条件，查询全部
        if (CollUtil.isEmpty(conditions))
        {
            boolBuilder.must(QueryBuilders.matchAllQuery());
            return boolBuilder;
        }

        for (EsCondition condition : conditions)
        {
            if (condition == null)
            {
                continue;
            }
            condition.buildCondition(boolBuilder);
        }

        return boolBuilder;
    }

    /**
     * 生成 es SearchRequest
     * 包含：检索条件，索引，排序，分页，分组，routing，includes，excludes 等
     *
     * @return SearchRequest
     */
    public SearchRequest buildSearchRequest()
    {

        SearchRequest request = new SearchRequest();

        // 索引
        String[] indices = Preconditions.checkNotNull(this.getIndices(), "indices must not null");
        request.indices(indices);

        // routing
        if (StrUtil.isNotBlank(routing))
        {
            request.routing(routing);
        }

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 生成es查询条件 BoolQueryBuilder
        QueryBuilder boolQueryBuilder = buildQueryBuilder();
        searchSourceBuilder.query(boolQueryBuilder);

        // 生成 es 排序
        buildSearchOrder(searchSourceBuilder);

        // 分页from，size
        if (pageNum != null)
        {
            int start = PageUtils.getStart(pageNum, pageSize);
            searchSourceBuilder.from(start);
        }

        if (pageSize != null)
        {
            searchSourceBuilder.size(pageSize);
        }

        // version
        if (version != null)
        {
            searchSourceBuilder.version(version);
        }

        searchSourceBuilder.fetchSource(this.getIncludes(), this.getExcludes());

        // 分组
        if (CollUtil.isNotEmpty(aggregations))
        {
            aggregations.forEach(searchSourceBuilder::aggregation);
            searchSourceBuilder.size(0);
        }

        request.source(searchSourceBuilder);
        request.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));

        return request;
    }

    /**
     * get 方法
     */
    public List<EsCondition> getConditions()
    {
        return conditions;
    }

    public List<AggregationBuilder> getAggregations()
    {
        return aggregations;
    }

    public String getRouting()
    {
        return routing;
    }

    public String getOrder()
    {
        return order;
    }

    public Integer getPageSize()
    {
        return pageSize;
    }

    public Integer getpageNum()
    {
        return pageNum;
    }

    public String[] getIndices()
    {
        if (this.indices == null)
        {
            return null;
        }
        return this.indices.toArray(new String[0]);
    }

    public Boolean getVersion()
    {
        return version;
    }

    public String[] getIncludes()
    {
        if (this.includes == null)
        {
            return null;
        }
        return this.includes.toArray(new String[0]);
    }

    public String[] getExcludes()
    {
        if (this.excludes == null)
        {
            return null;
        }
        return this.excludes.toArray(new String[0]);
    }

    private List<Object> convert(Collection<?> values)
    {

        if (values instanceof List)
        {
            return Convert.toList(Object.class, values);
        }

        return new ArrayList<>(values);
    }

    public static EsWrapper build()
    {
        return new EsWrapper();
    }
}
