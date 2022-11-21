package cn.matcheasy.project.modules.demo.mongo.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.RandomUtil;
import cn.matcheasy.framework.result.Result;
import cn.matcheasy.framework.utils.UUIDUtil;
import cn.matcheasy.project.modules.demo.mongo.dao.MongoDao;
import cn.matcheasy.project.modules.demo.mongo.entity.MongoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @class: MongoServiceImpl
 * @author: wangjing
 * @date: 2021/9/14/0014
 * @desc: TODO
 */
@Service
public class MongoServiceImpl implements MongoService
{
    @Autowired
    @Qualifier("defaultMongoTemplate")
    private MongoTemplate defaultMongoTemplate;

    @Autowired
    @Qualifier("otherMongoTemplate")
    private MongoTemplate otherMongoTemplate;

    @Autowired
    private MongoDao mongoDao;

    // mongo副本集才有事务
    // @Transactional(value = "defaultTransactionManager")
    @Override
    public Result save()
    {
        for (int i = 1; i < 6; i++)
        {
            MongoEntity mongoEntity = new MongoEntity();
            mongoEntity.setId(UUIDUtil.fastSimpleUUID());
            mongoEntity.setItg(RandomUtil.randomInt());
            mongoEntity.setLg(RandomUtil.randomLong());
            mongoEntity.setFlt((float) RandomUtil.randomDouble());
            mongoEntity.setDub(RandomUtil.randomDouble());
            mongoEntity.setStr(UUIDUtil.fastSimpleUUID());
            mongoEntity.setBoo(false);
            mongoEntity.setDate(new Date());
            mongoEntity.setList(CollectionUtil.newArrayList(
                    Dict.create().set("key1", "value1")
                            .set("key2", "value2")));
            if (i % 2 == 0)
            {
                defaultMongoTemplate.save(mongoEntity);
            }
            else
            {
                otherMongoTemplate.save(mongoEntity);
            }
        }
        // Integer j = 1 / 0;
        return Result.success();
    }

    @Override
    public Result find()
    {
        Query query = new Query();
        Criteria criteria = new Criteria();
        query.addCriteria(criteria);
        //query.skip(3);
        //query.limit(9);
        //query.fields().include("id").include("itg").include("str").include("boo").include("date");
        List<MongoEntity> mongoEntities = otherMongoTemplate.find(query, MongoEntity.class);

        return Result.success(Dict.create().set("mongoEntities", mongoEntities));
    }

    @Override
    public MongoEntity findOneById(String id)
    {
        return mongoDao.getById(id);
    }
}
