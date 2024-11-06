package com.gankcode.springboot.db.migration;

import com.alibaba.druid.DbType;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.util.StringUtils;

import java.io.Closeable;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


@Slf4j
public abstract class AbstractDatabase implements Closeable {

    private DruidDataSource dataSource;
    private DruidDataSource dataSourceGlobal;
    private Connection connectionGlobal;
    private Connection connectionSession;


    public void setDataSource(DruidDataSource dataSource) {
        this.dataSource = dataSource.cloneDruidDataSource();
        this.dataSourceGlobal = dataSource.cloneDruidDataSource();
        this.dataSourceGlobal.setUrl(dataSource.getUrl().replaceFirst("/[0-9a-zA-Z_-]+\\?", "?"));
    }

    /**
     * 获取数据库类型
     *
     * @return DbType
     */
    public abstract DbType getDbType();


    /**
     * 根据url获取驱动
     *
     * @param type jdbc type
     * @param url  jdbc url
     * @return driver class
     */
    public abstract String getDriver(String type, String url);

    /**
     * 创建 变更日志表
     *
     * @throws SQLException sql异常
     */
    public abstract void createChangeLogTable() throws SQLException;

    /**
     * 添加变更日志
     *
     * @param file   文件
     * @param sha256 内容校验
     * @return 成功/失败
     * @throws SQLException sql异常
     */
    public abstract boolean addChangeLog(String file, String sha256) throws SQLException;

    /**
     * 检查变更日志睡否已存在
     *
     * @param file   文件
     * @param sha256 内容校验
     * @return 存在/不存在
     * @throws SQLException sql异常
     */
    public abstract boolean existsChangeLog(String file, String sha256) throws SQLException;

    /**
     * 尝试锁定
     *
     * @return 成功/失败
     */
    public abstract boolean tryLock();

    /**
     * 解锁
     *
     * @throws SQLException
     */
    public abstract void unlock() throws SQLException;


    public synchronized Connection getGlobalConnection() throws SQLException {
        if (dataSource == null || dataSource.getUrl() == null) {
            return null;
        }
        if (this.connectionGlobal == null) {
            this.connectionGlobal = dataSourceGlobal.getConnection();
            this.connectionGlobal.setAutoCommit(true);
        }
        return this.connectionGlobal;
    }

    public synchronized Connection getSessionConnection() throws SQLException {
        if (dataSource == null || dataSource.getUrl() == null) {
            return null;
        }
        if (this.connectionSession == null) {
            this.connectionSession = dataSource.getConnection();
            this.connectionSession.setAutoCommit(true);
//            this.connectionSession.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
//            runStatement(this.connectionSession,"SET NAMES utf8mb4");
        }
        return this.connectionSession;
    }


    @Override
    public void close() {
        try {
            unlock();
        } catch (Exception ignored) {
        }
        try {
            if (this.connectionSession != null) {
                this.connectionSession.close();
            }
        } catch (Exception ignored) {
        }
        try {
            if (this.connectionGlobal != null) {
                this.connectionGlobal.close();
            }
        } catch (Exception ignored) {

        }
        this.connectionSession = null;
        this.connectionGlobal = null;
    }

    public int runStatementGlobal(final String sql) throws SQLException {
        return runStatement(getGlobalConnection(), sql, false);
    }

    public int runStatementSession(final String sql) throws SQLException {
        if (getDbType() == DbType.other) {
            return runStatement(getSessionConnection(), sql, true);
        } else {
            return runStatement(getSessionConnection(), sql, false);
        }
    }

    public int runStatement(final Connection connection, final String sql, boolean split) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            final String clean = SQLParserUtils.removeComment(sql, getDbType()).trim();
            final List<String> list = split ? SQLParserUtils.split(clean, getDbType()) : List.of(clean);
            int count = 0;
            for (String item : list) {
                if (!StringUtils.hasText(item.trim().replaceAll("[\\s\r\n]", ""))) {
                    continue;
                }
                if (!statement.execute(item)) {
                    continue;
                }
                try (ResultSet resultSet = statement.getResultSet()) {
                    while (resultSet.next()) {
                        count++;
                    }
                }
            }
            return count;
        } catch (SQLException e) {
            log.error("run statement : " + sql, e);
//            throw e;
            return 0;
        }
    }

    /**
     * 有一些数据库即便使用SendFullScript也只会执行一部分sql, 所以不再使用这个逻辑
     */
    @Deprecated
    private int runScript(final Connection connection, final String sql) throws SQLException {
        final ScriptRunner scriptRunner = new ScriptRunner(connection);
        final StringWriter writer = new StringWriter();
        scriptRunner.setLogWriter(new PrintWriter(writer));
        scriptRunner.setErrorLogWriter(new PrintWriter(writer));
        scriptRunner.setSendFullScript(true);
        scriptRunner.setStopOnError(true);
        scriptRunner.setThrowWarning(false);

        scriptRunner.runScript(new StringReader(sql));

        final String error = writer.toString().trim();
        if (StringUtils.hasText(error)) {
            log.error(error);
        }
        return -1;
    }


    public String getTransactionIsolation() throws SQLException {
        final int txIsolation = getGlobalConnection().getTransactionIsolation();
        return switch (txIsolation) {
            case Connection.TRANSACTION_READ_UNCOMMITTED -> "READ_UNCOMMITTED";
            case Connection.TRANSACTION_READ_COMMITTED -> "READ_COMMITTED";
            case Connection.TRANSACTION_REPEATABLE_READ -> "REPEATABLE_READ";
            case Connection.TRANSACTION_SERIALIZABLE -> "SERIALIZABLE";
            default -> "NONE";
        };
    }


}
