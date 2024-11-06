package com.gankcode.springboot.redis.config;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Configuration
@EnableCaching
@ConditionalOnExpression("T(org.springframework.util.StringUtils).hasText('${spring.data.redis.port}')")
class RedisCacheConfig {

    @Value("${spring.cache.redis.key-prefix}")
    private String namespace;

    @Resource
    private RedisConfig redisConfig;

    @Bean
    public KeyGenerator wiselyKeyGenerator() {
        return (target, method, params) -> {
            final List<String> keys = new ArrayList<>();
            final List<String> args = new ArrayList<>();
            for (Object param : params) {
                args.add(param.getClass().getSimpleName());
                args.add(String.valueOf(param));
            }
            keys.add(namespace);
            keys.add(target.getClass().getSimpleName());
            keys.add(method.getName());
            keys.add(String.join(",", args));
            return String.join(".", keys);
        };
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        final RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(factory);

        // 配置序列化(解决乱码的问题),过期时间30秒
        final RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .computePrefixWith(name -> namespace + ":" + name + ":")
                // 设置键的序列化方式
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisConfig.redisStringSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisConfig.redisObjectSerializer()))
                // 设置值的序列化方式
                // 不缓存空值
                //.disableCachingNullValues()
                ;

        return RedisCacheManager
                .builder(redisCacheWriter)
                .cacheDefaults(config)
                .build();
    }

    @Bean
    @SuppressWarnings({"NullableProblems", "PMD"})
    public CacheErrorHandler cacheErrorHandler() {
        return new CacheErrorHandler() {

            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                log.error("获取缓存出现异常\n缓存名称: " + cache.getName() + ", 缓存key: " + key + ", 异常: ", exception);
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                log.error("设置缓存出现异常\n缓存名称: " + cache.getName() + ", 缓存key: " + key + ", 异常: ", exception);
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                log.error("清除缓存出现异常\n缓存名称: " + cache.getName() + ", 缓存key: " + key + ", 异常: ", exception);
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                log.error("清理缓存出现异常\n缓存名称: " + cache.getName() + ", 异常: ", exception);
            }
        };
    }

}
