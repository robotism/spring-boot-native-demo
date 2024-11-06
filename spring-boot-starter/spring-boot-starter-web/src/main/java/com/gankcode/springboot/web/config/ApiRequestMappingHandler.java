package com.gankcode.springboot.web.config;

import com.gankcode.springboot.utils.LogUtils;
import com.gankcode.springboot.utils.ScanPackagesUtils;
import com.gankcode.springboot.web.annotation.ApiMapping;
import com.gankcode.springboot.web.bean.ApiInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class ApiRequestMappingHandler extends RequestMappingHandlerMapping {

    private static final Map<String, Package> PACKAGES = Arrays.stream(Package.getPackages())
            .collect(Collectors.toMap(Package::getName, it -> it));

    @Getter
    private static final Map<String, String> apiMappingCache = new HashMap<>();

    @Getter
    private static final List<ApiInfo> apiInfoList = new ArrayList<>();

    private final ScanPackagesUtils scanPackagesUtils;


    static {
        LogUtils.setLoggerLevel(ApiRequestMappingHandler.class.getName(), "WARN");
    }


    final String getApiMappingCache(final Class<?> cls) {
        return apiMappingCache.computeIfAbsent(cls.getName(), k -> getApiMapping(cls));
    }

    final String getApiMapping(final Class<?> cls) {
        final ClassLoader loader = ClassLoader.getSystemClassLoader();
        final List<AnnotatedElement> annotatedElements = new ArrayList<>();
        final List<String> paths = new ArrayList<>();
        final String dot = "\\.";
        for (String path : cls.getName().split(dot)) {
            paths.add(path);
            final String pkgName = String.join(".", paths);
            try {
                final Class<?> pkgInfo = Class.forName(pkgName + ".package-info", false, loader);
                annotatedElements.add(pkgInfo);
                continue;
            } catch (Exception ignored) {

            }
            try {
                final Package pkg = PACKAGES.get(pkgName);
                annotatedElements.add(pkg);
            } catch (Exception ignored) {
            }
        }
        annotatedElements.add(cls);

        final List<String> maps = new ArrayList<>();

        for (AnnotatedElement el : annotatedElements) {
            if (el == null || !el.isAnnotationPresent(ApiMapping.class)) {
                continue;
            }
            final ApiMapping apiMapping = AnnotationUtils.findAnnotation(el, ApiMapping.class);
            final String value = apiMapping != null ? apiMapping.value() : "";
            if (StringUtils.hasText(value)) {
                maps.add(value);
            }
        }
        return String.join("/", maps).replaceAll("/+", "/");
    }

    @Override
    protected RequestMappingInfo getMappingForMethod(@NotNull Method method, @NotNull Class<?> handlerType) {
        try {
            final RequestMappingInfo mappingInfo = super.getMappingForMethod(method, handlerType);
            if (mappingInfo == null) {
                return null;
            }

            final String className = handlerType.getName();

            if (!scanPackagesUtils.isComponentScanPackage(className)) {
                return mappingInfo;
            }

            final String apiMapping = getApiMappingCache(handlerType);

            if (!StringUtils.hasText(apiMapping)) {
                return mappingInfo;
            }

            final List<String> list = new ArrayList<>();
            if (mappingInfo.getPathPatternsCondition() != null) {
                for (PathPattern s : mappingInfo.getPathPatternsCondition().getPatterns()) {
                    list.add((apiMapping + "/" + s).replaceAll("/+", "/"));
                }
            } else if (mappingInfo.getPatternsCondition() != null) {
                for (String s : mappingInfo.getPatternsCondition().getPatterns()) {
                    list.add((apiMapping + "/" + s).replaceAll("/+", "/"));
                }
            }
            apiInfoList.addAll(ApiInfo.from(handlerType, method, list));
            return mappingInfo.mutate()
                    .paths(list.toArray(new String[0]))
                    .build();
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

}