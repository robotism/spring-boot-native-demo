package com.gankcode.springboot.web.http.pageable;


import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.Map;


@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface AllowedFilterCondition {

    String[] value();


    class Loader {
        private static final Map<String, String> CONDITIONS_MAP = new HashMap<>();

        static {
            CONDITIONS_MAP.put("between", "between");
            CONDITIONS_MAP.put("notBetween", "not between");
            CONDITIONS_MAP.put("like", "like");
            CONDITIONS_MAP.put("notLike", "not like");
            CONDITIONS_MAP.put("in", "in");
            CONDITIONS_MAP.put("notIn", "not in");
            CONDITIONS_MAP.put("eq", "=");
            CONDITIONS_MAP.put("ne", "!=");
            CONDITIONS_MAP.put("lt", "<");
            CONDITIONS_MAP.put("le", "<=");
            CONDITIONS_MAP.put("gt", ">");
            CONDITIONS_MAP.put("ge", ">=");
        }

        public static String[] splitKeyConditionValue(String key, Object value) {
            if (value == null) {
                return null;
            }
            return splitKeyConditionValue(key, String.valueOf(value));
        }

        public static String[] splitKeyConditionValue(String key, String value) {
            if (!StringUtils.hasText(key) || !StringUtils.hasText(value)) {
                return null;
            }
            key = key.trim();
            value = value.trim();
            if (!StringUtils.hasText(key) || !StringUtils.hasText(value)) {
                return null;
            }
            String cond = "";

            final int posK = key.indexOf(":");
            if (posK > 0) {
                final String k = key.substring(0, posK);
                final String c = translate(key.substring(posK + 1));
                if (c != null) {
                    key = k;
                    cond = c;
                }
            }

            final int posV = value.indexOf(":");
            if (posV > 0) {
                final String c = translate(value.substring(0, posV));
                final String v = value.substring(posV + 1);
                if (c != null) {
                    cond = c;
                    value = v;
                }
            }
            cond = StringUtils.hasText(cond) ? cond : "=";
            return new String[]{key, cond, value};
        }

        public static String translate(String cond) {
            if (CONDITIONS_MAP.containsKey(cond)) {
                return CONDITIONS_MAP.get(cond);
            }
            if (CONDITIONS_MAP.containsValue(cond)) {
                return cond;
            }

            return null;
        }

        public static String[] getAllows(Field field) {
            if (field == null) {
                return new String[0];
            }
            final Class<?> type = field.getType();

            final AllowedFilterCondition allowed = AnnotationUtils.findAnnotation(field, AllowedFilterCondition.class);
            if (allowed != null) {
                return allowed.value();
            }

            if (type == String.class) {
                return new String[]{"like", "not like", "in", "not in", "=", "!="};
            }

            if (Number.class.isAssignableFrom(type)) {
                return new String[]{"between", "not between", "in", "not in", "=", "!=", "<", "<=", ">", ">="};
            }

            if (Temporal.class.isAssignableFrom(type)) {
                return new String[]{"between", "not between", "=", "!=", "<", "<=", ">", ">="};
            }

            if (type.isEnum()) {
                return new String[]{"in", "not in", "=", "!="};
            }
            return new String[]{"="};
        }

    }
}
