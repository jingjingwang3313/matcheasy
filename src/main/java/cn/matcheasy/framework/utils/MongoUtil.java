package cn.matcheasy.framework.utils;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * @class: MongoUtil
 * @author: wangjing
 * @date: 2021/11/26/0026
 * @desc: TODO
 */
@Slf4j
public class MongoUtil
{
    public static String getPrimayryKey(Object obj)
    {
        String pri = null;
        Class<? extends Object> class1 = obj.getClass();
        Field[] fields = class1.getDeclaredFields();
        int pkNum = 0;
        for (int i = 0; i < fields.length; i++)
        {
            if ("id".equals(fields[i].getName()))
            {
                pri = fields[i].getName();
                pkNum++;
            }
            org.springframework.data.annotation.Id id = fields[i].getAnnotation(org.springframework.data.annotation.Id.class);
            if (id != null)
            {
                pri = fields[i].getName();
                pkNum++;
            }
            org.springframework.data.mongodb.core.mapping.Field f = fields[i].getAnnotation(org.springframework.data.mongodb.core.mapping.Field.class);
            if (f != null && "id".equals(f.value()))
            {
                pri = f.value();
                pkNum++;
            }
        }
        if (pkNum != 1)
        {
            pri = null;
            throw new RuntimeException("实体类【" + class1.getName() + "】的主键个数为【" + pkNum + "】，不符合要求");
        }
        return pri;
    }

    public static String getPKValue(Object obj)
    {
        String pk = getPrimayryKey(obj);
        Class<? extends Object> class1 = obj.getClass();

        Method method;
        String value = null;
        try
        {
            method = class1.getMethod("get" + pk.substring(0, 1).toUpperCase() + pk.substring(1));
            value = (String) method.invoke(obj);
        }
        catch (Exception e)
        {
            log.error("获取主键值异常，obj=" + JSONUtil.toJsonStr(obj), e);
            value = null;
        }
        return value;
    }

    public static Update convertOjbectToUpdate(Object obj)
    {
        Update update = new Update();
        setUpdateByObject(update, "", obj);
        return update;
    }

    public static void setUpdateByObject(Update update, String key, Object value)
    {
        try
        {
            if (StringUtils.hasText(key) && checkBaseType(value))
            {
                update.set(key, value);
                return;
            }
            String pkey = "";
            if (StringUtils.hasText(key))
            {
                pkey = key + ".";
            }
            Method[] methods = value.getClass().getDeclaredMethods();
            for (int i = 0; i < methods.length; i++)
            {
                if (methods[i].getName().startsWith("get"))
                {
                    Object v = methods[i].invoke(value);
                    if (v == null)
                    {
                        continue;
                    }
                    String f1 = methods[i].getName().substring(3, 4).toLowerCase();
                    String f2 = methods[i].getName().substring(4);
                    String field = pkey + f1 + f2;
                    setUpdateByObject(update, field, v);
                }
            }
        }
        catch (Exception e)
        {
            log.debug("将实体对象转换成mongodb的Update对象时异常，key=" + key + ",obj=" + JSONUtil.toJsonStr(value) + ",e=" + e);
        }
    }

    public static boolean checkBaseType(Object obj)
    {
        if (obj instanceof Double)
        {
            return true;
        }
        if (obj instanceof double[])
        {
            return true;
        }
        if (obj instanceof Float)
        {
            return true;
        }
        if (obj instanceof float[])
        {
            return true;
        }
        if (obj instanceof Long)
        {
            return true;
        }
        if (obj instanceof long[])
        {
            return true;
        }
        if (obj instanceof Integer)
        {
            return true;
        }
        if (obj instanceof int[])
        {
            return true;
        }
        if (obj instanceof Short)
        {
            return true;
        }
        if (obj instanceof short[])
        {
            return true;
        }
        if (obj instanceof Character)
        {
            return true;
        }
        if (obj instanceof char[])
        {
            return true;
        }
        if (obj instanceof Byte)
        {
            return true;
        }
        if (obj instanceof byte[])
        {
            return true;
        }
        if (obj instanceof Boolean)
        {
            return true;
        }
        if (obj instanceof boolean[])
        {
            return true;
        }
        if (obj instanceof String)
        {
            return true;
        }
        if (obj instanceof Date)
        {
            return true;
        }
        if (obj instanceof Map)
        {
            return true;
        }
        if (obj instanceof Collection)
        {
            return true;
        }
        if (obj instanceof Object[])
        {
            return true;
        }
        return false;
    }

    public static void initCreateTimeAndCTimeStamp(Object obj, boolean initCreateTime)
    {
        Date date = new Date();
        Class<? extends Object> class1 = obj.getClass();
        boolean islower = true;
        try
        {
            class1.getMethod("getcTimeStamp");
        }
        catch (Exception e)
        {
            islower = false;
            log.info("实体类【" + class1.getName() + "】没有getcTimeStamp方法" + ",e=" + e);
        }
        try
        {
            Object v;
            if (islower)
            {
                v = class1.getMethod("getcTimeStamp").invoke(obj);
            }
            else
            {
                v = class1.getMethod("getCTimeStamp").invoke(obj);
            }
            if (v == null)
            {
                Method cTimeStampMethod;
                if (islower)
                {
                    cTimeStampMethod = class1.getMethod("setcTimeStamp", Date.class);
                }
                else
                {
                    cTimeStampMethod = class1.getMethod("setCTimeStamp", Date.class);
                }
                cTimeStampMethod.invoke(obj, date);
            }
        }
        catch (Exception e)
        {
            log.warn("初始化最后修改时间异常，obj=" + JSONUtil.toJsonStr(obj) + ",e=" + e);
        }
        if (!initCreateTime)
        {
            return;
        }
        try
        {
            Object cv = class1.getMethod("getCreateTime").invoke(obj);
            if (cv == null)
            {
                Method createTimeMethod = class1.getMethod("setCreateTime", Date.class);
                createTimeMethod.invoke(obj, date);
            }
        }
        catch (Exception e)
        {
            log.warn("初始化创建时间异常，obj=" + JSONUtil.toJsonStr(obj) + ",e=" + e);
        }
    }
}
