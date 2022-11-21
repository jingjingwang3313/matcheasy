package cn.matcheasy.framework.constant;

/**
 * @class Constants
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: 定义系统变量名
 */
public interface ProjectConst
{
    // access_token
    public static final String ACCESS_TOKEN = "access_token";

    // 日志跟踪码
    public static final String TRACE_ID = "trace_id";

    public static final String SOURCE = "source";

    public static final String ACCESS_KEY = "access_key";

    public static final String ACCESS_SECRET = "access_secret";

    // 接口统一返回常量
    public static final String RESULT = "result";

    // true
    public static final String TRUE = "true";

    // false
    public static final String FALSE = "false";

    // 文件访问URL
    public static final String FILE_ACCESS_URL = "fileAccessUrl";

    // 文件存储URL
    public static final String FILE_STORAGE_URL = "fileStorageUrl";

    // userId
    public static final String USER_ID = "userId";

    // 登录码
    public static final String LOGIN_CODE = "loginCode";

    // 密码
    public static final String PASSWORD = "password";

    // 确认密码
    public static final String CONFIRM_PASSWORD = "confirmPassword";

    // 用户名
    public static final String USER_NAME = "userName";

    // 角色
    public static final String ROLE = "role";

    // 权限
    public static final String PERMISSION = "permission";

    // 接收验证码手机/邮箱
    public static final String PHONE_OR_EMAIL = "phoneOrEmail";

    /**
     * Handled by wangjing at 2021/5/8/0008 8:57
     *
     * @Desc 系统平台
     */
    public static final String ADMIN = "admin";//管理台

    public static final String COMPANY = "company";//企业应用

    public static final String PERSONAL = "personal";//个人应用

    public static final String OTHERS = "others";//其他

    /**
     * 系统配置属性文件位置
     */
    public static final String SYSTEM_CONFIG_LOCATION = "file:/home/config/systemConfig.properties";
}
