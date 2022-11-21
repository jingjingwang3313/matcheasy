package cn.matcheasy.framework.utils;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;

import java.util.List;
import java.util.Map;

/**
 * @class: JsonUtil
 * @author: wangjing
 * @date: 2021/9/10/0010
 * @desc: TODO
 */
public class JsonUtil
{
    /**
     * is json string
     *
     * @param s string
     * @return boolean
     */
    public static boolean isJson(String s)
    {
        return JSONUtil.isJson(s);
    }

    /**
     * Convert Object to JSON
     *
     * @param obj        jsonObj
     * @param dateFormat dataFormat
     * @return String
     */
    public static String toJson(Object obj, String dateFormat)
    {
        if (null == obj)
        {
            return null;
        }
        if (obj instanceof CharSequence)
        {
            return obj.toString();
        }
        JSON parse = JSONUtil.parse(obj, JSONConfig.create().setDateFormat(dateFormat).setOrder(true));
        return JSONUtil.toJsonStr(parse);
    }

    /**
     * Convert Object to JSON
     *
     * @param obj    jsonObj
     * @param config JSONConfig
     * @return String
     */
    public static String toJson(Object obj, JSONConfig config)
    {

        if (null == obj)
        {
            return null;
        }
        if (obj instanceof CharSequence)
        {
            return StrUtil.str((CharSequence) obj);
        }

        JSON parse = JSONUtil.parse(obj, config);
        return JSONUtil.toJsonStr(parse);
    }

    /**
     * Convert Object to JSON
     *
     * @param obj jsonObj
     * @return String
     */
    public static String toJson(Object obj)
    {
        return toJson(obj, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Convert Object to PrettyJson
     *
     * @param obj        jsonObj
     * @param dateFormat dataFormat
     * @return String
     */
    public static String toPrettyJson(Object obj, String dateFormat)
    {

        if (null == obj)
        {
            return null;
        }
        if (obj instanceof CharSequence)
        {
            return StrUtil.str((CharSequence) obj);
        }

        JSON parse = JSONUtil.parse(obj, JSONConfig.create().setDateFormat(dateFormat).setOrder(true));
        return JSONUtil.toJsonPrettyStr(parse);
    }

    /**
     * Convert Object to PrettyJson
     *
     * @param obj jsonObj
     * @return String
     */
    public static String toPrettyJson(Object obj)
    {
        return toPrettyJson(obj, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Convert JSON to Object
     *
     * @param json  jsonStr
     * @param clazz T
     * @return T
     */
    public static <T> T toObj(String json, Class<T> clazz)
    {
        return JSONUtil.toBean(json, clazz);
    }

    /**
     * Convert JSON to String[]
     *
     * @param json jsonStr
     * @return String[]
     */
    public static String[] toArray(String json)
    {
        return JSONUtil.toBean(json, new TypeReference<String[]>()
        {
        }, true);
    }

    /**
     * Convert json to `List<String>`
     *
     * @param json jsonStr
     * @return List<String>
     */
    public static List<String> toList(String json)
    {
        return JSONUtil.toList(json, String.class);
    }

    /**
     * Convert json to `List<T>`
     *
     * @param json  jsonStr
     * @param clazz T
     * @return List<T>
     */
    public static <T> List<T> toList(String json, Class<T> clazz)
    {
        return JSONUtil.toList(json, clazz);
    }

    /**
     * Convert json to `List<Map<K,V>>`
     *
     * @param json   jsonStr
     * @param kClass K
     * @param vClass V
     * @return List<Map < K, V>>
     */
    public static <K, V> List<Map<K, V>> toList(String json, Class<K> kClass, Class<V> vClass)
    {
        return JSONUtil.toBean(json, new TypeReference<List<Map<K, V>>>()
        {
        }, true);
    }

    /**
     * Convert json to `Map<K, V>`
     *
     * @param json   jsonStr
     * @param kClass K
     * @param vClass V
     * @return `Map<K,V>`
     */
    public static <K, V> Map<K, V> toMap(String json, Class<K> kClass, Class<V> vClass)
    {
        return JSONUtil.toBean(json, new TypeReference<Map<K, V>>()
        {
        }, true);
    }

    /**
     * Convert json to `Map<String, T>`
     *
     * @param json   jsonStr
     * @param vClass T
     * @return `Map<String,T>`
     */
    public static <T> Map<String, T> toMap(String json, Class<T> vClass)
    {
        return JSONUtil.toBean(json, new TypeReference<Map<String, T>>()
        {
        }, true);
    }

    /**
     * Convert json to `Map<String, Object>`
     *
     * @param json jsonStr
     * @return `Map<String,Object>`
     */
    public static Map<String, Object> toMap(String json)
    {
        return JSONUtil.toBean(json, new TypeReference<Map<String, Object>>()
        {
        }, true);
    }

    /**
     * Convert json to `Dict`
     *
     * @param json jsonStr
     * @return `Dict`
     */
    public static Dict toDict(String json)
    {
        Map<String, Object> stringObjectMap = JSONUtil.toBean(json, new TypeReference<Map<String, Object>>()
        {
        }, true);
        return new Dict(stringObjectMap);
    }

    /**
     * Convert json to T
     *
     * @param json                      jsonStr
     * @param typeReference<T>(Demo:new typeReference<List<Map<String, String>>>(){})
     * @return T
     */
    public static <T> T toGeneric(String json, TypeReference<T> typeReference)
    {
        return JSONUtil.toBean(json, typeReference, true);
    }

}
