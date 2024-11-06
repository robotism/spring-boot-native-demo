//package com.gankcode.springboot.db.config.mybatis.plus;//package com.gankcode.springboot.db.config.mybatis.flex;
//
//import com.alibaba.druid.pool.DruidAbstractDataSource;
//import com.alibaba.druid.pool.DruidDataSource;
//import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
//import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.*;
//import com.gankcode.springboot.db.config.DatasourceProperties;
//import com.gankcode.springboot.db.migration.Migration;
//import jakarta.annotation.PostConstruct;
//import lombok.Getter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.ObjectProvider;
//import org.springframework.boot.autoconfigure.AutoConfigureBefore;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Import;
//
//import javax.sql.DataSource;
//import java.util.*;
//import java.util.stream.Collectors;
//
//
//@Getter
//@Slf4j
//@Configuration
////@EnableConfigurationProperties(DynamicDataSourceProperties.class)
//@AutoConfigureBefore(value = DataSourceAutoConfiguration.class, name = "com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure")
//@Import({DruidDynamicDataSourceConfiguration.class, DynamicDataSourceCreatorAutoConfiguration.class})
//@ConditionalOnProperty(prefix = "spring.datasource", name = "dynamic", matchIfMissing = true)
//public class DatasourceAutoConfiguration extends DynamicDataSourceAutoConfiguration {
//
//    private final Migration migration;
//
//    private final DatasourceProperties datasourceProperties;
//
//    private final Map<String, DruidDataSource> dataSourceMap = new HashMap<>();
//    private final Map<String, Set<DruidDataSource>> groupedDataSourceMap = new HashMap<>();
//
//
//    public DatasourceAutoConfiguration(Migration migration,
//                                       DatasourceProperties datasourceProperties,
//                                       DynamicDataSourceProperties properties,
//                                       ObjectProvider<List<DynamicDataSourcePropertiesCustomizer>> dataSourcePropertiesCustomizers) {
//        super(properties, dataSourcePropertiesCustomizers);
//        this.migration = migration;
//        this.datasourceProperties = datasourceProperties;
//        this.dataSourceMap.putAll(getDataSourceMap(datasourceProperties.getDynamic()));
//        this.groupedDataSourceMap.putAll(getGroupedDataSourceMap(dataSourceMap));
//    }
//
//
//    @PostConstruct
//    public void init() throws Exception {
//        for (Map.Entry<String, DatasourceProperties.Datasource> entry : datasourceProperties.getDynamic().entrySet()) {
//            final String name = entry.getKey();
//            final String group = getGroup(name);
//            final DatasourceProperties.Datasource properties = entry.getValue();
//            final DruidDataSource dataSource = getDataSource(name);
//            migration.migrate(group, properties, dataSource);
//        }
//    }
//
//    public String getSqls() throws Exception {
//        final StringBuilder sb = new StringBuilder();
//        for (String group : groupedDataSourceMap.keySet()) {
//            sb.append(migration.getSqls(group));
//        }
//        return sb.toString();
//    }
//
//    @Override
//    public DataSource dataSource(List<DynamicDataSourceProvider> providers) {
//        final List<DynamicDataSourceProvider> list = new ArrayList<>(providers);
//        for (Map.Entry<String, Set<DruidDataSource>> entry : groupedDataSourceMap.entrySet()) {
//            final Set<DruidDataSource> dataSources = entry.getValue();
//            list.add(() -> dataSources.stream().collect(Collectors.toMap(DruidAbstractDataSource::getName, it -> it)));
//        }
//        return super.dataSource(list);
//    }
//
//    public DruidDataSource getDataSource(String ds) {
//        if (groupedDataSourceMap.containsKey(ds)) {
//            return groupedDataSourceMap.get(ds).iterator().next();
//        }
//        if (dataSourceMap.containsKey(ds)) {
//            return dataSourceMap.get(ds);
//        }
//        return null;
//    }
//
//    public Map<String, Set<DruidDataSource>> getGroupedDataSourceMap(Map<String, DruidDataSource> dataSourceMap) {
//        final Map<String, Set<DruidDataSource>> map = new LinkedHashMap<>();
//        if (dataSourceMap == null) {
//            return map;
//        }
//        for (Map.Entry<String, DruidDataSource> entry : dataSourceMap.entrySet()) {
//            final String name = entry.getKey();
//            final String group = getGroup(name);
//            final DruidDataSource dataSource = entry.getValue();
//            final Set<DruidDataSource> set = map.computeIfAbsent(group, s -> new LinkedHashSet<>());
//            set.add(dataSource);
//        }
//        return Collections.unmodifiableMap(map);
//    }
//
//    public Map<String, DruidDataSource> getDataSourceMap(Map<String, DatasourceProperties.Datasource> properties) {
//        final Map<String, DruidDataSource> map = new LinkedHashMap<>();
//        if (properties == null) {
//            return map;
//        }
//        for (Map.Entry<String, DatasourceProperties.Datasource> entry : properties.entrySet()) {
//            final String name = entry.getKey();
//            final DatasourceProperties.Datasource datasource = entry.getValue();
//            final DruidDataSource dataSource = new DruidDataSource();
//            dataSource.configFromPropeties(datasourceProperties.getDruid());
//            dataSource.setName(name);
//            dataSource.setUrl(datasource.getUrl());
//            dataSource.setDriverClassName(datasource.getDriverClassName());
//            dataSource.setUsername(datasource.getUsername());
//            dataSource.setPassword(datasource.getPassword());
//            map.put(name, dataSource);
//        }
//        return Collections.unmodifiableMap(map);
//    }
//
//    private String getGroup(String name) {
//        return name.split("[-_]")[0];
//    }
//
//}
//
