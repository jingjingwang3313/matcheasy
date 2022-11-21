package cn.matcheasy.framework.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.matcheasy.framework.constant.ProjectConst;
import cn.matcheasy.framework.exception.BusinessException;

import java.util.Collection;

/**
 * @class: MyAssert
 * @author: wangjing
 * @date: 2021/9/16/0016
 * @desc: TODO
 */
public class MyAssert
{

    private MyAssert()
    {
    }

    public static void isNotBlank(String str, String msg) throws Exception
    {
        isTrue(StrUtil.isNotBlank(str), msg);
    }

    public static void isNotNull(Object object, String msg) throws Exception
    {
        isTrue(null != object, msg);
    }

    public static void isNotEmpty(Collection coll, String msg) throws Exception
    {
        isTrue(CollectionUtil.isNotEmpty(coll), msg);
    }

    public static void isTrue(boolean boo, String msg) throws Exception
    {
        if (!boo)
        {
            throw new Exception(msg);
        }
    }

    public static void throwException(String message, Object... parameters)
    {
        throwException(ProjectConst.FALSE, StrUtil.format(message, parameters));
    }

    public static void throwException(String status, String message, Object... parameters)
    {
        throw new BusinessException(status, StrUtil.format(message, parameters));
    }


}
