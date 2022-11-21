package cn.matcheasy.framework.commonbean;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @class: PageBean
 * @author: wangjing
 * @date: 2021/5/6/0006
 * @desc: TODO
 */
@Data
public class PageBean<T> implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long pageNum;//当前页码

    private Long pageSize;//每页条数

    private Long totalCount;//总记录数

    private Long totalPage;//总页数

    private List<T> records = new LinkedList<>();//每页显示的数据集合

    public PageBean()
    {

    }

    public PageBean(Number pageNum, Number pageSize, Number totalPage, Number totalCount, List<T> records)
    {
        this.pageNum = Convert.toLong(pageNum, 1L);
        this.pageSize = Convert.toLong(pageSize, 20L);
        this.totalPage = Convert.toLong(totalPage, 0L);
        this.totalCount = Convert.toLong(totalCount, 0L);
        this.records = ObjectUtil.isNull(records) ? new ArrayList<>() : records;
    }

    public static <T> PageBean<T> build(Number pageNum, Number pageSize, Number totalPage, Number totalCount, List<T> records)
    {
        return new PageBean<>(pageNum, pageSize, totalPage, totalCount, records);
    }

    public static <T> PageBean<T> empty(Number pageNum, Number pageSize)
    {
        return new PageBean<>(pageNum, pageSize, 0, 0, new ArrayList<>());
    }

}
