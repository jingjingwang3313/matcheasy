package cn.matcheasy.project.common.api.es.service;

import cn.matcheasy.framework.result.Result;

/**
 * @class: EsIndexService
 * @author: wangjing
 * @date: 2021/5/24/0024
 * @desc: TODO
 */
public interface EsIndexService {

    Result createIndex(String indexName) throws Exception;

    Result deleteIndex(String indexName) throws Exception;

}
