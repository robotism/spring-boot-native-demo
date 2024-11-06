package com.gankcode.springboot.db.config.mybatis.flex;

import com.alibaba.druid.pool.DruidDataSource;
import com.gankcode.springboot.db.config.DatasourceProperties;
import com.gankcode.springboot.db.config.druid.DruidDataSourceBuilder;
import com.gankcode.springboot.db.migration.Migration;
import com.mybatisflex.core.datasource.DataSourceDecipher;
import com.mybatisflex.core.datasource.FlexDataSource;
import com.mybatisflex.spring.boot.MultiDataSourceAutoConfiguration;
import com.mybatisflex.spring.boot.MybatisFlexProperties;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.*;


@Getter
@Slf4j
@Configuration
public class DatasourceAutoConfiguration extends MultiDataSourceAutoConfiguration {

    private final DatasourceProperties datasourceProperties;

    private final Migration migration;

    private final DruidDataSourceBuilder dataSourceBuilder;

    private final Map<String, DruidDataSource> dataSourceMap = new HashMap<>();
    private final Map<String, Set<DruidDataSource>> groupedDataSourceMap = new HashMap<>();


    public DatasourceAutoConfiguration(Migration migration,
                                       DruidDataSourceBuilder dataSourceBuilder,
                                       DatasourceProperties datasourceProperties,
                                       MybatisFlexProperties mybatisFlexProperties,
                                       ObjectProvider<DataSourceDecipher> dataSourceDecipherProvider) {
        super(mybatisFlexProperties, dataSourceDecipherProvider);
        this.migration = migration;
        this.dataSourceBuilder = dataSourceBuilder;
        this.datasourceProperties = datasourceProperties;
        this.dataSourceMap.putAll(getDataSourceMap(datasourceProperties.getDynamic()));
        this.groupedDataSourceMap.putAll(getGroupedDataSourceMap(dataSourceMap));
    }


    @PostConstruct
    public void init() throws Exception {
        for (Map.Entry<String, DatasourceProperties.Datasource> entry : datasourceProperties.getDynamic().entrySet()) {
            final String name = entry.getKey();
            final String group = getGroup(name);
            final DatasourceProperties.Datasource properties = entry.getValue();
            final DruidDataSource dataSource = getDataSource(name);
            migration.migrate(group, properties, dataSource);
        }
    }

    public String getSqls() throws Exception {
        final StringBuilder sb = new StringBuilder();
        for (String group : groupedDataSourceMap.keySet()) {
            sb.append(migration.getSqls(group));
        }
        return sb.toString();
    }

    @Primary
    @Override
    public DataSource dataSource() {
        FlexDataSource flexDataSource = (FlexDataSource) super.dataSource();
        for (Map.Entry<String, DruidDataSource> entry : dataSourceMap.entrySet()) {
            final String name = entry.getKey();
            final DruidDataSource dataSource = entry.getValue();
            if (flexDataSource == null) {
                flexDataSource = new FlexDataSource(name, dataSource, false);
            } else {
                flexDataSource.addDataSource(name, dataSource, false);
            }
            log.info("add datasource : {}", dataSource.getUrl());
        }
        return flexDataSource;
    }

    public DruidDataSource getDataSource(String ds) {
        if (groupedDataSourceMap.containsKey(ds)) {
            return groupedDataSourceMap.get(ds).iterator().next();
        }
        if (dataSourceMap.containsKey(ds)) {
            return dataSourceMap.get(ds);
        }
        return null;
    }

    public Map<String, Set<DruidDataSource>> getGroupedDataSourceMap(Map<String, DruidDataSource> dataSourceMap) {
        final Map<String, Set<DruidDataSource>> map = new LinkedHashMap<>();
        if (dataSourceMap == null) {
            return map;
        }
        for (Map.Entry<String, DruidDataSource> entry : dataSourceMap.entrySet()) {
            final String name = entry.getKey();
            final String group = getGroup(name);
            final DruidDataSource dataSource = entry.getValue();
            final Set<DruidDataSource> set = map.computeIfAbsent(group, s -> new LinkedHashSet<>());
            set.add(dataSource);
        }
        return Collections.unmodifiableMap(map);
    }


    public Map<String, DruidDataSource> getDataSourceMap(Map<String, DatasourceProperties.Datasource> properties) {
        final Map<String, DruidDataSource> map = new LinkedHashMap<>();
        if (properties == null) {
            return map;
        }
        for (Map.Entry<String, DatasourceProperties.Datasource> entry : properties.entrySet()) {
            final String datasourceName = entry.getKey();
            final DatasourceProperties.Datasource datasource = entry.getValue();
            map.put(datasourceName, dataSourceBuilder.build(datasourceName, datasource));
        }
        return Collections.unmodifiableMap(map);
    }

    private String getGroup(String name) {
        return name.split("[-_]")[0];
    }

}

