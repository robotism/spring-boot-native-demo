package com.gankcode.springboot.web.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;


@SuppressWarnings("PMD")
public interface HandlerMethodFilter {

    void preHandle(HttpServletRequest request,
                   HttpServletResponse response,
                   HandlerMethod handlerMethod) throws Exception;

}
