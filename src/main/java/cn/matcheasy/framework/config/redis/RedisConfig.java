package cn.matcheasy.framework.config.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/**
 * @class RedisConfig
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
@Slf4j
@Configuration
@EnableCaching
public class RedisConfig
{
    @Autowired
    private LettuceConnectionFactory lettuceConnectionFactory;

    /**
     * ???????????????key???????????????,KeyGenerator??????????????????????????????????????????????????????
     * ????????????+?????????+???????????????????????????????????????key
     */
    @Bean
    public KeyGenerator keyGenerator()
    {
        return new KeyGenerator()
        {
            @Override
            public Object generate(Object target, Method method, Object... params)
            {
                // ?????? + ????????? + ??????
                return target.getClass().getSimpleName() + "_"
                        + method.getName() + "_"
                        + StringUtils.arrayToDelimitedString(params, "_");
            }
        };
    }

    /**
     * ???????????????
     */
    @Bean
    public CacheManager cacheManager()
    {
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(lettuceConnectionFactory);
        @SuppressWarnings("serial")
        Set<String> cacheNames = new HashSet<String>()
        {
            {
                //  ??????cache???????????????????????????????????????
                add("redisCache");
                add("powerCache");
                add("examCache");
            }
        };
        builder.initialCacheNames(cacheNames);
        return builder.build();
    }

    /**
     * ??????manager????????????,1??????
     */
    @Bean
    @Primary
    public RedisCacheManager cacheManager1Hour(RedisConnectionFactory connectionFactory)
    {
        RedisCacheConfiguration config = instanceConfig(3600L);
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .transactionAware()
                .build();
    }

    /**
     * ??????manager????????????,1???
     */
    @Bean
    public RedisCacheManager cacheManager1Day(RedisConnectionFactory connectionFactory)
    {
        RedisCacheConfiguration config = instanceConfig(3600 * 24L);
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .transactionAware()
                .build();
    }

    /**
     * ??????redis??????????????????
     */
    private RedisCacheConfiguration instanceConfig(Long ttl)
    {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        // ????????????@JsonSerialize???????????????
        objectMapper.configure(MapperFeature.USE_ANNOTATIONS, false);
        // ????????????????????????????????????
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // ???????????????????????????json????????????
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(ttl))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer));
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory)
    {
        //????????????
        //SerializeUtil serializeUtil = new SerializeUtil();
        // ??????redisTemplate
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);

        RedisSerializer<?> stringSerializer = new StringRedisSerializer();
        // ?????? Jackson2JsonRedisSerializer ????????????
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(
                Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        redisTemplate.setKeySerializer(stringSerializer);// key?????????
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);// value?????????
        redisTemplate.setHashKeySerializer(stringSerializer);// Hash key?????????
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);// Hash value?????????
        // ??????????????????
        redisTemplate.setEnableTransactionSupport(false);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory lettuceConnectionFactory)
    {
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key??????String??????????????????
        redisTemplate.setKeySerializer(stringRedisSerializer);
        // hash???key?????????String??????????????????
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        // value?????????????????????String
        redisTemplate.setValueSerializer(stringRedisSerializer);
        // hash???value?????????????????????String
        redisTemplate.setHashValueSerializer(stringRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

}