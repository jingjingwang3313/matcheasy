package cn.matcheasy.framework.config.redisson;

import cn.matcheasy.framework.utils.RedissonLockUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @class RedissonConfig
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: 同时使用redissonClient和正常整合redis使用RedisTemplate是完全不冲突的
 */
@Slf4j
@Configuration
public class RedissonConfig
{
    @Autowired
    private RedisProperties redisProperties;

    /**
     * Handled by wangjing at 2021/1/7/0007 14:21
     *
     * @Desc 注册redisson的bean
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson()
    {
        //redisson版本是3.5+ ,集群的ip前面要加上"redis://",不然会报错,3.2- 版本可不加
        List<String> clusterNodes = new ArrayList<>();
        List<String> nodeList = redisProperties.getCluster().getNodes();
        Optional.ofNullable(nodeList).ifPresent(list ->
        {
            list.forEach(node ->
            {
                clusterNodes.add("redis://" + node);
            });
        });
        Config config = new Config();
        ClusterServersConfig clusterServersConfig = config.useClusterServers()
                .addNodeAddress(clusterNodes.toArray(new String[clusterNodes.size()]));
        clusterServersConfig.setPassword(redisProperties.getPassword());//设置密码
        return Redisson.create(config);
    }

    /**
     * 将RedissonDistributeLocker 交给Spring管理,将RedissonDistributeLocker 交给操作锁工具类
     */
    @Bean
    public RedissonDistributeLocker redissonLocker(RedissonClient redissonClient)
    {
        RedissonDistributeLocker locker = new RedissonDistributeLocker(redissonClient);
        RedissonLockUtil.setLocker(locker);
        return locker;
    }

}
