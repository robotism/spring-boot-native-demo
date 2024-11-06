package com.gankcode.springboot.db.migration;

import com.alibaba.druid.pool.DruidDataSource;
import com.gankcode.springboot.crypto.Crypto;
import com.gankcode.springboot.db.config.DatasourceProperties;
import com.gankcode.springboot.db.config.DynamicTableNameProcessor;
import com.gankcode.springboot.utilsdev.DevUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Data
@RequiredArgsConstructor
@Component
public class Migration {

    private final DynamicTableNameProcessor dynamicTableNameProcessor;

    private final MigrationProperties migrationProperties;


    public void migrate(final String group,
                        final DatasourceProperties.Datasource properties,
                        final DruidDataSource dataSource) throws Exception {

        if (!StringUtils.hasText(group) || migrationProperties == null || dataSource == null) {
            log.info("Migration missing args: group={}, prop={}, ds={}", group, migrationProperties, dataSource);
            return;
        }

        final String type = properties.getType();
        final List<String> initSqls = properties.getInitSqls();

        if (initSqls == null || initSqls.isEmpty()) {
            return;
        }

        try (AbstractDatabase database = DatabaseFactory.findByUrl(type, dataSource.getUrl())) {
            if (database == null) {
                DevUtils.exit("不支持的数据库: %s", dataSource.getUrl());
                return;
            }
            database.setDataSource(dataSource);

            final String transactionIsolation = database.getTransactionIsolation();
            log.info("TransactionIsolation({}): {}", group, transactionIsolation);

            for (final String sql : initSqls) {
                if (!StringUtils.hasText(sql)) {
                    continue;
                }
                if (!StringUtils.hasText(sql.trim())) {
                    continue;
                }

                log.info("Exec SQL({}) Global: {}", group, sql);
                database.runStatementGlobal(sql);
            }

            if (!database.tryLock()) {
                log.error("Migration({}) is lock by another process, skipped.", group);
                return;
            }

            final List<String> locations = migrationProperties.getLocations();
            final Map<String, Resource> map = findSqlResource(locations, group);
            for (Map.Entry<String, Resource> item : map.entrySet()) {
                final Resource resource = item.getValue();
                final String path = item.getKey();
                final String name = StringUtils.getFilename(path);
                final String sql = FileCopyUtils.copyToString(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
                final String sqlSha256 = Crypto.sha256().compute(sql);
                if (!StringUtils.hasText(sql.trim())) {
                    continue;
                }
                final boolean runAlways = name.substring(0, 1).equalsIgnoreCase("R");
                if (!runAlways && database.existsChangeLog(path, sqlSha256)) {
                    continue;
                }
                log.info("Exec SQL({}) File: {}", group, path);
                database.runStatementSession(getFixedTableNameSql(sql));
                database.addChangeLog(path, sqlSha256);
            }
        }
    }

    public String getSqls(final String group) throws Exception {
        final List<String> locations = migrationProperties.getLocations();
        final Map<String, Resource> map = findSqlResource(locations, group);
        final StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Resource> item : map.entrySet()) {
            final Resource resource = item.getValue();
            final String sql = FileCopyUtils.copyToString(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            sb.append(getFixedTableNameSql(sql));
        }
        return sb.toString();
    }

    private String getFixedTableNameSql(final String sql) throws JSQLParserException {
        if (dynamicTableNameProcessor == null || !StringUtils.hasText(sql)) {
            return sql;
        }
        final Statements statements = CCJSqlParserUtil.parseStatements(sql);
        for (Statement statement : statements) {
            final Class<?> clazz = statement.getClass();
            final List<Field> fields = Arrays.stream(clazz.getDeclaredFields()).toList();
            final List<Table> tables = fields.stream()
                    .filter(f -> Table.class.isAssignableFrom(f.getType()))
                    .map(f -> {
                        try {
                            f.setAccessible(true);
                            return (Table) f.get(statement);
                        } catch (Exception e) {
                            return null;
                        }
                    }).filter(Objects::nonNull).toList();
            tables.forEach(t -> {
                final String newTableName = dynamicTableNameProcessor.process(t.getName());
                if (StringUtils.hasText(newTableName)) {
                    t.withName(newTableName);
                }
            });
            log.debug(statement.toString());
        }
        return statements.toString();
    }

    private static TreeMap<String, Resource> findSqlResource(final List<String> locations,
                                                             final String group) {

        final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        final TreeMap<String, Resource> map = new TreeMap<>((obj1, obj2) -> {
            if (obj1.contains("__lib_")) {
                return -1;
            }
            if (obj2.contains("__lib_")) {
                return 1;
            }
            if (obj1.contains("__mod_")) {
                return -1;
            }
            if (obj2.contains("__mod_")) {
                return 1;
            }
            final int v = obj1.compareToIgnoreCase(obj2);
            return v != 0 ? v : -1;
        });
        for (String location : locations) {
            try {
                final String path = (location + "/" + group).replaceAll("//", "/");
                final String pattern = (path + "/*.sql");
                final Resource[] resources = resolver.getResources(pattern);
                for (Resource resource : resources) {
                    map.put(path + "/" + resource.getFilename(), resource);
                }
            } catch (Exception ignored) {

            }
        }
        return map;
    }

}
