package com.gankcode.springboot.utils;

import org.apache.logging.log4j.core.config.Configurator;


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
            Configurator.setLevel(loggerName, org.apache.logging.log4j.Level.valueOf(level));
        } catch (Exception e) {
            // log.error("", e);
        }
    }

}
