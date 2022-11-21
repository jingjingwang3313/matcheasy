package cn.matcheasy.project.modules.demo.es.dao;

import cn.matcheasy.framework.constant.EsIndexConst;
import cn.matcheasy.project.common.base.dao.es.AbstractEsCommonDao;
import cn.matcheasy.project.modules.demo.es.entity.EsEntity;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 * @class: EsDao
 * @author: wangjing
 * @date: 2021/9/16/0016
 * @desc: TODO
 */
@Repository
public class EsDao extends AbstractEsCommonDao<EsEntity>
{
    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    @Override
    public RestHighLevelClient getRestHighLevelClient()
    {
        return restHighLevelClient;
    }

    @Override
    public RestClient getRestClient()
    {
        return null;
    }

    @Override
    public String getIndexName()
    {
        return EsIndexConst.ES_ENTITY;
    }

}
