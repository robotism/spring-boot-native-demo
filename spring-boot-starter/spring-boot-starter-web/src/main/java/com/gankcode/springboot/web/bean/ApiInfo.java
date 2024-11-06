package com.gankcode.springboot.web.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiInfo {

    private Class<?> handleClass;
    private Method method;

    private String mappingMethod;
    private String mappingPath;
    private String mappingGroup;


    public static List<ApiInfo> from(Class<?> handleClass, Method method, List<String> paths) {
        final List<ApiInfo> list = new ArrayList<>();
        for (String path : paths) {
            final RequestMapping requestMapping = AnnotationUtils.findAnnotation(method, RequestMapping.class);
            if (requestMapping == null) {
                continue;
            }
            final String[] split = path.split("/");
            final String mappingGroup;
            if (split.length > 3) {
                mappingGroup = split[1] + "/" + split[2] + "/" + split[3];
            } else {
                mappingGroup = path;
            }

            for (RequestMethod requestMethod : requestMapping.method()) {
                list.add(new ApiInfo(
                        handleClass,
                        method,
                        requestMethod.name(),
                        path,
                        mappingGroup
                ));
            }
        }
        return list;
    }

}
