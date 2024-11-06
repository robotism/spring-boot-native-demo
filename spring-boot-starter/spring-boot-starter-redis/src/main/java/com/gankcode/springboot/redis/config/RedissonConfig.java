package com.gankcode.springboot.redis.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.config.BaseConfig;
import org.redisson.config.Config;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;


@Slf4j
@AllArgsConstructor
@Configuration
@ConditionalOnExpression("T(org.springframework.util.StringUtils).hasText('${spring.data.redis.port}')")
public class RedissonConfig {

    /**
     * @return RedissonAutoConfigurationCustomizer
     * @see org.redisson.spring.starter.RedissonAutoConfiguration#redissonAutoConfigurationCustomizers
     */
    @Bean
    public RedissonAutoConfigurationCustomizer redissonAutoConfigurationCustomizer() {
        return config -> {

            final Field[] fields = Config.class.getDeclaredFields();

            for (Field field : fields) {
                final Class<?> type = field.getType();

                if (!BaseConfig.class.isAssignableFrom(type)) {
                    continue;
                }
                field.setAccessible(true);
                try {
                    final BaseConfig<?> baseConfig = (BaseConfig<?>) field.get(config);
                    if (baseConfig == null) {
                        continue;
                    }
                    baseConfig.setPingConnectionInterval(3000);
                } catch (IllegalAccessException e) {
                    log.error("", e);
                }
            }
        };
    }
}
