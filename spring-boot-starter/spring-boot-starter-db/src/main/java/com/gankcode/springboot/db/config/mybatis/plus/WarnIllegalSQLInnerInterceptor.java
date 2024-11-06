//package com.gankcode.springboot.db.config.mybatis.plus;
//
//import com.baomidou.mybatisplus.extension.plugins.inner.IllegalSQLInnerInterceptor;
//import com.gankcode.springboot.utils.LogUtils;
//import lombok.extern.slf4j.Slf4j;
//import net.sf.jsqlparser.statement.Statement;
//import org.apache.ibatis.executor.statement.StatementHandler;
//
//import java.sql.Connection;
//
//@Slf4j
//public class WarnIllegalSQLInnerInterceptor extends IllegalSQLInnerInterceptor {
//
//    public WarnIllegalSQLInnerInterceptor() {
//        LogUtils.setLoggerLevel(getClass().getName(), "INFO");
//    }
//
//    // 修改为 只打印错误日志, 不中断逻辑
//    @Override
//    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
//        super.beforePrepare(sh, connection, transactionTimeout);
//    }
//
//    @Override
//    protected String processParser(Statement statement, int index, String sql, Object obj) {
//        try {
//            return super.processParser(statement, index, sql, obj);
//        } catch (Exception e) {
//            log.error(e.getLocalizedMessage());
//        }
//        return statement.toString();
//    }
//}