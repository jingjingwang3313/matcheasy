package cn.matcheasy.project.modules.demo.es.service;

import cn.matcheasy.project.modules.demo.es.entity.EsEntity;

/**
 * @class: EsService
 * @author: wangjing
 * @date: 2021/11/29/0029
 * @desc: TODO
 */
public interface EsService
{
    EsEntity findOneById(String id);

    void insert() throws InterruptedException;
}
