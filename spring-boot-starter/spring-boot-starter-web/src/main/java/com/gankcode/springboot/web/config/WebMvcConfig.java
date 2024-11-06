package com.gankcode.springboot.web.config;

import com.gankcode.springboot.utils.ScanPackagesUtils;
import com.gankcode.springboot.web.constant.HttpHeaders;
import com.gankcode.springboot.web.http.pageable.PageRequestArgumentResolver;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;


@AllArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer, WebMvcRegistrations {

    final StringHttpMessageConverter stringHttpMessageConverter;
    final JsonHttpMessageConverter jsonHttpMessageConverter;

    final ScanPackagesUtils scanPackagesUtils;


    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new ApiRequestMappingHandler(scanPackagesUtils);
    }

//    @Bean
//    public CorsFilter corsFilter() {
//        final CorsConfiguration corsConfiguration = new CorsConfiguration();
//        corsConfiguration.addAllowedOrigin("*");
//        corsConfiguration.addAllowedHeader("*");
//        corsConfiguration.addAllowedMethod("*");
//        corsConfiguration.setMaxAge(3600L);
//        corsConfiguration.setAllowCredentials(true);
//        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", corsConfiguration);
//        return new CorsFilter(source);
//    }

    @Override
    public void addCorsMappings(@NotNull CorsRegistry registry) {
        WebMvcConfigurer.super.addCorsMappings(registry);
        registry.addMapping("/**")
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowedOriginPatterns("*")
                .exposedHeaders(HttpHeaders.getExposedHeaders())
                .allowCredentials(true).maxAge(3600);
    }


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 移除默认的String和Json消息转换器, 同过RestTemplate扩展自定义的
        converters.removeIf(converter -> converter instanceof StringHttpMessageConverter);
        converters.removeIf(converter -> converter instanceof JsonHttpMessageConverter);
        converters.addFirst(stringHttpMessageConverter);
        converters.addFirst(jsonHttpMessageConverter);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new PageRequestArgumentResolver(true));
    }

}
