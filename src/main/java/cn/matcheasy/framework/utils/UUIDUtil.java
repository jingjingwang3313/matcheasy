package cn.matcheasy.framework.utils;

import cn.hutool.core.lang.UUID;

/**
 * @class UUIDUtil
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
public class UUIDUtil {

    /**
     * 避免工具类实例化
     */
    private UUIDUtil() {
    }

    /**
     * 获取随机UUID
     * @return 随机UUID
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 简化的UUID，去掉了横线
     * @return 简化的UUID，去掉了横线
     */
    public static String simpleUUID() {
        return UUID.randomUUID().toString(true);
    }

    /**
     * 获取随机UUID，使用性能更好的ThreadLocalRandom生成UUID
     * @return 随机UUID
     */
    public static String fastUUID() {
        return UUID.fastUUID().toString();
    }

    /**
     * 简化的UUID，去掉了横线，使用性能更好的ThreadLocalRandom生成UUID
     * @return 简化的UUID，去掉了横线
     */
    public static String fastSimpleUUID() {
        return UUID.fastUUID().toString(true);
    }

}
