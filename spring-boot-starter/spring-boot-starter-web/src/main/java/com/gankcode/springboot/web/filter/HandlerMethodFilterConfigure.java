package com.gankcode.springboot.web.filter;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;


@AllArgsConstructor
@Configuration
public class HandlerMethodFilterConfigure implements WebMvcConfigurer {

    private final List<HandlerMethodFilter> handlerMethodFilters;

    @PostConstruct
    public void init() {
        handlerMethodFilters.sort((o1, o2) -> {
            final Order ol = AnnotationUtils.findAnnotation(AopUtils.getTargetClass(o1), Order.class);
            final Order or = AnnotationUtils.findAnnotation(AopUtils.getTargetClass(o2), Order.class);

            final int l = ol != null ? ol.value() : Ordered.LOWEST_PRECEDENCE;
            final int r = or != null ? or.value() : Ordered.LOWEST_PRECEDENCE;
            // 值越小优先级越高
            return Integer.compare(l, r);
        });
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 可添加多个
        registry.addInterceptor(new HandlerInterceptor() {

            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                if (!handler.getClass().isAssignableFrom(HandlerMethod.class)) {
                    return true;
                }
                final HandlerMethod method = ((HandlerMethod) handler);
                for (HandlerMethodFilter filter : handlerMethodFilters) {
                    filter.preHandle(request, response, method);
                }
                return true;
            }

        }).addPathPatterns("/**");
    }

}
