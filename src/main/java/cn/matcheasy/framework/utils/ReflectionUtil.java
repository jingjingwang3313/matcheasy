package cn.matcheasy.framework.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @class: ReflectionUtil
 * @author: wangjing
 * @date: 2021/11/26/0026
 * @desc: TODO
 */
public class ReflectionUtil
{
    private static final Logger logger = LoggerFactory.getLogger(ReflectionUtil.class);

    public static Class<?> getSuperClassGenericType(final Class<?> clazz, final int index)
    {

        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType))
        {
            logger.warn(String.format("Warn: %s's superclass not ParameterizedType", clazz.getSimpleName()));
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index < params.length && index >= 0)
        {

            if (!(params[index] instanceof Class))
            {
                logger.warn(String.format("Warn: %s not set the actual class on superclass generic parameter", clazz.getSimpleName()));
                return Object.class;
            }

            return (Class) params[index];
        }

        logger.warn(String.format("Warn: Index: %s, Size of %s's Parameterized Type: %s .", index, clazz.getSimpleName(), params.length));
        return Object.class;
    }

}
