package cn.matcheasy.framework.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * @class: BeanUtil
 * @author: wangjing
 * @date: 2021/4/29/0029
 * @desc: 对象或集合同名属性转换
 */
public class BeanCopyUtil extends BeanUtil
{

    /**
     * 避免工具类实例化
     */
    private BeanCopyUtil()
    {
    }

    /**
     * List<source>转换List<clazz>
     */
    public static <T> List<T> convertList(List<?> source, Class<T> clazz)
    {
        String oldOb = JSON.toJSONString(source);
        return JSON.parseArray(oldOb, clazz);
    }

    /**
     * 对象source转换对象clazz
     */
    public static <T> T convertBean(Object source, Class<T> clazz)
    {
        String oldOb = JSON.toJSONString(source);
        return JSON.parseObject(oldOb, clazz);
    }

    /**
     * copyProperties
     *
     * @param source        source
     * @param target        target
     * @param ignoredFields ignoredFields
     */
    public static void copyProperties(Object source, Object target, String... ignoredFields)
    {
        CopyOptions copyOptions = CopyOptions.create()
                .setIgnoreProperties(ignoredFields);
        BeanUtil.copyProperties(source, target, copyOptions);
    }

    /**
     * copyPropertiesIgnoredNull
     *
     * @param source        source
     * @param target        target
     * @param ignoredFields ignoredFields
     */
    public static void copyPropertiesIgnoredNull(Object source, Object target, String... ignoredFields)
    {
        CopyOptions copyOptions = CopyOptions.create()
                .setIgnoreProperties(ignoredFields)
                .setIgnoreNullValue(true);
        BeanUtil.copyProperties(source, target, copyOptions);
    }

    /**
     * copyPropertiesIgnoredError
     *
     * @param source        source
     * @param target        target
     * @param ignoredFields ignoredFields
     */
    public static void copyPropertiesIgnoredError(Object source, Object target, String... ignoredFields)
    {
        CopyOptions copyOptions = CopyOptions.create()
                .setIgnoreProperties(ignoredFields)
                .setIgnoreError(true);
        BeanUtil.copyProperties(source, target, copyOptions);
    }

    /**
     * copyPropertiesIgnoredNullAndError
     *
     * @param source        source
     * @param target        target
     * @param ignoredFields ignoredFields
     */
    public static void copyPropertiesIgnoredNullAndError(Object source, Object target, String... ignoredFields)
    {
        CopyOptions copyOptions = CopyOptions.create()
                .setIgnoreProperties(ignoredFields)
                .setIgnoreError(true)
                .setIgnoreNullValue(true);
        BeanUtil.copyProperties(source, target, copyOptions);
    }

    /**
     * copyProperties
     *
     * @param source        source
     * @param target        target
     * @param ignoredError  是否忽略错误
     * @param ignoredNull   是否忽略null
     * @param ignoredFields 忽略的字段
     */
    public static void copyProperties(Object source, Object target, boolean ignoredError, boolean ignoredNull, String... ignoredFields)
    {
        CopyOptions copyOptions = CopyOptions.create()
                .setIgnoreProperties(ignoredFields)
                .setIgnoreError(ignoredError)
                .setIgnoreNullValue(ignoredNull);
        BeanUtil.copyProperties(source, target, copyOptions);
    }

    /**
     * copyProperties
     *
     * @param source        source
     * @param target        target
     * @param ignoredError  是否忽略错误
     * @param ignoredNull   是否忽略null
     * @param ignoreCase    是否忽略大小写
     * @param ignoredFields 忽略的字段
     */
    public static void copyProperties(Object source, Object target, boolean ignoredError, boolean ignoredNull, boolean ignoreCase, String... ignoredFields)
    {
        CopyOptions copyOptions = CopyOptions.create()
                .setIgnoreProperties(ignoredFields)
                .setIgnoreError(ignoredError)
                .setIgnoreCase(ignoreCase)
                .setIgnoreNullValue(ignoredNull);
        BeanUtil.copyProperties(source, target, copyOptions);
    }
}
