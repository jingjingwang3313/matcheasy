package cn.matcheasy.framework.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @class ThreadValueUtil
 * @author: wangjing
 * @date: 2021/11/6/0006
 * @desc: 线程内存空间
 */
public class ThreadValueUtil
{
    public final static ThreadLocal<Map> threadLocal = new ThreadLocal<>();

    public static void set(String key, Object value)
    {
        Map<String, Object> threadMap = threadLocal.get();
        if (null == threadMap)
        {
            threadMap = new ConcurrentHashMap<>();
            threadLocal.set(threadMap);
        }
        threadMap.put(key, value);
    }

    public static Object get(String key)
    {
        Map<String, Object> threadMap = threadLocal.get();
        return null == threadMap ? null : threadMap.get(key);
    }

}
