package com.gankcode.springboot.web.http.pageable;

import com.gankcode.springboot.utils.JsonTemplate;
import com.gankcode.springboot.web.http.ErrorCode;
import com.gankcode.springboot.web.http.RequestException;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @param <F> Filter
 * @param <O> Order
 */
@Slf4j
@Data
@Valid
public abstract class BasePageRequestBean<F extends PageRequestMap, O extends PageRequestMap> {


    @Min(0)
    @Schema(description = "偏移量", example = "0")
    private Long offset;

    @Min(1)
    @Schema(description = "数量", example = "500")
    private Long limit;

    @Parameter(description = "过滤条件", style = ParameterStyle.DEEPOBJECT)
    private @Valid F filter;

    @Parameter(description = "排序", style = ParameterStyle.DEEPOBJECT)
    private @Valid O order;

    public long getOffset() {
        return offset == null ? 0 : Math.max(0, offset);
    }

    public long getLimit() {
        return limit == null ? 50 : Math.max(1, limit);
    }


    @Schema(hidden = true)
    public PageRequestMap getFilters() {
        final PageRequestMap map = new PageRequestMap();
        if (filter == null) {
            return map;
        }
        final Class<?> cls = AopUtils.getTargetClass(filter);
        for (Map.Entry<String, Object> entry : filter.entrySet()) {
            try {
                final String[] kcv = AllowedFilterCondition.Loader.splitKeyConditionValue(entry.getKey(), entry.getValue());
                if (kcv == null) {
                    continue;
                }
                final String key = kcv[0];
                final String cond = kcv[1];
                final String value = kcv[2];

                final Field field = cls.getDeclaredField(key);
                field.setAccessible(true);
                final Set<String> allows = Set.of(AllowedFilterCondition.Loader.getAllows(field));
                if (!allows.contains(cond)) {
                    throw new RequestException(ErrorCode.INVALID_DATA, "filter condition not support " +
                            " , field = " + field.getName() +
                            " , condition = " + cond +
                            " , supported conditions = " + JsonTemplate.getInstance().toJson(allows)
                    );
                }
                final Class<?> type = field.getType();
                map.addFilter(key, type, cond, value);
            } catch (NoSuchFieldException ignored) {
                // log.error("", e);
            } catch (Exception e) {
                log.error("", e);
            }
        }
        return map;
    }


    @Schema(hidden = true)
    public String getOrders() {
        if (order == null) {
            return null;
        }

        final List<String> list = new ArrayList<>();

        final Class<?> cls = AopUtils.getTargetClass(order);

        final List<String> fields = Arrays.stream(cls.getDeclaredFields())
                .map(Field::getName)
                .toList();

        for (Map.Entry<String, Object> entry : order.entrySet()) {
            final String name = entry.getKey() != null ? entry.getKey().trim() : "";
            final String value = entry.getValue() != null ? entry.getValue().toString().trim().toUpperCase(Locale.ROOT) : "";

            if (!StringUtils.hasText(name)) {
                continue;
            }
            if (!fields.contains(name)) {
                continue;
            }
            if (!Arrays.asList("ASC", "DESC").contains(value)) {
                continue;
            }

            final String table = getTableName(cls, name);
            final String key = name.replaceAll("[A-Z]", "_$0").toLowerCase(Locale.ROOT);

            if (StringUtils.hasText(table)) {
                list.add(" " + table + ".`" + key + "` " + value + " ");
            } else {
                list.add(" `" + key + "` " + value + " ");
            }
        }
        return list.isEmpty() ? null : String.join(",", list);
    }

    public String getTableName(Class<?> order, String fieldName) {
        try {
            final Field field = order.getDeclaredField(fieldName);
            field.setAccessible(true);
            final PageRequestTableName tableName = AnnotationUtils.findAnnotation(field, PageRequestTableName.class);
            if (tableName == null) {
                return null;
            }
            return tableName.value();
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }


    public Map<String, String> toFeignQueryMap() {
        final Map<String, String> map = new LinkedHashMap<>();
        if (offset != null) {
            map.put("offset", String.valueOf(offset));
        }
        if (limit != null) {
            map.put("limit", String.valueOf(limit));
        }
        if (filter != null) {
            filter.forEach((k, v) -> map.put("filter[" + k + "]", String.valueOf(v)));
        }
        if (order != null) {
            order.forEach((k, v) -> map.put("order[" + k + "]", String.valueOf(v)));
        }
        return map;
    }

    public MultiValueMap<String, String> toMultiValueMap() {
        final MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        if (offset != null) {
            map.put("offset", Collections.singletonList(String.valueOf(offset)));
        }
        if (limit != null) {
            map.put("limit", Collections.singletonList(String.valueOf(limit)));
        }
        if (filter != null) {
            filter.forEach((k, v) -> map.put(
                    "filter[" + k + "]",
                    Collections.singletonList(String.valueOf(v))
            ));
        }
        if (order != null) {
            order.forEach((k, v) -> map.put(
                    "order[" + k + "]",
                    Collections.singletonList(String.valueOf(v))
            ));
        }
        return map;
    }

}
