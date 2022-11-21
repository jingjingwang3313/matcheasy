package cn.matcheasy.framework.validate;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.matcheasy.framework.exception.BusinessException;

import java.util.Collection;
import java.util.Map;

/**
 * @class: Preconditions
 * @author: wangjing
 * @date: 2021/11/26/0026
 * @desc: TODO
 */
public class Preconditions
{
    private Preconditions()
    {
    }

    public static void check(boolean expression)
    {
        if (!expression)
        {
            throw new BusinessException("IllegalCondition");
        }
    }

    public static void check(boolean expression, Object errorMessage)
    {
        if (!expression)
        {
            throw new BusinessException(Convert.toStr(errorMessage));
        }
    }

    public static void check(boolean expression, String errorMessageTemplate, Object... errorMessageArgs)
    {
        if (!expression)
        {
            throw new BusinessException(StrUtil.format(errorMessageTemplate, errorMessageArgs));
        }
    }

    public static void checkArgument(boolean expression)
    {
        if (!expression)
        {
            throw new BusinessException("IllegalArgument");
        }
    }

    public static void checkArgument(boolean expression, Object errorMessage)
    {
        if (!expression)
        {
            throw new BusinessException(Convert.toStr(errorMessage));
        }
    }

    public static void checkArgument(boolean expression, String errorMessageTemplate, Object... errorMessageArgs)
    {
        if (!expression)
        {
            throw new BusinessException(StrUtil.format(errorMessageTemplate, errorMessageArgs));
        }
    }

    public static <T> T checkNotNull(T reference)
    {
        if (reference == null)
        {
            throw new BusinessException("IllegalNull");
        }
        return reference;
    }

    public static <T> T checkNotNull(T reference, Object errorMessage)
    {
        if (reference == null)
        {
            throw new BusinessException(Convert.toStr(errorMessage));
        }
        return reference;
    }

    public static <T> T checkNotNull(T reference, String errorMessageTemplate, Object... errorMessageArgs)
    {
        if (reference == null)
        {
            throw new BusinessException(StrUtil.format(errorMessageTemplate, errorMessageArgs));
        }
        return reference;
    }

    public static <T> Collection<T> checkNotEmpty(Collection<T> reference)
    {
        if (reference == null || reference.isEmpty())
        {
            throw new BusinessException("IllegalEmpty");
        }
        return reference;
    }

    public static <T> Collection<T> checkNotEmpty(Collection<T> reference, Object errorMessage)
    {
        if (reference == null || reference.isEmpty())
        {
            throw new BusinessException(Convert.toStr(errorMessage));
        }
        return reference;
    }

    public static <T> Collection<T> checkNotEmpty(Collection<T> reference, String errorMessageTemplate, Object... errorMessageArgs)
    {
        if (reference == null || reference.isEmpty())
        {
            throw new BusinessException(StrUtil.format(errorMessageTemplate, errorMessageArgs));
        }
        return reference;
    }

    public static <K, V> Map<K, V> checkNotEmpty(Map<K, V> reference)
    {
        if (reference == null || reference.isEmpty())
        {
            throw new BusinessException("IllegalEmpty");
        }
        return reference;
    }

    public static <K, V> Map<K, V> checkNotEmpty(Map<K, V> reference, Object errorMessage)
    {
        if (reference == null || reference.isEmpty())
        {
            throw new BusinessException(Convert.toStr(errorMessage));
        }
        return reference;
    }

    public static <K, V> Map<K, V> checkNotEmpty(Map<K, V> reference, String errorMessageTemplate, Object... errorMessageArgs)
    {
        if (reference == null || reference.isEmpty())
        {
            throw new BusinessException(StrUtil.format(errorMessageTemplate, errorMessageArgs));
        }
        return reference;
    }

    public static String checkNotBlank(String reference)
    {
        if (StrUtil.isBlank(reference))
        {
            throw new BusinessException("IllegalBland");
        }
        return reference;
    }

    public static String checkNotBlank(String reference, Object errorMessage)
    {
        if (StrUtil.isBlank(reference))
        {
            throw new BusinessException(Convert.toStr(errorMessage));
        }
        return reference;
    }

    public static String checkNotBlank(String reference, String errorMessageTemplate, Object... errorMessageArgs)
    {
        if (StrUtil.isBlank(reference))
        {
            throw new BusinessException(StrUtil.format(errorMessageTemplate, errorMessageArgs));
        }
        return reference;
    }
}
