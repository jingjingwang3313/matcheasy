package cn.matcheasy.framework.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.PageUtil;
import cn.matcheasy.framework.commonbean.PageBean;

import java.util.List;

/**
 * @class: PageUtils
 * @author: wangjing
 * @date: 2021/11/26/0026
 * @desc: TODO
 */
public class PageUtils extends PageUtil
{
    public static <T> PageBean<T> build(Number pageNum, Number pageSize, Number total, List<T> records)
    {
        int totalPage = PageUtil.totalPage(Convert.toInt(total), Convert.toInt(pageSize));
        return PageBean.build(pageNum, pageSize, totalPage, total, records);
    }

    public static <T> PageBean<T> empty(Number pageNum, Number pageSize)
    {
        return PageBean.empty(pageNum, pageSize);
    }
}
