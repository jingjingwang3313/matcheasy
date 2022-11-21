package cn.matcheasy.project.modules.demo.es.service;

import cn.matcheasy.framework.log.MatcheasyLog;
import cn.matcheasy.framework.utils.JsonUtil;
import cn.matcheasy.framework.utils.UUIDUtil;
import cn.matcheasy.project.modules.demo.es.dao.EsDao;
import cn.matcheasy.project.modules.demo.es.entity.EsEntity;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @class: EsServiceImpl
 * @author: wangjing
 * @date: 2021/11/29/0029
 * @desc: TODO
 */
@Slf4j
@Service
public class EsServiceImpl implements EsService
{
    @Autowired
    private EsDao esDao;

    @Override
    public EsEntity findOneById(String id)
    {
        return esDao.selectById(id);
    }

    @Override
    public void insert() throws InterruptedException
    {
        EsEntity<Map> bean = new EsEntity<>();
        bean.setId(UUIDUtil.fastSimpleUUID());
        bean.setItg(1);
        bean.setLg(2L);
        bean.setFlt(3.4F);
        bean.setDub(5.6D);
        bean.setStr("string");
        bean.setBoo(false);
        bean.setDate(new Date());
        ArrayList<Map> list = Lists.newArrayList();
        Map m = new HashMap<>();
        m.put("childId", UUIDUtil.fastSimpleUUID());
        m.put("childItg", 1);
        m.put("childLg", 2L);
        m.put("childDate", new Date());
        list.add(m);
        bean.setList(list);
        esDao.insert(bean);

        EsEntity esEntity = esDao.selectById("3cd121a1cdd94bb7bc162bf75b0d04d0");
        MatcheasyLog.info(JsonUtil.toPrettyJson(esEntity));

    }
}
