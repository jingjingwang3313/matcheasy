package cn.matcheasy.framework.result;

/**
 * @class ResultEnum
 * @author: wangjing
 * @date: 2020/11/7/0007
 * @desc: TODO
 */
public enum ResultEnum {

    /**
     * Result枚举定义
     */
    SUCCESS("true", "200", "响应成功!"),

    PARAM_ERROR("false", "400", "请求数据格式错误!"),

    NO_POWER("false", "401", "没有访问权限!"),

    ACCOUNT_EXCEPTION("false", "402", "账户异常,请重新登陆!"),

    NOT_ALLOW("false", "403", "请求不允许!"),

    NOT_FOUND("false", "404", "请求资源不存在!"),

    TOKEN_EXPIRED("false", "405", "access_token过期,请重新获取!"),

    TOKEN_LOSE("false", "406", "access_token参数缺失,请获取!"),

    SERVER_ERROR("false", "500", "服务器内部错误!"),

    BIZ_ERROR("false", "503", "服务系统维护中!");

    private String success;

    private String code;

    private String msg;

    ResultEnum(String success, String code, String msg) {
        this.success = success;
        this.code = code;
        this.msg = msg;
    }

    public String getSuccess() {
        return success;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
