package cn.matcheasy.project.modules.demo.mongo.service;

import cn.matcheasy.framework.result.Result;
import cn.matcheasy.project.modules.demo.mongo.entity.MongoEntity;

/**
 * @class: MongoService
 * @author: wangjing
 * @date: 2021/9/14/0014
 * @desc: TODO
 */
public interface MongoService
{
    Result save();

    Result find();

    MongoEntity findOneById(String id);
}
