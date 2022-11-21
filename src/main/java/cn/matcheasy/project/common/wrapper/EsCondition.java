package cn.matcheasy.project.common.wrapper;

import cn.hutool.core.util.StrUtil;
import org.elasticsearch.index.query.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @class: EsCondition
 * @author: wangjing
 * @date: 2021/11/26/0026
 * @desc: es Dao底层查询条件对象
 */
public class EsCondition implements Logic, Operator, Serializable
{

    private static final String LIKE_SYMBOL = "%";
    private static final String STAR_SYMBOL = "*";

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 条件运算符 等于，不等于，大于，小于，like，范围等
     */
    private int operator;

    /**
     * 逻辑运算符，默认 and
     **/
    private int logic = AND;

    /**
     * 字段值
     */
    private Object value;

    /**
     * 多个值的情况，in，范围
     */
    private List<Object> values = new ArrayList<>();

    /**
     * 用于初始化 '=','!=','>','<' 等
     */
    public EsCondition(String fieldName, Object value, int operator)
    {
        this.fieldName = fieldName;
        this.value = value;
        this.operator = operator;
    }

    /**
     * 用于初始化 'in','not in','between'
     */
    public EsCondition(String fieldName, Object value, List<Object> values, int operator)
    {
        this.fieldName = fieldName;
        this.value = value;
        this.values = values;
        this.operator = operator;
    }

    public EsCondition()
    {

    }

    public String getFieldName()
    {
        return fieldName;
    }

    public void setFieldName(String fieldName)
    {
        this.fieldName = fieldName;
    }

    public int getOperator()
    {
        return operator;
    }

    public void setOperator(int operator)
    {
        this.operator = operator;
    }

    public int getLogic()
    {
        return logic;
    }

    public void setLogic(int logic)
    {
        this.logic = logic;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

    public List<Object> getValues()
    {
        return values;
    }

    public void setValues(List<Object> values)
    {
        this.values = values;
    }

    /**
     * es中的字段，可以特殊处理
     */
    private String buildFieldName(String fieldName)
    {
        return fieldName;
    }

    /**
     * 构建逻辑连接
     */
    private void buildLogic(BoolQueryBuilder boolQueryBuilder, QueryBuilder qb)
    {
        if (logic == AND)
        {
            boolQueryBuilder.must(qb);
            return;
        }

        if (logic == OR)
        {
            boolQueryBuilder.should(qb);
        }
    }

    /**
     * 创建es查询的条件
     */
    public void buildCondition(BoolQueryBuilder boolQueryBuilder)
    {

        if (operator == EQUAL)
        {

            if (value == null)
            {
                ExistsQueryBuilder isCond = QueryBuilders.existsQuery(buildFieldName(fieldName));
                boolQueryBuilder.mustNot(isCond);
                return;
            }

            QueryBuilder qb = QueryBuilders.termQuery(buildFieldName(fieldName), value);
            this.buildLogic(boolQueryBuilder, qb);
            return;
        }

        if (operator == NOT_EQUAL)
        {

            if (value == null)
            {
                ExistsQueryBuilder isCond = QueryBuilders.existsQuery(buildFieldName(fieldName));
                this.buildLogic(boolQueryBuilder, isCond);
                return;
            }

            QueryBuilder qb = QueryBuilders.termQuery(buildFieldName(fieldName), value);
            boolQueryBuilder.mustNot(qb);
            return;
        }

        if (operator == BETWEEN)
        {
            Object paramStartValue = values.get(0);
            Object paramEndValue = values.size() > 1 ? values.get(1) : null;
            QueryBuilder qb = QueryBuilders.rangeQuery(buildFieldName(fieldName))
                    .from(paramStartValue)
                    .to(paramEndValue)
                    .includeLower(true)
                    .includeUpper(true);
            this.buildLogic(boolQueryBuilder, qb);
            return;
        }

        if (operator == LIKE)
        {
            BoolQueryBuilder likeCond = buildLike();
            this.buildLogic(boolQueryBuilder, likeCond);
            return;
        }

        if (operator == NOT_LIKE)
        {
            BoolQueryBuilder likeCond = buildLike();
            boolQueryBuilder.mustNot(likeCond);
            return;
        }

        if (operator == GREATER)
        {
            QueryBuilder qb = QueryBuilders.rangeQuery(buildFieldName(fieldName))
                    .from(value)
                    .includeLower(false);
            this.buildLogic(boolQueryBuilder, qb);
            return;
        }

        if (operator == LESS)
        {
            QueryBuilder qb = QueryBuilders.rangeQuery(buildFieldName(fieldName))
                    .to(value)
                    .includeUpper(false);
            this.buildLogic(boolQueryBuilder, qb);
            return;
        }

        if (operator == GREATER_EQUAL)
        {
            QueryBuilder qb = QueryBuilders.rangeQuery(buildFieldName(fieldName))
                    .from(value)
                    .includeLower(true);
            this.buildLogic(boolQueryBuilder, qb);
            return;
        }

        if (operator == LESS_EQUAL)
        {
            QueryBuilder qb = QueryBuilders.rangeQuery(buildFieldName(fieldName))
                    .to(value)
                    .includeUpper(true);
            this.buildLogic(boolQueryBuilder, qb);
            return;
        }

        if (operator == IN)
        {
            QueryBuilder qb = QueryBuilders.termsQuery(buildFieldName(fieldName), values);
            this.buildLogic(boolQueryBuilder, qb);
            return;
        }

        if (operator == NOT_IN)
        {
            QueryBuilder qb = QueryBuilders.termsQuery(buildFieldName(fieldName), values);
            boolQueryBuilder.mustNot(qb);
        }
    }

    /**
     * 创建like查询条件
     */
    private BoolQueryBuilder buildLike()
    {

        BoolQueryBuilder likeCond = QueryBuilders.boolQuery();
        String val = value.toString();

        if (val.contains(LIKE_SYMBOL))
        {
            MatchPhraseQueryBuilder q1 = QueryBuilders.matchPhraseQuery(buildFieldName(fieldName), value.toString().replace(LIKE_SYMBOL, StrUtil.EMPTY));
            WildcardQueryBuilder q2 = QueryBuilders.wildcardQuery(buildFieldName(fieldName), value.toString().replace(LIKE_SYMBOL, STAR_SYMBOL));
            likeCond.should(q1);
            likeCond.should(q2);
            return likeCond;
        }

        MatchPhraseQueryBuilder q1 = QueryBuilders.matchPhraseQuery(buildFieldName(fieldName), value.toString());
        WildcardQueryBuilder q2 = QueryBuilders.wildcardQuery(buildFieldName(fieldName), STAR_SYMBOL + value.toString() + STAR_SYMBOL);
        likeCond.should(q1);
        likeCond.should(q2);

        return likeCond;
    }
}
