package cn.matcheasy.framework.utils;

import java.util.regex.Pattern;

/**
 * @class RegExUtil
 * @author: wangjing
 * @date: 2020/11/19/0019
 * @desc: 正则表达式校验工具类
 */
public class RegExUtil {

    /**
     * 避免工具类实例化
     */
    private RegExUtil() {
    }

    //邮箱
    public static final String REGEX_EMAIL =  "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";

    //手机号
    public static final String REGEX_PHONE_NUMBER = "0?(13|14|15|18|17)[0-9]{9}";

    public static boolean isEmail(String email) {
        return Pattern.matches(REGEX_EMAIL, email);
    }

    public static boolean isPhoneNumber(String phone) {
        return Pattern.matches(REGEX_PHONE_NUMBER, phone);
    }

}
