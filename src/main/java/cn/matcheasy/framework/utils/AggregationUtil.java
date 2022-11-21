package cn.matcheasy.framework.utils;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Cardinality;
import org.elasticsearch.search.aggregations.metrics.ParsedSum;
import org.elasticsearch.search.aggregations.metrics.ParsedValueCount;

import java.util.List;

/**
 * @class: AggregationUtil
 * @author: wangjing
 * @date: 2021/11/26/0026
 * @desc: es聚合结果解析
 */
public class AggregationUtil
{

    /**
     * 获取分组结构最外层的Buckets
     *
     * @param response   response
     * @param bucketName bucketName
     * @return List
     */
    public static List<? extends Terms.Bucket> getRootBuckets(SearchResponse response, String bucketName)
    {
        Aggregations aggregations = response.getAggregations();
        Terms terms = aggregations.get(bucketName);
        return terms.getBuckets();
    }

    /**
     * 获取分组的buckets
     *
     * @param aggregations aggregations
     * @param bucketName   bucketName
     * @return List
     */
    public static List<? extends Terms.Bucket> getBuckets(Aggregations aggregations, String bucketName)
    {
        Terms terms = aggregations.get(bucketName);
        return terms.getBuckets();
    }

    /**
     * 获取最外层的cardinality类型的value
     *
     * @param response   response
     * @param bucketName bucketName
     * @return long
     */
    public static long getRootCardinalityValue(SearchResponse response, String bucketName)
    {
        Aggregations aggregations = response.getAggregations();
        Cardinality cardinality = aggregations.get(bucketName);
        return cardinality.getValue();
    }

    /**
     * 获取最外层的sum类型的value
     *
     * @param response   response
     * @param bucketName bucketName
     * @return long
     */
    public static long getRootSumValue(SearchResponse response, String bucketName)
    {
        Aggregations aggregations = response.getAggregations();
        ParsedSum parsedSum = aggregations.get(bucketName);
        return (long) parsedSum.getValue();
    }

    /**
     * 获取最外层的valueCount类型的value
     *
     * @param response   response
     * @param bucketName bucketName
     * @return long
     */
    public static long getRootValueCountValue(SearchResponse response, String bucketName)
    {
        Aggregations aggregations = response.getAggregations();
        if (aggregations == null)
        {
            return 0;
        }
        ParsedValueCount parsedValueCount = aggregations.get(bucketName);
        return parsedValueCount.getValue();
    }

    /**
     * 获取最外层的DateHistogram类型的value
     *
     * @param response   response
     * @param bucketName bucketName
     * @return List
     */
    public static List<? extends Histogram.Bucket> getRootDateHistogramValue(SearchResponse response, String bucketName)
    {
        Aggregations aggregations = response.getAggregations();
        ParsedDateHistogram parsedDateHistogram = aggregations.get(bucketName);
        return parsedDateHistogram.getBuckets();
    }

    /**
     * 获取SubTermsBuckets
     *
     * @param bucket        bucket
     * @param subBucketName subBucketName
     * @return List
     */
    public static List<? extends Terms.Bucket> getSubTermsBuckets(Terms.Bucket bucket, String subBucketName)
    {
        Terms terms = bucket.getAggregations().get(subBucketName);
        return terms.getBuckets();
    }

    /**
     * 获取SubDateHistogramBuckets
     *
     * @param bucket        bucket
     * @param subBucketName subBucketName
     * @return List
     */
    public static List<? extends Histogram.Bucket> getSubDateHistogramBuckets(Terms.Bucket bucket, String subBucketName)
    {
        ParsedDateHistogram parsedDateHistogram = bucket.getAggregations().get(subBucketName);
        return parsedDateHistogram.getBuckets();
    }

    /**
     * 获取valueCount
     *
     * @param bucket bucket
     * @param name   name
     * @return long
     */
    public static long parsedValueCount(Terms.Bucket bucket, String name)
    {
        ParsedValueCount parsedValueCount = bucket.getAggregations().get(name);
        return parsedValueCount.getValue();
    }

    /**
     * 获取valueCount
     *
     * @param bucket bucket
     * @param name   name
     * @return long
     */
    public static long parsedValueCount(Histogram.Bucket bucket, String name)
    {
        ParsedValueCount parsedValueCount = bucket.getAggregations().get(name);
        return parsedValueCount.getValue();
    }

    /**
     * 获取sum
     *
     * @param bucket bucket
     * @param name   name
     * @return Double
     */
    public static Double parsedSum(Terms.Bucket bucket, String name)
    {
        ParsedSum parsedSum = bucket.getAggregations().get(name);
        return parsedSum.getValue();
    }

    /**
     * 获取sum
     *
     * @param bucket bucket
     * @param name   name
     * @return Double
     */
    public static Double parsedSum(Histogram.Bucket bucket, String name)
    {
        ParsedSum parsedSum = bucket.getAggregations().get(name);
        return parsedSum.getValue();
    }

    /**
     * 获取Cardinality
     *
     * @param bucket bucket
     * @param name   name
     * @return long
     */
    public static long parsedCardinality(Terms.Bucket bucket, String name)
    {
        Cardinality cardinality = bucket.getAggregations().get(name);
        return cardinality.getValue();
    }

    /**
     * 获取Cardinality
     *
     * @param bucket bucket
     * @param name   name
     * @return long
     */
    public static long parsedCardinality(Histogram.Bucket bucket, String name)
    {
        Cardinality cardinality = bucket.getAggregations().get(name);
        return cardinality.getValue();
    }
}
