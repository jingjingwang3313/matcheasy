package cn.matcheasy.project.common.base.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.matcheasy.framework.commonbean.PageBean;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.List;

/**
 * @class: BaseController
 * @author: wangjing
 * @date: 2021/5/7/0007
 * @desc: TODO
 */
public class BaseController
{
    // ========================Mybatisplus分页================================

    public IPage startPage(Long pageNum, Long pageSize) throws Exception
    {
        if (ObjectUtil.isNull(pageNum) || pageNum < 1)
        {
            pageNum = 1L;
        }
        if (ObjectUtil.isNull(pageSize) || pageSize < 1)
        {
            pageSize = 10L;
        }
        return new Page(pageNum, pageSize);
    }

    public <T> PageBean returnPage(IPage<T> pageRet) throws Exception
    {
        PageBean<T> pageBean = new PageBean<>();
        pageBean.setPageNum(pageRet.getCurrent());
        pageBean.setPageSize(pageRet.getSize());
        pageBean.setTotalCount(pageRet.getTotal());
        pageBean.setTotalPage(pageRet.getPages());
        pageBean.setRecords(pageRet.getRecords());
        return pageBean;
    }

    // ============================ES分页====================================

    public Long getPageSize(Long pageSize)
    {
        if (ObjectUtil.isNull(pageSize) || pageSize < 1)
        {
            pageSize = 10L;
        }
        return pageSize;
    }

    public Long getPageNum(Long pageNum)
    {
        if (ObjectUtil.isNull(pageNum) || pageNum < 1)
        {
            pageNum = 1L;
        }
        return pageNum;
    }

    public SearchRequest getSearchRequest(String indexName) throws Exception
    {
        return new SearchRequest(indexName);
    }

    public SearchResponse getSearchResponse(RestHighLevelClient restHighLevelClient, SearchRequest searchRequest) throws Exception
    {
        return restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
    }

    public SearchSourceBuilder startEsPage(Long pageNum, Long pageSize) throws Exception
    {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        Long from = (pageNum - 1) * pageSize;
        searchSourceBuilder.from(Convert.toInt(from));
        searchSourceBuilder.size(Convert.toInt(pageSize));
        return searchSourceBuilder;
    }

    public <T> List<T> getEsSearchResult(SearchResponse searchResponse, Class<T> clazz)
    {
        List list = CollectionUtil.newArrayList();
        // 根据状态和数据条数验证是否返回了数据
        if (RestStatus.OK.equals(searchResponse.status()) && searchResponse.getHits().getTotalHits().value > 0)
        {
            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits)
            {
                // 将 JSON 转换成对象
                Object bean = JSON.parseObject(hit.getSourceAsString(), clazz);
                list.add(bean);
            }
        }
        return list;
    }

    public <T> PageBean returnEsPage(Long pageNum, Long pageSize, Long totalCount, List<T> list) throws Exception
    {
        PageBean pageBean = new PageBean();
        pageBean.setPageNum(pageNum);
        pageBean.setPageSize(pageSize);
        pageBean.setTotalCount(totalCount);
        Long totalPage = 1L;
        if (totalCount % pageSize == 0)
        {
            totalPage = totalCount / pageSize;
        }
        else
        {
            totalPage = totalCount / pageSize + 1;
        }
        pageBean.setTotalPage(totalPage);
        pageBean.setRecords(list);
        return pageBean;
    }

}
