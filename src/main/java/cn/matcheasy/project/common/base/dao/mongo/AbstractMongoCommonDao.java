package cn.matcheasy.project.common.base.dao.mongo;

import cn.matcheasy.framework.commonbean.PageBean;
import cn.matcheasy.framework.utils.MongoUtil;
import cn.matcheasy.framework.utils.PageUtils;
import cn.matcheasy.framework.utils.ReflectionUtil;
import com.mongodb.DBObject;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Date;
import java.util.List;

/**
 * @class: AbstractMongoCommonDao
 * @author: wangjing
 * @date: 2021/11/26/0026
 * @desc: 继承此类操作mongo
 */
@Slf4j
public abstract class AbstractMongoCommonDao<T>
{
    /**
     * 抽象方法获取MongoTemplate,子类需要实现
     */
    public abstract MongoTemplate getMongoTemplate();

    /**
     * 抽象方法获取返回对象Class
     */
    public Class<T> getClazz()
    {
        return ((Class<T>) entityClass);
    }

    public Class<?> entityClass = this.currentEntityClass();

    protected Class<?> currentEntityClass()
    {
        return ReflectionUtil.getSuperClassGenericType(this.getClass(), 0);
    }

    /**
     * 抽象方法获取返回对象主键Id名称
     */
    public String getIdField()
    {
        return "id";
    }


    public void insert(T t)
    {
        getMongoTemplate().save(t);
        log.info("【Mongo】新增表[" + t.getClass().getSimpleName() + "]完成");

    }

    public void insert(List<T> tList)
    {
        getMongoTemplate().insert(tList, getClazz());
        log.info("【Mongo】新增表[" + getClazz().getSimpleName() + "]完成");
    }


    public T getById(String id)
    {
        return getMongoTemplate().findById(id, getClazz());
    }

    public T getOneFieldsByQuery(Query query, String... queryFields)
    {

        if (queryFields != null && queryFields.length > 0)
        {
            for (String field : queryFields)
            {
                query.fields().include(field);
            }
        }
        return getMongoTemplate().findOne(query, getClazz());
    }

    public List<T> getFieldsByQuery(Query query, String... queryFields)
    {

        if (queryFields != null && queryFields.length > 0)
        {
            for (String field : queryFields)
            {
                query.fields().include(field);
            }
        }
        return this.getMongoTemplate().find(query, getClazz());
    }

    public List<T> getFieldsByQuery(Query query, String[] queryFields, List<Sort> sorts, Integer limit, Integer size)
    {

        if (queryFields != null && queryFields.length > 0)
        {
            for (String field : queryFields)
            {
                query.fields().include(field);
            }
        }
        if (CollectionUtils.isNotEmpty(sorts))
        {
            for (Sort sort : sorts)
            {
                query.with(sort);
            }
        }
        if (limit != null)
        {
            query.skip(limit);
            query.limit(size);
        }
        return getMongoTemplate().find(query, getClazz());
    }

    public PageBean<T> selectPage(Query query, String[] queryFields, List<Sort> sorts, Integer pageNum, Integer pageSize)
    {

        if (queryFields != null && queryFields.length > 0)
        {
            for (String field : queryFields)
            {
                query.fields().include(field);
            }
        }
        if (CollectionUtils.isNotEmpty(sorts))
        {
            for (Sort sort : sorts)
            {
                query.with(sort);
            }
        }
        if (pageNum != null && pageSize != null && pageSize != 0)
        {
            int start = PageUtils.getStart(pageNum, pageSize);
            query.skip(start);
            query.limit(pageSize);
        }
        List<T> list = getMongoTemplate().find(query, getClazz());
        Integer count = count(query);

        return PageUtils.build(pageNum, pageSize, count, list);
    }

    public List<T> getFindAll()
    {
        return getMongoTemplate().findAll(getClazz());
    }

    public Integer count(Query query)
    {
        return (int) getMongoTemplate().count(query, getClazz());
    }

    public Integer updateByQuery(Query query, T t)
    {
        log.info("【Mongo】条件修改表[" + t.getClass().getSimpleName() + "]开始");
        Update update = MongoUtil.convertOjbectToUpdate(t);
        UpdateResult updateResult = getMongoTemplate().updateMulti(query, update, getClazz());
        Integer result = (int) updateResult.getModifiedCount();
        log.info("【Mongo】条件修改表[" + t.getClass().getSimpleName() + "]结束，result= " + result);
        return result;
    }

    public Integer updateById(String id, T t)
    {
        log.info("【Mongo】id修改表[" + t.getClass().getSimpleName() + "]开始 id= " + id);
        Update update = MongoUtil.convertOjbectToUpdate(t);
        Criteria criteria = new Criteria();
        criteria.and(getIdField()).is(id);
        Query query = new Query();
        query.addCriteria(criteria);
        UpdateResult updateResult = getMongoTemplate().updateFirst(query, update, getClazz());
        Integer result = (int) updateResult.getModifiedCount();
        log.info("【Mongo】id修改表[" + t.getClass().getSimpleName() + "]结束，result= " + result);
        return result;
    }

    public Integer upsertByQuery(String id, Class<?> t)
    {
        log.info("【Mongo】条件upsert表[" + t.getClass().getSimpleName() + "]开始");
        Update update = MongoUtil.convertOjbectToUpdate(t);
        this.initDateByUpdate(update);
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and(getIdField()).is(id);
        query.addCriteria(criteria);
        UpdateResult upsertResult = getMongoTemplate().upsert(query, update, getClazz());
        Integer result = (int) upsertResult.getModifiedCount();
        log.info("【Mongo】条件upsert表[" + t.getClass().getSimpleName() + "]结束，result= " + result);
        return result;
    }

    public Integer removeById(String id)
    {
        log.info("【Mongo】id删除表[" + getClazz().getSimpleName() + "]开始 id= " + id);
        Criteria criteria = new Criteria();
        criteria.and(getIdField()).is(id);
        Query query = new Query();
        query.addCriteria(criteria);
        DeleteResult deleteResult = getMongoTemplate().remove(query, getClazz());
        Integer result = (int) deleteResult.getDeletedCount();
        log.info("【Mongo】id删除表[" + getClazz().getSimpleName() + "]结束，result= " + result);
        return result;
    }

    public Integer removeByQuery(Query query)
    {
        log.info("【Mongo】条件删除表[" + getClazz().getSimpleName() + "]开始 ");
        DeleteResult deleteResult = getMongoTemplate().remove(query, getClazz());
        Integer result = (int) deleteResult.getDeletedCount();
        log.info("【Mongo】条件删除表[" + getClazz().getSimpleName() + "]结束，result= " + result);
        return result;
    }

    private void initDateByUpdate(Update update)
    {
        DBObject updateObject = (DBObject) update.getUpdateObject().get("$set");
        Date date = new Date();
        if (updateObject == null)
        {
            update.setOnInsert("createTime", date);
            update.set("cTimeStamp", date);
            return;
        }
        if (updateObject.get("createTime") == null)
        {
            update.setOnInsert("createTime", date);
        }
        if (updateObject.get("cTimeStamp") == null)
        {
            update.set("cTimeStamp", date);
        }
    }
}
