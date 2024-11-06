package com.gankcode.springboot.db.config.druid;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.gankcode.springboot.db.config.DatasourceProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class DruidDataSourceBuilder {

    private final List<Filter> filters;

    private final DatasourceProperties datasourceProperties;

    public DruidDataSource build(String name, DatasourceProperties.Datasource datasource) {
        final NamedDruidDataSource dataSource = new NamedDruidDataSource();
        dataSource.configFromPropeties(datasourceProperties.getDruid());
        dataSource.setProxyFilters(filters);
        dataSource.setName(name);
        dataSource.setUrl(datasource.getUrl());
        dataSource.setDriverClassName(datasource.getDriverClassName());
        dataSource.setUsername(datasource.getUsername());
        dataSource.setPassword(datasource.getPassword());
        return dataSource;
    }

}
