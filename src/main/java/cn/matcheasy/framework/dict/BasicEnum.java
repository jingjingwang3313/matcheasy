package cn.matcheasy.framework.dict;

import java.util.HashMap;
import java.util.Map;

/**
 * @class: BasicEnum
 * @author: wangjing
 * @date: 2021/11/25/0025
 * @desc: TODO
 */
public class BasicEnum
{
    public static Map<String, String> respTypeHashMap = new HashMap<>();

    static
    {
        for (RespType respType : RespType.values())
        {
            respTypeHashMap.put(respType.key, respType.value);
        }
    }

    /**
     * 响应类型枚举
     */
    public enum RespType
    {

        SUCCESS("success", "成功"),
        FAILED("false", "失败");

        private String key;

        private String value;

        RespType(String key, String value)
        {
            this.key = key;
            this.value = value;
        }

        public String getKey()
        {
            return key;
        }

        public String getValue()
        {
            return value;
        }

    }

    /**
     * 获取枚举值list
     */
    /*public static void main(String[] args) throws Exception
    {
        Class<?> clazz = BasicEnum.RespType.class;
        Object[] objects = clazz.getEnumConstants();
        Method getKey = clazz.getMethod("getKey");
        Method getValue = clazz.getMethod("getValue");
        List<Map<String, Object>> list = CollectionUtil.newArrayList();
        Map<String, Object> map = null;
        for (Object obj : objects)
        {
            map = MapUtil.newHashMap();
            map.put("code", obj);
            map.put("key", getKey.invoke(obj));
            map.put("value", getValue.invoke(obj));
            list.add(map);
        }
        System.out.println(JSONUtil.toJsonPrettyStr(list));
    }*/

}
