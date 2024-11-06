package com.gankcode.springboot.web.utils;

import com.gankcode.springboot.web.constant.HttpHeaders;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Locale;


public class HttpUtils {

    private static final String UNKNOWN = "unknown";


    public static RestTemplate newRestTemplate() {

        final SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        final RestTemplate template = new RestTemplate(factory);
        for (HttpMessageConverter<?> converter : template.getMessageConverters()) {
            if (converter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) converter).setDefaultCharset(StandardCharsets.UTF_8);
            }
        }
        return template;
    }

    public static ServletRequestAttributes getRequestAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }

    public static String getRequestURI() {
        final HttpServletRequest request = getRequest();
        return request == null ? null : request.getRequestURI();
    }

    public static HttpServletRequest getRequest() {
        final ServletRequestAttributes attributes = getRequestAttributes();
        return attributes == null ? null : attributes.getRequest();
    }

    public static HttpServletResponse getResponse() {
        final ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes == null ? null : attributes.getResponse();
    }

    public static Locale getLocale() {
        final HttpServletRequest request = getRequest();
        return request == null ? null : request.getLocale();
    }


    @Deprecated
    private static HttpSession getSession() {
        return getSession(false);
    }


    @Deprecated
    private static HttpSession getSession(final boolean create) {
        final HttpServletRequest request = getRequest();
        return request == null ? null : request.getSession(create);
    }

    public static String getBaseUrl() {
        final HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        final String origin = request.getHeader(HttpHeaders.ORIGIN);
        final String scheme;
        if (StringUtils.hasText(origin) && origin.startsWith("http") && origin.contains("://")) {
            scheme = origin.split("://")[0];
        } else {
            scheme = request.getScheme();
        }
        final String domain = request.getServerName();
        final int port = request.getServerPort();
        final String contextPath = request.getContextPath();
        final String path = (StringUtils.isEmpty(contextPath) ? "" : "/") + contextPath;

        if (("http".equals(scheme) && port == 80) || ("https".equals(scheme) && port == 443)) { //NOPMD
            return String.format("%s://%s%s", scheme, domain, path);
        } else {
            return String.format("%s://%s:%d%s", scheme, domain, port, path);
        }
    }

    public static String getClientUa() {
        final HttpServletRequest request = getRequest();
        if (request == null) {
            return "";
        }
        final String ua = request.getHeader(HttpHeaders.USER_AGENT);
        return ua != null ? ua : "";
    }

    public static String getClientIp() {
        return getClientIp(getRequest());
    }


    /**
     * 根据http请求,获取访问当前服务器的客户端真实ip地址
     *
     * @param request 请求实体
     * @return ip
     */
    public static String getClientIp(HttpServletRequest request) {
        try {
            String ip = request.getHeader("x-forwarded-for");
            if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }

            if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
                if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) { //NOPMD
                    //根据网卡取本机配置的IP
                    final InetAddress inet = InetAddress.getLocalHost();
                    ip = inet.getHostAddress();
                }
            }
            //对于通过多个代理的情况, 第一个IP为客户端真实IP,多个IP按照','分割
            // "***.***.***.***".length() = 15
            if (ip != null && ip.length() > 15) { //NOPMD
                if (ip.indexOf(',') > 0) { //NOPMD
                    ip = ip.substring(0, ip.indexOf(','));
                }
            }

            return ip;
        } catch (Exception ignored) {

        }
        return null;
    }

    public static String printRequestInfo(final HttpServletRequest request) throws IOException {
        final StringBuilder info = new StringBuilder();

        info.append("\n").append("client ipv4:\t").append(HttpUtils.getClientIp(request));
        info.append("\n").append("request url:\t").append(request.getRequestURI());
        info.append("\n").append("request method:\t").append(request.getMethod());

        info.append("\n");

        final Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            final String headerName = headerNames.nextElement();
            final String headerValue = request.getHeader(headerName);
            info.append("\n").append("request header:\t").append(headerName).append("=").append(headerValue);
        }

        info.append("\n\n\n");

        final String requestBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        info.append("\n").append("request body:\t---------- begin ↓");
        info.append("\n").append(requestBody);
        info.append("\n").append("request body:\t---------- end ↑");

        return info.toString();
    }

    public static String urlEncode(String plain) {
        return urlEncode(plain, StandardCharsets.UTF_8);
    }

    public static String urlEncode(String plain, Charset charset) {
        try {
            return URLEncoder.encode(plain, charset);
        } catch (Exception ignored) {
            return plain;
        }
    }

    public static String urlDecode(String cipher) {
        return urlDecode(cipher, StandardCharsets.UTF_8);
    }

    public static String urlDecode(String cipher, Charset charset) {
        if (cipher == null) {
            return null;
        }
        try {
            return URLDecoder.decode(cipher, charset);
        } catch (Exception ignored) {
            return cipher;
        }
    }

}
