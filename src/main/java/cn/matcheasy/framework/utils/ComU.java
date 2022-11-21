package cn.matcheasy.framework.utils;

import cn.hutool.core.map.MapUtil;
import cn.matcheasy.framework.constant.ProjectConst;
import cn.matcheasy.framework.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

/**
 * @class ComU
 * @author: wangjing
 * @date: 2020/11/7/0007
 * @desc: 尽量使用commons, guava, hutool等已集成第三方工具类。
 */
public class ComU
{

    /**
     * 避免工具类实例化
     */
    private ComU()
    {
    }

    /**
     * 获取 request
     */
    public static HttpServletRequest getRequest()
    {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null)
        {
            return null;
        }
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    /**
     * 获取参数值
     */
    public static String getParameter(String name)
    {
        HttpServletRequest request = getRequest();
        return getParameter(request, name);
    }

    /**
     * 获取参数值
     */
    public static String getParameter(HttpServletRequest request, String name)
    {
        if (request == null)
        {
            return null;
        }

        return request.getParameter(name);
    }

    /**
     * 获取request请求值，转化为map
     */
    public static Map<String, Object> getRequestMap(HttpServletRequest request)
    {
        Map<String, Object> requestMap = new HashMap<String, Object>();
        try
        {
            String key = null;
            String value = null;
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements())
            {
                key = String.valueOf(headerNames.nextElement());
                // 指定header存值
                if (StringUtils.equals(ProjectConst.ACCESS_TOKEN, key)
                        || StringUtils.equals(ProjectConst.ACCESS_KEY, key)
                        || StringUtils.equals(ProjectConst.SOURCE, key)
                        || StringUtils.equals(ProjectConst.ACCESS_SECRET, key))
                {
                    value = request.getHeader(key);
                    requestMap.put(key, value);
                }
            }
            Enumeration<String> parameterNames = request.getParameterNames();
            String[] valueArrayTemp = null;
            StringBuilder sb = new StringBuilder();
            while (parameterNames.hasMoreElements())
            {
                key = String.valueOf(parameterNames.nextElement());
                valueArrayTemp = request.getParameterValues(key);
                value = "";
                if (valueArrayTemp.length <= 1)
                {
                    value = request.getParameter(key);
                    requestMap.put(key, value);
                }
                else
                {
                    requestMap.put(key, StringUtils.join(valueArrayTemp, ","));
                }
            }
            /*BufferedReader br;
            br = request.getReader();
            String str, wholeStr = "";
            while ((str = br.readLine()) != null) {
                wholeStr += str;
            }
            if (StringUtils.isNotBlank(wholeStr)) {
                requestMap.putAll(JSON.parseObject(wholeStr, Map.class));
            }*/
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return requestMap;
    }

    /**
     * 生成随机数
     *
     * @param length     生成的个数(长度)
     * @param constitute 生成方式 1:0-9 2:A-Z 3:a-z 4:混合
     * @return
     */
    public static String getRandom(int length, int constitute)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++)
        {
            if (constitute == 1)
            {
                sb.append((int) (Math.random() * 10));// (int)(Math.random()*10)为
                // 0-9
            }
            else if (constitute == 2)
            {
                sb.append((char) ((int) (Math.random() * 26) + 65));// [0,26)
                // +65
            }
            else if (constitute == 3)
            {
                sb.append((char) ((int) (Math.random() * 26) + 97));// [0,26)
                // +97
            }
            else if (constitute == 4)
            {
                int count = (int) (Math.random() * 62);// [0, 62)
                if (count < 10)
                {
                    sb.append(count);
                }
                else if (count >= 10 && count < 36)
                {// 10-35
                    sb.append((char) (count + 55));
                }
                else if (count >= 36 && count < 62)
                {// 36-61
                    sb.append((char) (count + 61));
                }
            }
        }
        return sb.toString();
    }

    /**
     * 生成随机数
     *
     * @param length 生成的个数(长度),首位是1-9
     * @return
     */
    public static String getRandomBylength(int length)
    {
        StringBuffer sb = new StringBuffer();
        Random rand = new Random();
        for (int i = 0; i < length; i++)
        {
            if (i == 0)
            {
                sb.append(rand.nextInt(9) + 1);// 出现1-9的数据
            }
            else
            {
                sb.append((int) (Math.random() * 10));
            }
        }
        return sb.toString();
    }

    /**
     * 将图片转化为64位编码
     *
     * @param src
     * @return
     */
    public static String getImageStr(String src)
    {
        BufferedImage bufferImg = null;
        ByteArrayOutputStream byteArrayStream = null;
        // 获取图片的后缀
        String suffix = src.substring(src.lastIndexOf(".") + 1);
        try
        {
            if (src.contains("http"))
            {
                bufferImg = ImageIO.read(new URL(src).openStream());
            }
            else
            {
                bufferImg = ImageIO.read(new File(src));
            }
            byteArrayStream = new ByteArrayOutputStream();
            ImageIO.write(bufferImg, suffix, byteArrayStream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return "false";
        }
        byte[] data = byteArrayStream.toByteArray();
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }

    /**
     * BigDecimal类型保留指定的小数数位
     *
     * @param bd  BigDecimal
     * @param len 小数位数
     * @return
     */
    public static String formatBigDecimal(BigDecimal bd, int len)
    {
        if (bd == null)
        {
            return "";
        }
        // 设置小数位数，第一个变量是小数位数，第二个变量是取舍方法(四舍五入)
        bd = bd.setScale(len, BigDecimal.ROUND_HALF_UP);
        return bd.toString();
    }

    /**
     * 去掉Float转化成的字符串中把小数点后无用的零去掉
     *
     * @param source
     * @param len    保留位数,用不到可以填null
     * @return
     */
    public static String floatDeletRightZreo(String source, String len)
    {
        if (!StringUtils.isEmpty(len))
        {
            source = String.format("%." + len + "f", Float.valueOf(source));
        }
        if (source.indexOf(".") > 0)
        {
            source = source.replaceAll("0+?$", "");// 去掉后面无用的零
            source = source.replaceAll("[.]$", "");// 如小数点后面全是零则去掉小数点
            return source;
        }
        else
        {
            return source;
        }
    }

    /**
     * Handled by wangjing at 2021/1/21/0021 16:23
     *
     * @Desc 统一返回Result类型data数据转换Map
     */
    public static Map getResultDataToMap(Result result)
    {
        if (StringUtils.equalsIgnoreCase(result.getSuccess(), ProjectConst.TRUE))
        {
            Map<String, Object> map = result.getData();
            Map m = MapUtil.get(map, ProjectConst.RESULT, Map.class);
            return m;
        }
        return new HashMap();
    }

    /**
     * Handled by wangjing at 2021/1/21/0021 16:23
     *
     * @Desc 统一返回Result类型data数据转换List
     */
    public static List getResultDataToList(Result result)
    {
        if (!StringUtils.equalsIgnoreCase(result.getSuccess(), ProjectConst.FALSE))
        {
            Map<String, Object> map = result.getData();
            List list = MapUtil.get(map, ProjectConst.RESULT, List.class);
            return list;
        }
        return new ArrayList();
    }

}
