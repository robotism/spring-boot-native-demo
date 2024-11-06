package com.gankcode.springboot.web.http.pageable;

import com.gankcode.springboot.utils.JsonTemplate;
import com.gankcode.springboot.web.utils.SqlUtils;
import com.mybatisflex.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.invoker.Invoker;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@SuppressWarnings("PMD")
@EqualsAndHashCode(callSuper = true)
@Data
public class PageRequestMap extends LinkedHashMap<String, Object> {


    private static final Map<String, String> TABLE_METHOD_OF_ENUM_TYPES = new ConcurrentHashMap();
    private static final ReflectorFactory REFLECTOR_FACTORY = new DefaultReflectorFactory();

    public Object getValue(String key) {
        final Object result = get(key);
        if (result instanceof Map<?, ?>) {
            Map<String, Object> params = (Map<String, Object>) result;
            return params.get("value");
        }
        return result;
    }

    @Schema(hidden = true)
    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    @Schema(hidden = true)
    public void addFilter(String key, Class<?> type, Object value) {
        addFilter(key, type, "=", String.valueOf(value));
    }

    @Schema(hidden = true)
    public void addFilter(String key, Class<?> type, Object value, boolean onlyIfAbsent) {
        addFilter(key, type, "=", String.valueOf(value), onlyIfAbsent);
    }


    @Schema(hidden = true)
    public void addFilter(String key, Class<?> type, String flag, String value) {
        addFilter(key, type, flag, value, false);
    }

    @Schema(hidden = true)
    public void addFilter(String key, Class<?> type, String flag, String value, boolean onlyIfAbsent) {
        final Map<String, Object> params = new HashMap<>();
        params.put("flag", flag);
        params.put("value", value);

        if ("like".equalsIgnoreCase(flag) || "not like".equalsIgnoreCase(flag)) {
            params.put("args", "'%" + SqlUtils.escape(value) + "%'");
        } else if ("between".equalsIgnoreCase(flag) || "not between".equalsIgnoreCase(flag)) {
            final String escapes = getEscapesArray(type, value).collect(Collectors.joining(" and "));
            params.put("args", escapes);
        } else if ("in".equalsIgnoreCase(flag) || "not in".equalsIgnoreCase(flag)) {
            final String escapes = getEscapesArray(type, value).collect(Collectors.joining(","));
            if (StringUtils.hasText(escapes)) {
                params.put("args", "(" + escapes + ")");
            }
        } else if (type == String.class) {
            params.put("args", "'" + SqlUtils.escape(value) + "'");
        } else {
            params.put("args", getTypedValue(type, value));
        }
        final Object args = params.get("args");
        if (args == null) {
            return;
        }

        // args 用于 $
        // value 用于 #
        if (onlyIfAbsent) {
            putIfAbsent(key, params);
        } else {
            put(key, params);
        }
    }

    private Stream<String> getEscapesArray(Class<?> type, String value) {
        if (!value.startsWith("[")) {
            value = "[" + value;
        }
        if (!value.endsWith("]")) {
            value = value + "]";
        }
        final Object[] objs = (Object[]) JsonTemplate.getInstance().fromJson(value, type.arrayType());
        final List<Object> list = Arrays.stream(objs)
                .map(it -> getTypedValue(type, it))
                .toList();
        return list.stream()
                .filter(Objects::nonNull)
                .map(it -> SqlUtils.escape(it) + "");
    }

    private Object getTypedValue(Class<?> type, Object value) {
        try {
            if (value == null) {
                return null;
            } else if (type.isEnum()) {
                return getEnumValue(JsonTemplate.getInstance().fromJson("" + value, type));
            } else if (type == String.class) {
                return StringUtils.hasText((String) value) ? "'" + value + "'" : null;
            } else if (Temporal.class.isAssignableFrom(type)) {
                return "'" + value + "'";
            } else {
                return JsonTemplate.getInstance().fromJson("" + value, type);
            }
        } catch (Exception e) {
            log.error("page request map get typed value error '{}' , '{}'", type, value);
            log.error("page request map get typed value error", e);
            throw e;
        }
    }


    private Object getEnumValue(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            final Class<?> enumClassType = obj.getClass();
            final MetaClass metaClass = MetaClass.forClass(enumClassType, REFLECTOR_FACTORY);
            final String name = findEnumValueFieldName(enumClassType).orElse("value");
            final Invoker getInvoker = metaClass.getGetInvoker(name);
            return getInvoker.invoke(obj, new Object[0]);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    public static Optional<String> findEnumValueFieldName(Class<?> clazz) {
        if (clazz != null && clazz.isEnum()) {
            return Optional.ofNullable(TABLE_METHOD_OF_ENUM_TYPES.computeIfAbsent(clazz.getName(), (key) -> {
                Optional<Field> fieldOptional = findEnumValueAnnotationField(clazz);
                return fieldOptional.map(Field::getName).orElse(null);
            }));
        } else {
            return Optional.empty();
        }
    }

    private static Optional<Field> findEnumValueAnnotationField(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields()).filter((field) -> field.isAnnotationPresent(EnumValue.class)).findFirst();
    }
}
