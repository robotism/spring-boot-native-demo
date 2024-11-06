package com.gankcode.springboot.web.converter;

import com.gankcode.springboot.utils.JsonTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public final class Converter {


    private static final Map<String, Class<?>> RETURN_TYPE_MAP = new ConcurrentHashMap<>();


    private static ResolvableType[] getInterfaces(ResolvableType type) {
        final ResolvableType[] interfaces = type.getInterfaces();
        if (interfaces.length > 0) {
            return interfaces;
        }
        final ResolvableType parent = type.getSuperType();
        if (parent.equals(ResolvableType.NONE)) {
            return new ResolvableType[0];
        }
        return getInterfaces(parent);
    }

    private static Class<?> getReturnType(StackTraceElement element, Class<?> sourceCls) {
        return RETURN_TYPE_MAP.computeIfAbsent("" + sourceCls + ":" + element.getMethodName(), f -> {
            try {
                final Class<?> toCls = Class.forName(element.getClassName());
                final ResolvableType forInstance = ResolvableType.forClass(sourceCls);
                final ResolvableType[] interfaces = getInterfaces(forInstance);
                ResolvableType toType = null;
                for (ResolvableType inf : interfaces) {
                    final Class<?> cls = inf.resolve();
                    if (toCls.equals(cls)) {
                        toType = inf;
                        break;
                    }
                }
                if (toType == null) {
                    return null;
                }
                return toType.resolveGeneric(0);
            } catch (Exception ignored) {

            }
            return null;
        });

    }

    public static Object convert(Object source) {
        if (source == null) {
            return null;
        }
        try {
            final Class<?> sourceCls = AopUtils.getTargetClass(source);
            final StackTraceElement[] elements = Thread.currentThread().getStackTrace();
            final Class<?> returnCls = getReturnType(elements[2], sourceCls);
            return JsonTemplate.getInstance().deepCloneTo(source, returnCls);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }


    public static <T> T convert(Object source, Class<?> returnCls) {
        try {
            final Class<?> sourceCls = AopUtils.getTargetClass(source);
            return (T) JsonTemplate.getInstance().deepCloneTo(source, returnCls);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }



}
