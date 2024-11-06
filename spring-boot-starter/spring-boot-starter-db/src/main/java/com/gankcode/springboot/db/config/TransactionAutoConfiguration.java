package com.gankcode.springboot.db.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;


@Slf4j
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
//@ConditionalOnBean(DataSource.class)
public class TransactionAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(TransactionManager.class)
    @ConditionalOnBean(DataSource.class)
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

}
