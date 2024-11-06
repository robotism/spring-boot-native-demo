package com.gankcode.springboot.db.migration.database;

import com.alibaba.druid.DbType;
import com.gankcode.springboot.db.migration.AbstractCommonDatabase;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;


public class MysqlDatabase extends AbstractCommonDatabase {

    private static final String JDBC_MYSQL = "jdbc:mysql";
    private static final List<String> JDBC_TYPES = Arrays.asList("mysql", "mysqldb");

    @Override
    public String getDriver(String type, String url) {
        if (StringUtils.hasText(url)
                && url.length() > JDBC_MYSQL.length()
                && url.substring(0, JDBC_MYSQL.length()).equalsIgnoreCase(JDBC_MYSQL)) {
            return "com.mysql.cj.jdbc.Driver";
        }
        if (JDBC_TYPES.contains(type)) {
            return "com.mysql.cj.jdbc.Driver";
        }
        return null;
    }


    @Override
    public DbType getDbType() {
        return DbType.mysql;
    }

    @Override
    protected String getCreateChangeLockTableSql(String changeLockTableName) {
        return String.format("CREATE TABLE %1$s (id INT)", changeLockTableName);
    }

    @Override
    protected String getDropChangeLockTableSql(String changeLockTableName) {
        return String.format("DROP TABLE IF EXISTS %1$s;", changeLockTableName);
    }

    @Override
    protected String getCreateChangeLogTableSql(String changeLogTableName) {
        return String.format("CREATE TABLE IF NOT EXISTS %1$s ("
                + "    `id`     INT PRIMARY KEY NOT NULL AUTO_INCREMENT,"
                + "    `file`   NVARCHAR(256)  NOT NULL,"
                + "    `sha256` VARCHAR(64)     NOT NULL,"
                + "    `ts`     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "    INDEX (`file`),"
                + "    INDEX (`sha256`)"
                + ");", changeLogTableName);
    }

    @Override
    protected String getAddChangeLogSql(String changeLogTableName, String file, String sha256) {
        return String.format("INSERT INTO %1$s (`file`, `sha256`) VALUES ('%2$s', '%3$s');", changeLogTableName, file, sha256);
    }

    @Override
    protected String getExistsChangeLogSql(String changeLogTableName, String file, String sha256) {
        return String.format("SELECT * FROM %1$s WHERE `file`='%2$s' "
                + "UNION ALL "
                + "SELECT * FROM %1$s WHERE `sha256`= '%3$s';", changeLogTableName, file, sha256);
    }
}
