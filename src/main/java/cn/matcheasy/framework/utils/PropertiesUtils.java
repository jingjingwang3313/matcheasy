package cn.matcheasy.framework.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

/**
 * @class: PropertiesUtils
 * @author: wangjing
 * @date: 2021/9/13/0013
 * @desc: 配置文件Properties读取工具类
 */
@Slf4j
public class PropertiesUtils
{
    /**
     * 避免工具类实例化
     */
    private PropertiesUtils()
    {
    }

    /**
     * 读取相对路径，根据主键key读取主键的值value
     */
    public static String readRelativeValue(String filePath, String key)
    {
        Properties props = getPropertiesFromRelativeFile(filePath);
        String value = props.getProperty(key);
        return value;
    }

    /**
     * 读取绝对路径，根据主键key读取主键的值value
     */
    public static String readAbsoluteValue(String filePath, String key)
    {
        Properties props = getPropertiesFromAbsoluteFile(filePath);
        String value = props.getProperty(key);
        return value;
    }

    /**
     * @param file 文件相对路径
     * @desc : 从本地读取配置文件加载到Properties中
     **/
    public static Properties getPropertiesFromRelativeFile(String file)
    {
        Properties props = new Properties();
        InputStream in = props.getClass().getResourceAsStream(file);
        try
        {
            props.load(in);
        }
        catch (IOException e)
        {
            log.error("读取文本失败", e);
        }
        return props;
    }


    /**
     * @param file 文件绝对路径
     * @desc : 从本地读取配置文件加载到Properties中
     **/
    public static Properties getPropertiesFromAbsoluteFile(String file)
    {
        Properties props = null;
        InputStream in = null;
        try
        {
            in = new FileInputStream(file);
            if (in != null)
            {
                props = new Properties();
                props.load(in);
            }
        }
        catch (Exception e)
        {
            log.error("异常", e);
        }
        finally
        {
            closeInputStream(in);
        }
        return props;
    }

    /**
     * @param urlStr 文件URL
     * @desc : 从URL读取配置文件加载到Properties中
     **/
    public static Properties getPropertiesFromUrl(String urlStr)
    {
        Properties props = null;
        InputStream in = null;
        URL url;
        URLConnection uConn;
        try
        {
            url = new URL(urlStr);
            uConn = url.openConnection();
            uConn.setUseCaches(false);
            in = uConn.getInputStream();
            props = new Properties();
            props.load(in);
        }
        catch (Exception e)
        {
            log.error("异常", e);
        }
        finally
        {
            closeInputStream(in);
        }
        return props;
    }

    /**
     * @param in 输入流
     * @desc :关闭流
     **/
    private static void closeInputStream(InputStream in)
    {
        if (in != null)
        {
            try
            {
                in.close();
            }
            catch (IOException var2)
            {
                var2.printStackTrace();
            }
        }

    }
}
