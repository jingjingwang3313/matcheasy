package cn.matcheasy.framework.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @class UrlUtil
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
public class UrlUtil {
    
    private final static String ENCODE = "UTF-8";
    
    /**
     * 避免工具类实例化
     */
    private UrlUtil() {
    }
    
    /**
     * URL 解码
     *
     * @return String
     */
    public static String getDecodeURL(String url) {
        String result = "";
        try {
            if (StringUtils.isEmpty(url)) {
                return result;
            }
            result = URLDecoder.decode(url, ENCODE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * URL 转码
     *
     * @return String
     */
    public static String getEncodeURL(String url) {
        String result = "";
        try {
            if (StringUtils.isEmpty(url)) {
                return result;
            }
            
            result = URLEncoder.encode(url, ENCODE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
    
}