package cn.matcheasy.framework.utils;

import cn.hutool.json.JSONUtil;
import cn.matcheasy.project.modules.sysuser.entity.SysUser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @class SerializeUtils
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
public class SerializeUtil
{

    /**
     * 反序列化
     */
    public static Object deserialize(byte[] bytes)
    {
        Object result = null;
        if (isEmpty(bytes))
        {
            return null;
        }
        try
        {
            ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
            try
            {
                ObjectInputStream objectInputStream = new ObjectInputStream(byteStream);
                try
                {
                    result = objectInputStream.readObject();
                }
                catch (ClassNotFoundException ex)
                {
                    throw new Exception("Failed to deserialize object type", ex);
                }
            }
            catch (Throwable ex)
            {
                throw new Exception("Failed to deserialize", ex);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isEmpty(byte[] data)
    {
        return (data == null || data.length == 0);
    }

    /**
     * 序列化
     */
    public static byte[] serialize(Object object)
    {

        byte[] result = null;

        if (object == null)
        {
            return new byte[0];
        }
        try
        {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(128);
            try
            {
                if (!(object instanceof Serializable))
                {
                    throw new IllegalArgumentException(SerializeUtil.class.getSimpleName() + " requires a Serializable payload " +
                            "but received an object of type [" + object.getClass().getName() + "]");
                }
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteStream);
                objectOutputStream.writeObject(object);
                objectOutputStream.flush();
                result = byteStream.toByteArray();
            }
            catch (Throwable ex)
            {
                throw new Exception("Failed to serialize", ex);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args)
    {
        SysUser sysUser = new SysUser();
        sysUser.setUserName("阿萨德");
        sysUser.setPassword("2133213");
        byte[] serialize = serialize(JSONUtil.toJsonStr(sysUser));
        System.out.println(serialize.toString());
        String str = deserialize(serialize).toString();
        System.out.println(str);
    }
}