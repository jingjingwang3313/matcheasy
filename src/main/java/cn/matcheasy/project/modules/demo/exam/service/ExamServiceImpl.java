package cn.matcheasy.project.modules.demo.exam.service;

import cn.matcheasy.framework.annotation.DataSourceRoute;
import cn.matcheasy.framework.config.druid.dynamic.DataSourceContext;
import cn.matcheasy.framework.config.druid.dynamic.DataSourceKey;
import cn.matcheasy.project.modules.demo.exam.entity.Exam;
import cn.matcheasy.project.modules.demo.exam.mapper.ExamMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @class: ExamServiceImpl
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
@Slf4j
@Service
@DataSourceRoute(name = DataSourceKey.OTHER_CLUSTER)
//@Transactional
public class ExamServiceImpl implements ExamService
{

    @Autowired
    private ExamMapper examMapper;

    /**
     * @Cacheable 标注在方法上缓存该方法的返回值, 标注在类上缓存该类所有的方法返回值
     * @CachePut 缓存修改, @CachePut(value = {"examCache"}, keyGenerator = "keyGenerator")
     * @CacheEvict 缓存删除, @CacheEvict(value = {"examCache"}, keyGenerator = "keyGenerator")
     * <p>
     * key 缓存的key规则,可以用springEL表达式,默认是方法参数组合
     * keyGenerator 自定义缓存key的生成策略,key属性和keyGenerator属性只能二选一
     * value 缓存名称,可以有多个
     * condition 缓存条件,使用springEL编写,返回true才缓存
     * unless 不缓存条件
     * sync 缓存同步,使只有一个线程可以访问,其他线程堵塞,直到返回结果更新到缓存中
     */
    /*@Caching(
            cacheable = {
                    @Cacheable(value = {"examCache"}, keyGenerator = "keyGenerator")
            },
            put = {
                    @CachePut(value = {"examCache"}, key = "#id"),
                    @CachePut(value = {"examCache"}, key = "'stock:'+#id")
            },
            evict = {
                    @CacheEvict(value = {"examCache"}, keyGenerator = "keyGenerator")
            }
    )*/
    @Cacheable(value = {"examCache"}, keyGenerator = "keyGenerator", condition = "", sync = true,
            unless = "#result == null || #result.size() == 0", cacheManager = "cacheManager1Hour")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)//查询方法无需事务
    @Override
    public Exam select(String exam_id) throws Exception
    {
        log.info("业务info日志...");
        Exam exam = examMapper.select(exam_id);
        return exam;
    }

    //@Transactional(rollbackFor = Exception.class)//抛出异常回滚,try{}catch{}不回滚
    @Override
    public void update(Exam exam) throws Exception
    {
        log.warn("业务warn日志...");
        examMapper.update(exam);
    }

    @Override
    public void delete(String exam_id) throws Exception
    {
        log.error("业务error日志...");
        examMapper.delete(exam_id);
    }

    @Async("threadPoolExecutor")
    @Override
    public Future<List> getList() throws Exception
    {
        log.info("Thrad name: " + Thread.currentThread().getName());
        List list = new ArrayList();
        list.add("aaa");
        list.add("vvv");
        list.add("bbb");
        list.add("rrr");
        list.add("ddd");
        return new Future<List>()
        {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning)
            {
                return false;
            }

            @Override
            public boolean isCancelled()
            {
                return false;
            }

            @Override
            public boolean isDone()
            {
                return false;
            }

            @Override
            public List get() throws InterruptedException, ExecutionException
            {
                return list;
            }

            @Override
            public List get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
            {
                return null;
            }
        };
    }

    @Transactional
    @Override
    public void testDS1() throws Exception
    {
        DataSourceContext.setDataSource(DataSourceKey.OTHER_CLUSTER);
        Exam exam = new Exam();
        exam.setExam_id("1500");
        exam.setExam_name("测试修改OTHER_CLUSTER");
        examMapper.update(exam);
        //List<Exam> examList2 = examMapperCluster.findAll(new HashMap());
        //log.info(DataSourceContext.getDataSource() + ": " + examList2.toString());

        DataSourceContext.setDataSource(DataSourceKey.DEFAULT_CLUSTER);
        exam.setExam_id("1499");
        exam.setExam_name("测试修改EFAULT_CLUSTER");
        examMapper.update(exam);
        //List<Exam> examList = examMapperCluster.findAll(new HashMap());
        //log.info(DataSourceContext.getDataSource() + ": " + examList.toString());

        int a = 3 / 0; //测试分布式事务

    }

    @DataSourceRoute(name = DataSourceKey.OTHER_CLUSTER)
    @Override
    public void testDS2() throws Exception
    {
        List<Exam> examList = examMapper.findAll(new HashMap());
        log.info(DataSourceContext.getDataSource() + ": " + examList.toString());
    }

}
