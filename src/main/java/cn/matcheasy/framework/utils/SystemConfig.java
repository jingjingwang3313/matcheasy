package cn.matcheasy.framework.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import cn.matcheasy.framework.constant.ProjectConst;

/**
 * @class SystemConfig
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: 获取系统配置属性工具类
 */
public class SystemConfig
{
    /**
     * 静态内部类单例模式
     */
    private static class SystemConfigHolder
    {
        private static SystemConfig INSTANCE = new SystemConfig();
    }

    private static SystemConfig getInstance()
    {
        return SystemConfigHolder.INSTANCE;
    }

    private final Setting setting;

    private SystemConfig()
    {
        setting = new Setting(FileUtil.file(ProjectConst.SYSTEM_CONFIG_LOCATION), CharsetUtil.CHARSET_UTF_8, true);
        setting.autoLoad(true);
    }

    /**
     * 获取setting对象，自由调用其它方法
     *
     * @return Setting
     */
    public static Setting getSetting()
    {
        return getInstance().setting;
    }

    /**
     * 读取配置文件的数据
     *
     * @param key key
     * @return String
     */
    public static String getProperty(String key)
    {
        return getProperty(key, null);
    }

    /**
     * 读取配置文件的数据
     *
     * @param key        key
     * @param defaultVal 默认值
     * @return String
     */
    public static String getProperty(String key, String defaultVal)
    {
        String str = getInstance().setting.getStr(key);
        if (str != null)
        {
            return StrUtil.trim(str);
        }
        return defaultVal;
    }

}
