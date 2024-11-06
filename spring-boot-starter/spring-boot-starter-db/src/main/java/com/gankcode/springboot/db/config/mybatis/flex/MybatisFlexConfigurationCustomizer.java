package com.gankcode.springboot.db.config.mybatis.flex;

import com.gankcode.springboot.db.config.DynamicTableNameProcessor;
import com.mybatisflex.core.MybatisFlexBootstrap;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.ConsoleMessageCollector;
import com.mybatisflex.core.audit.MessageCollector;
import com.mybatisflex.core.mybatis.FlexConfiguration;
import com.mybatisflex.core.table.TableManager;
import com.mybatisflex.spring.boot.ConfigurationCustomizer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class MybatisFlexConfigurationCustomizer implements ConfigurationCustomizer {

    private final DynamicTableNameProcessor dynamicTableNameProcessor;

    @PostConstruct
    public void init() {
        //动态表名
        TableManager.setDynamicTableProcessor(dynamicTableNameProcessor::process);
    }

    @Override
    public void customize(FlexConfiguration configuration) {
        configuration.setLogImpl(Slf4jImpl.class);
    }

    static {
        // 设置logImpl
        final MybatisFlexBootstrap bootstrap = MybatisFlexBootstrap.getInstance();
        bootstrap.setLogImpl(Slf4jImpl.class);
        LogFactory.useCustomLogging(Slf4jImpl.class);
        //设置 SQL 审计收集器
        final MessageCollector collector = new ConsoleMessageCollector();
        AuditManager.setMessageCollector(collector);
        //开启审计功能
        AuditManager.setAuditEnable(true);
    }


}