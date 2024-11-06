package com.gankcode.springboot.web.http;

import com.gankcode.springboot.i18n.I18nUtils;


public interface BaseErrorCode {

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    int getCode();

    /**
     * 获取错误消息
     *
     * @return 错误消息
     */
    String getMessage();

    /**
     * 获取国际化消息
     *
     * @param args 参数
     * @return 翻译后的消息
     */
    default String getI18nMessage(Object... args) {
        return I18nUtils.translate(getMessage(), args);
    }


    /**
     * 组别： 通用
     */
    int GROUP_COMMON = 0x1000;

    /**
     * 组别： 库-树结构
     */
    int GROUP_LIB_TREE = 0x2000;

    /**
     * 组别：服务-基础
     */
    int GROUP_INFRA = 0x3000;

    /**
     * 组别：服务-文件系统
     */
    int GROUP_FILE = 0x4000;


    /**
     * 组别：服务-认证
     */
    int GROUP_AUTH = 0x8000;

    /**
     * 组别：服务-应用市场
     */
    int GROUP_APP_STORE = 0x9000;

    /**
     * 组别：服务-门店
     */
    int GROUP_STORE = 0xA000;

    /**
     * 组别：服务-上陈
     */
    int GROUP_MALL = 0xB000;
}
