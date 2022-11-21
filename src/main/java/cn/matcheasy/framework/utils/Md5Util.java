package cn.matcheasy.framework.utils;

import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @class Md5Util
 * @author: wangjing
 * @date: 2020/11/10/0010
 * @desc: Md5单向算法加密
 */
public class Md5Util {

    // 用来将字节转换成16进制表示的字符
    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
            'b', 'c', 'd', 'e', 'f'};

    /**
     * 避免工具类实例化
     */
    private Md5Util() {
    }

    /**
     * 对字符串进行MD5加密
     */
    public static String encrypt(String text) {
        MessageDigest msgDigest = null;
        try {
            msgDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("System doesn't support MD5 algorithm.");
        }
        try {
            msgDigest.update(text.getBytes("UTF-8")); // 注意该接口是按照指定编码形式签名
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("System doesn't support your  EncodingException.");
        }
        byte[] bytes = msgDigest.digest();
        String md5Str = new String(encodeHex(bytes));
        return md5Str;
    }

    private static char[] encodeHex(byte[] data) {
        int l = data.length;
        char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS[0x0F & data[i]];
        }
        return out;
    }

    public static void main(String[] args) {
        System.out.println(Md5Util.encrypt("123456"));
        System.out.println(DigestUtils.md5DigestAsHex("123456".getBytes()));
    }

}
