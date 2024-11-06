package com.gankcode.springboot.db.migration;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;


@Slf4j
public abstract class AbstractCommonDatabase extends AbstractDatabase {

    @Setter
    private String changeLogTableName = "__change_log__";
    @Setter
    private String changeLockTableName = "__change_lock__";

    /**
     * 获取创建变更表锁SQL语句
     *
     * @param changeLockTableName 表名
     * @return SQL
     */
    protected abstract String getCreateChangeLockTableSql(String changeLockTableName);

    /**
     * 获取删除变更表锁SQL语句
     *
     * @param changeLockTableName 表名
     * @return SQL
     */
    protected abstract String getDropChangeLockTableSql(String changeLockTableName);

    /**
     * 获取创建变更日志表SQL语句
     *
     * @param changeLogTableName 表名
     * @return SQL
     */
    protected abstract String getCreateChangeLogTableSql(String changeLogTableName);

    /**
     * 获取添加变更日志SQL语句
     *
     * @param changeLogTableName 表名
     * @param file               文件
     * @param sha256             内容校验
     * @return SQL
     */
    protected abstract String getAddChangeLogSql(String changeLogTableName, String file, String sha256);

    /**
     * 获取查询变更日志SQL语句
     *
     * @param changeLogTableName 表名
     * @param file               文件
     * @param sha256             内容校验
     * @return SQL
     */
    protected abstract String getExistsChangeLogSql(String changeLogTableName, String file, String sha256);

    @Override
    public boolean tryLock() {
        try {
            runStatementSession(getCreateChangeLockTableSql(changeLockTableName));
            return true;
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return false;
    }

    @Override
    public void unlock() throws SQLException {
        runStatementSession(getDropChangeLockTableSql(changeLockTableName));
    }

    @Override
    public void createChangeLogTable() throws SQLException {
        runStatementSession(getCreateChangeLogTableSql(changeLogTableName));
    }

    @Override
    public boolean existsChangeLog(String file, String sha256) throws SQLException {
        createChangeLogTable();
        return runStatementSession(getExistsChangeLogSql(changeLogTableName, file, sha256)) > 0;
    }

    @Override
    public boolean addChangeLog(String file, String sha256) throws SQLException {
        createChangeLogTable();
        return runStatementSession(getAddChangeLogSql(changeLogTableName, file, sha256)) > 0;
    }
}
