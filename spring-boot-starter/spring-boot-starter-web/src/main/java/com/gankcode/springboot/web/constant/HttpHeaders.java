package com.gankcode.springboot.web.constant;

import com.gankcode.springboot.web.annotation.ExposedHeader;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Objects;
import java.util.stream.Stream;


public class HttpHeaders extends org.springframework.http.HttpHeaders {

    /**
     * 随机数
     */
    private static final String X_BASE = "X-";

    /**
     * 随机数
     */
    @ExposedHeader
    public static final String X_NONCE = X_BASE + "Nonce";

    /**
     * 内容MD5
     */
    @ExposedHeader
    public static final String X_CONTENT_MD5 = X_BASE + "Content-MD5";

    /**
     * 内容SHA1
     */
    @ExposedHeader
    public static final String X_CONTENT_SHA1 = X_BASE + "Content-SHA1";

    /**
     * 内容SHA256
     */
    @ExposedHeader
    public static final String X_CONTENT_SHA256 = X_BASE + "Content-SHA256";

    /**
     * 内容SHA512
     */
    @ExposedHeader
    public static final String X_CONTENT_SHA512 = X_BASE + "Content-SHA512";


    /**
     * 代理IP
     */
    @ExposedHeader
    public static final String X_FORWARDED_FOR = X_BASE + "Forwarded-For";

    /**
     * 跟踪ID
     */
    @ExposedHeader
    public static final String X_TRACE_ID = X_BASE + "Trace-ID";


    /**
     * 代理URL
     */
    @ExposedHeader
    public static final String X_PROXY_URL = X_BASE + "Proxy-Url";

    /**
     * 终端用户
     */
    @ExposedHeader
    public static final String X_END_USER_ID = X_BASE + "End-User-ID";

    /**
     * 终端设备
     */
    @ExposedHeader
    public static final String X_END_DEVICE_ID = X_BASE + "End-Device-ID";


    /**
     * 选项
     */
    @ExposedHeader
    public static final String X_OPTIONS = X_BASE + "Options";

    /**
     * 当前会话的认证用户
     */
    @ExposedHeader
    public static final String TOKEN = "Token";


    public static String[] getExposedHeaders() {
        return Stream.of(HttpHeaders.class.getDeclaredFields())
                .filter(it -> AnnotationUtils.findAnnotation(it, ExposedHeader.class) != null)
                .map(it -> {
                    try {
                        return (String) it.get(null);
                    } catch (Exception ignored) {
                        return null;
                    }

                })
                .filter(Objects::nonNull)
                .toArray(String[]::new);
    }
}
