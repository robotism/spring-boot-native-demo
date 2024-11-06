package com.gankcode.springboot.utils;

import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

public final class LogUtils {

    private LogUtils() {

    }

    /**
     * 动态设置/修改日志级别
     *
     * @param loggerName 包名
     * @param level      级别
     */
    public static void setLoggerLevel(final String loggerName, String level) {
        try {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            loggerContext.getLogger(loggerName).setLevel(ch.qos.logback.classic.Level.valueOf(level));
        } catch (Exception e) {
            // log.error("", e);
        }
    }

}
