package cn.matcheasy.framework.log;

import cn.hutool.core.convert.Convert;
import cn.matcheasy.framework.constant.ProjectConst;
import cn.matcheasy.framework.utils.ThreadValueUtil;
import cn.matcheasy.framework.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @class ProjectLog
 * @author: wangjing
 * @date: 2020/12/6/0006
 * @desc: 追踪日志 ProjectLog.info(StrUtil.format("{}",""));
 */
@Slf4j
public class MatcheasyLog
{
    // -----------------------------------------error-----------------------------------------
    public static void error(String message)
    {
        error("", message, null);
    }

    public static void error(String serial, String message)
    {
        error("", message, null);
    }

    public static void error(String message, Throwable e)
    {
        error("", message, e);
    }

    public static void error(String serial, String message, Throwable e)
    {
        String trace_id = Convert.toStr(ThreadValueUtil.get(ProjectConst.TRACE_ID), UUIDUtil.fastSimpleUUID());
        log.error("【{}】【{}】【{}】", trace_id, serial, message, e);
    }

    // -----------------------------------------warn-----------------------------------------
    public static void warn(String message)
    {
        warn("", message, null);
    }

    public static void warn(String serial, String message)
    {
        warn("", message, null);
    }

    public static void warn(String message, Throwable e)
    {
        warn("", message, e);
    }

    public static void warn(String serial, String message, Throwable e)
    {
        String trace_id = Convert.toStr(ThreadValueUtil.get(ProjectConst.TRACE_ID), UUIDUtil.fastSimpleUUID());
        log.warn("【{}】【{}】【{}】", trace_id, serial, message, e);
    }

    // -----------------------------------------info-----------------------------------------
    public static void info(String message)
    {
        info("", message, null);
    }

    public static void info(String serial, String message)
    {
        info("", message, null);
    }

    public static void info(String message, Throwable e)
    {
        info("", message, e);
    }

    public static void info(String serial, String message, Throwable e)
    {
        String trace_id = Convert.toStr(ThreadValueUtil.get(ProjectConst.TRACE_ID), UUIDUtil.fastSimpleUUID());
        log.info("【{}】【{}】【{}】", trace_id, serial, message, e);
    }

    // -----------------------------------------debug-----------------------------------------
    public static void debug(String message)
    {
        debug("", message, null);
    }

    public static void debug(String serial, String message)
    {
        debug("", message, null);
    }

    public static void debug(String message, Throwable e)
    {
        debug("", message, e);
    }

    public static void debug(String serial, String message, Throwable e)
    {
        String trace_id = Convert.toStr(ThreadValueUtil.get(ProjectConst.TRACE_ID), UUIDUtil.fastSimpleUUID());
        log.debug("【{}】【{}】【{}】", trace_id, serial, message, e);
    }
}
