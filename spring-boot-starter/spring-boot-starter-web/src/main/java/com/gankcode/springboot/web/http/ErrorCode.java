package com.gankcode.springboot.web.http;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseErrorCode {

    /**
     * 系统繁忙
     */
    BUSY(-2, "system.error.busy"),
    /**
     * 系统错误
     */
    ERROR(-1, "system.error.internal"),
    /**
     * 请求成功
     */
    SUCCESS(0, "http.request.success"),
    /**
     * 请求失败
     */
    FAILURE(1, "http.request.failure"),
    /**
     * 校验错误
     */
    INVALID(2, "http.request.failure.validate"),

    /**
     * 校验错误
     */
    INVALID_DATA(3, "http.request.failure.validate.data"),

    /**
     * 校验错误
     */
    INVALID_JSON(4, "http.request.failure.validate.json"),

    /**
     * 校验错误
     */
    RESERVED_NAMES(5, "http.request.failure.validate.reserved.names"),

    /**
     * 校验错误
     */
    RESERVED_KEYS(6, "http.request.failure.validate.reserved.keys"),

    /**
     * 校验错误
     */
    SENSITIVE_KEYS(7, "http.request.failure.validate.sensitive.keys"),


    /**
     * 不支持的操作
     */
    UNSUPPORTED(HttpStatus.NOT_IMPLEMENTED.value(), "http.request.failure.unsupported"),

    /**
     * 没有权限
     */
    PERMISSION_DENIED(HttpStatus.UNAUTHORIZED.value(), "http.request.failure.permission.denied"),

    /**
     * 请求频率限制
     */
    RATE_LIMIT(HttpStatus.TOO_MANY_REQUESTS.value(), "http.request.failure.rate.limit"),



    /**
     * 已经存在
     */
    EXIST(HttpStatus.CONFLICT.value(), "http.request.failure.exist"),

    LOST(HttpStatus.NOT_FOUND.value(), "http.request.failure.lost"),


    ;


    private final int code;
    private final String message;

}
