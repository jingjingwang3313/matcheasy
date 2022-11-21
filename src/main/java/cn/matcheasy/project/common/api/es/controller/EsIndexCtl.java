package cn.matcheasy.project.common.api.es.controller;

import cn.matcheasy.framework.result.Result;
import cn.matcheasy.project.common.api.es.service.EsIndexService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @class: EsIndexCtl
 * @author: wangjing
 * @date: 2021/5/21/0021
 * @desc: ES索引操作, ES的数据结构特点, 不需要索引更新方法
 */
@Slf4j
@RestController
@RequestMapping("/anon/es/index")
public class EsIndexCtl {

    @Autowired
    private EsIndexService esIndexService;

    /**
     * 创建索引
     */
    @PostMapping("/createIndex")
    public Result createIndex(String indexName) throws Exception {
        Result result = esIndexService.createIndex(indexName);
        return result;
    }

    /**
     * 删除索引
     */
    @DeleteMapping("/deleteIndex")
    public Result deleteIndex(String indexName) throws Exception {
        Result result = esIndexService.deleteIndex(indexName);
        return result;
    }

}
