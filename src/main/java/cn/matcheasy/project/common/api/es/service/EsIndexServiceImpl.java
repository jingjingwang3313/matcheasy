package cn.matcheasy.project.common.api.es.service;

import cn.matcheasy.framework.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @class: EsIndexServiceImpl
 * @author: wangjing
 * @date: 2021/5/24/0024
 * @desc: TODO
 */
@Slf4j
@Service
public class EsIndexServiceImpl implements EsIndexService {

    @Autowired
    private RestHighLevelClient esClient;

    @Override
    public Result createIndex(String indexName) throws Exception {
        if (!checkIndex(indexName)) {
            // 创建索引配置信息，配置
            Settings settings = Settings.builder()
                    .put("index.number_of_shards", 1)
                    .put("index.number_of_replicas", 0)
                    .build();
            // 新建创建索引请求对象,然后设置索引类型（ES7+ 不存在索引类型）和 mapping 与 index 配置
            CreateIndexRequest request = new CreateIndexRequest(indexName).settings(settings);
            // 执行创建索引
            CreateIndexResponse createIndexResponse = this.esClient.indices().create(request, RequestOptions.DEFAULT);
            // 判断是否创建成功
            boolean isCreated = createIndexResponse.isAcknowledged();
            log.info("是否创建成功：{}", isCreated);
            return Result.success("创建索引 " + indexName + " 成功!");
        }
        return Result.success("索引 " + indexName + " 已存在!");
    }

    @Override
    public Result deleteIndex(String indexName) throws Exception {
        if (checkIndex(indexName)) {
            // 新建删除索引请求对象
            DeleteIndexRequest request = new DeleteIndexRequest(indexName);
            // 执行删除索引
            AcknowledgedResponse acknowledgedResponse = this.esClient.indices().delete(request, RequestOptions.DEFAULT);
            // 判断是否删除成功
            boolean siDeleted = acknowledgedResponse.isAcknowledged();
            log.info("是否删除成功：{}", siDeleted);
            return Result.success("删除索引 " + indexName + " 成功!");
        }
        return Result.success("索引 " + indexName + " 已删除!");
    }

    /**
     * 判断索引是否存在
     */
    public boolean checkIndex(String indexName) throws Exception {
        return this.esClient.indices().exists(new GetIndexRequest(indexName), RequestOptions.DEFAULT);
    }

}
