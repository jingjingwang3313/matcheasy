package cn.matcheasy.project.modules.demo.mongo.dao;

import cn.matcheasy.project.common.base.dao.mongo.AbstractMongoCommonDao;
import cn.matcheasy.project.modules.demo.mongo.entity.MongoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * @class: MongoDao
 * @author: wangjing
 * @date: 2021/9/16/0016
 * @desc: TODO
 */
@Repository
public class MongoDao extends AbstractMongoCommonDao<MongoEntity>
{
    @Autowired
    @Qualifier("defaultMongoTemplate")
    private MongoTemplate defaultMongoTemplate;

    @Override
    public MongoTemplate getMongoTemplate()
    {
        return defaultMongoTemplate;
    }

}
