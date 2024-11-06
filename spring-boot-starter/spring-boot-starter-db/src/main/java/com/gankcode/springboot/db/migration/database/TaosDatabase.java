package com.gankcode.springboot.db.migration.database;

import com.alibaba.druid.DbType;
import com.gankcode.springboot.db.migration.AbstractCommonDatabase;
import org.springframework.util.StringUtils;


public class TaosDatabase extends AbstractCommonDatabase {

    private static final String JDBC_TAOS = "jdbc:taos";
    private static final String JDBC_TAOS_RS = "jdbc:taos-rs";

    @Override
    public String getDriver(String type, String url) {
        if (StringUtils.hasText(url)
                && url.length() > JDBC_TAOS_RS.length()
                && url.substring(0, JDBC_TAOS_RS.length()).equalsIgnoreCase(JDBC_TAOS_RS)) {
            return "com.taosdata.jdbc.rs.RestfulDriver";
        }
        if (StringUtils.hasText(url)
                && url.length() > JDBC_TAOS.length()
                && url.substring(0, JDBC_TAOS.length()).equalsIgnoreCase(JDBC_TAOS)) {
            return "com.taosdata.jdbc.TSDBDriver";
        }
        return null;
    }

    @Override
    public DbType getDbType() {
        return DbType.other;
    }

    @Override
    protected String getCreateChangeLockTableSql(String changeLockTableName) {
        return String.format("CREATE TABLE %1$s (ts TIMESTAMP, id INT)", changeLockTableName);
    }

    @Override
    protected String getDropChangeLockTableSql(String changeLockTableName) {
        return String.format("DROP TABLE IF EXISTS %1$s;", changeLockTableName);
    }

    @Override
    protected String getCreateChangeLogTableSql(String changeLogTableName) {
        return String.format("CREATE TABLE IF NOT EXISTS %1$s ("
                + "    ts TIMESTAMP,"
                + "    sql NCHAR(256),"
                + "    sha256 NCHAR(64)"
                + ");", changeLogTableName);
    }

    @Override
    protected String getAddChangeLogSql(String changeLogTableName, String file, String sha256) {
        return String.format("INSERT INTO %1$s (ts, sql, sha256) VALUES (now, '%2$s', '%3$s');", changeLogTableName, file, sha256);
    }

    @Override
    protected String getExistsChangeLogSql(String changeLogTableName, String file, String sha256) {
        return String.format("SELECT * FROM %1$s WHERE sql='%2$s' "
                + "UNION ALL "
                + "SELECT * FROM %1$s WHERE sha256='%3$s';", changeLogTableName, file, sha256);
    }
}
