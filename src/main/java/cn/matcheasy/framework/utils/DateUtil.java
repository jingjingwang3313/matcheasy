package cn.matcheasy.framework.utils;

/**
 * @class DateUtil
 * @author: wangjing
 * @date: 2020/11/7/0007
 * @desc: 尽量使用commons, guava, hutool等已集成第三方工具类。
 */
public class DateUtil
{
    public final static String FORMAT_yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
    public final static String FORMAT_yyyyMMddHHmmss = "yyyyMMddHHmmss";

    public final static String FORMAT_yyyy_MM_dd_HH_mm_ss_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    public final static String FORMAT_yyyyMMddHHmmssSSS = "yyyyMMddHHmmssSSS";

    public final static String FORMAT_yyyy_MM_dd = "yyyy-MM-dd";
    public final static String FORMAT_yyyyMMdd = "yyyyMMdd";

    public final static String FORMAT_yyyy_nian_MM_yue_dd_ri_HH_shi_mm_fen_ss_miao = "yyyy年MM月dd日 HH时mm分ss秒";
    public final static String FORMAT_yyyy_nian_MM_yue_mm_ri = "yyyy年MM月dd日";

    /**
     * 避免工具类实例化
     */
    private DateUtil()
    {
    }
}
