package com.gankcode.springboot.web.constant;


public final class RegExp {

    /**
     * 用户名: 字母开头, 字母或数字结尾, 只能包含字母和数字 下划线和减号, 3-32位
     */
    public static final String USERNAME = "^[a-zA-Z][a-zA-Z0-9_-]{1,30}[a-zA-Z0-9_]$";

    /**
     * 用户昵称: 非空字符串 2-16位
     */
    public static final String NICKNAME = "^[^\\s]{2,32}$";

    /**
     * 密码: 字母+数字+ 一些常用特殊字符 6-16位
     */
    public static final String PASSWORD = "^[a-zA-Z0-9#?!@$%^&*-_]{6,32}$";

    /**
     * 邮件地址
     */
    public static final String EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";

    /**
     * 电话号码
     */
    public static final String PHONE = "^[0-9]+$";

    /**
     * 数字
     */
    public static final String NUMBER = "^[-]{0,1}[0-9]+$";

    /**
     * 短ID
     */
    public static final String SHORT_ID = "^[0-9a-fA-F]{4,32}$";


    /**
     * 32位MD5
     */
    public static final String MD5 = "^[0-9a-fA-F]{32}$";

    /**
     * 64位 SHA256
     */
    public static final String SHA1 = "^[0-9a-fA-F]{40}$";

    /**
     * 64位 SHA256
     */
    public static final String SHA256 = "^[0-9a-fA-F]{64}$";

    /**
     * 64位 SHA256
     */
    public static final String SHA512 = "^[0-9a-fA-F]{128}$";

    /**
     * 文件路径分隔符
     */
    public static final String FILE_PATH_SEPARATOR = "/";

    /**
     * 文件路径
     */
    public static final String FILE_PATH = "^/+[^\\:*?\"<>|]*$";

    /**
     * 文件名
     */
    public static final String FILE_NAME = "^[^\\/:*?\"<>|]{1,255}$";


}
