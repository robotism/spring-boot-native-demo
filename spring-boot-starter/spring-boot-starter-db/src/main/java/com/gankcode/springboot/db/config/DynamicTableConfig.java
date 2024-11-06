package com.gankcode.springboot.db.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DynamicTableConfig {

    @Bean
    public DynamicTableNameProcessor dynamicTableNameProcessor(DatasourceProperties properties) {
        return new DynamicTableNameProcessor.PrefixDynamicTableNameProcessor(properties.getTablePrefix());
    }
}
