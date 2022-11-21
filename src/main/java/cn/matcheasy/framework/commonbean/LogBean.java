package cn.matcheasy.framework.commonbean;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @class: LogBean
 * @author: wangjing
 * @date: 2022/2/8/0008
 * @desc: 日志对象
 */
@Data
public class LogBean implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * traceId
     */
    private String traceId;

    /**
     * 日志级别（info,debug,warning,error等）
     */
    private String level;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 系统名称
     */
    private String sysName;

    /**
     * 模块名称
     */
    private String module;

    /**
     * 操作名称（登录，新增，修改，删除，导入）
     */
    private String operation;

    /**
     * 访问ip
     */
    private String ip;

    /**
     * 访问的url
     */
    private String url;

    /**
     * 类名
     */
    private String className;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 执行耗时
     */
    private Long timeCost;

    /**
     * 操作详情
     */
    private String content;

    /**
     * 操作时间
     */
    private Date createTime;

    /**
     * 请求参数
     */
    private String req;

    /**
     * 返回值
     */
    private String resp;

    /**
     * 其他信息-json格式
     */
    private String note;
}
