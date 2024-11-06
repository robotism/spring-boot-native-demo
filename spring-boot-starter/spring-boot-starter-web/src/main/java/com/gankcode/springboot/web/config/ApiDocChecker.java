package com.gankcode.springboot.web.config;

import com.gankcode.springboot.annotation.ConditionalOnDebug;
import com.gankcode.springboot.config.EnvConfig;
import com.gankcode.springboot.utilsdev.DevUtils;
import com.gankcode.springboot.web.http.ResponseBean;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.RequestEntity;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
@ConditionalOnDebug
@Configuration
class ApiDocChecker {

    @Resource
    private EnvConfig envConfig;


    @PostConstruct
    public void initAsync() {
        new Thread(this::init).start();
    }

    public void init() {
        try {
            // 获取所有 BeanScanner 注解的 Bean 对象
            final Map<String, RequestMappingHandlerMapping> beanScanners = envConfig.getApplicationContext()
                    .getBeansOfType(RequestMappingHandlerMapping.class);

            beanScanners.forEach(this::onBeanClass);

        } catch (Exception e) {
            log.error("", e);
        }
    }

    private void onBeanClass(String name, RequestMappingHandlerMapping mapping) {
        if (!StringUtils.hasText(name) || mapping == null) {
            return;
        }
        // 获取url与类和方法的对应信息
        final Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : map.entrySet()) {
            final RequestMappingInfo requestMappingInfo = entry.getKey();
            final HandlerMethod handlerMethod = entry.getValue();

            final Class<?> cls = handlerMethod.getMethod().getDeclaringClass();
            final Method method = handlerMethod.getMethod();

            if (AnnotationUtils.findAnnotation(cls, RestController.class) == null) {
                continue;
            }

            final RequestMethodsRequestCondition methodsCondition = requestMappingInfo.getMethodsCondition();
            final Iterator<RequestMethod> iterator = methodsCondition.getMethods().iterator();
            final RequestMethod requestMethod = iterator.hasNext() ? iterator.next() : null;

            if (requestMethod != null) {
                onMethodCheckout(cls, method);
            }
        }
    }

    protected void onMethodCheckout(Class<?> cls, Method method) {

        if (!envConfig.getScanPackagesUtils().isComponentScanPackage(cls)) {
            return;
        }

        checkClassUnfriendlyCode(cls);
        checkMethodUnfriendlyCode(method);
        checkMethodParamsUnfriendlyCode(method);
//        checkMethodReturnUnfriendlyCode(method);
    }

    private void checkClassUnfriendlyCode(final Class<?> cls) {
        final Tag tag = AnnotationUtils.findAnnotation(cls, Tag.class);
        if (tag == null) {
            DevUtils.error(cls, "Swagger 注解不规范: 缺少注解 @Tag(name = \"????\", description = \"????\")");
            return;
        }
        if (tag.name().trim().isEmpty()) {
            DevUtils.error(cls, "Swagger 注解不规范: 注解 @Tag 参数 name 不能为空");
            return;
        }
        if (tag.description().trim().isEmpty()) {
            DevUtils.error(cls, "Swagger 注解不规范: 注解 @Tag 参数 description 不能为空");
        }

    }

    private void checkMethodUnfriendlyCode(final Method method) {
        final Operation api = AnnotationUtils.findAnnotation(method, Operation.class);
        if (api == null) {
            DevUtils.error(method, "Swagger 注解不规范: 缺少注解 @Operation(summary = \"????\")");
            return;
        }
        if (api.summary().trim().isEmpty()) {
            DevUtils.error(method, "Swagger 注解不规范: 注解 @Operation 参数 summary 不能为空");
            return;
        }

        final List<RequestMethod> list = new ArrayList<>();
        for (Annotation item : method.getDeclaredAnnotations()) {
            if (item instanceof RequestMapping) {
                DevUtils.error(method, "请不要在方法上使用 @RequestMapping");
                continue;
            }
            final RequestMethod[] methods = getRequestMethod(item);
            if (methods == null) {
                continue;
            }

            for (RequestMethod requestMethod : methods) {
                if (!list.isEmpty() && !list.contains(requestMethod)) {
                    DevUtils.error(method, "请不要使用多个请求类型注解");
                    break;
                } else {
                    list.add(requestMethod);
                }
            }
        }

        final Parameters parameters = AnnotationUtils.findAnnotation(method, Parameters.class);
        final Parameter parameter = AnnotationUtils.findAnnotation(method, Parameter.class);
        if (parameter != null || parameters != null) {
            DevUtils.error(method, "Swagger 注解不规范: 请不要在方法上使用注解 @Parameter 或 @Parameters");
        }
    }

    private void checkMethodReturnUnfriendlyCode(final Method method) {
        final Type genericType = method.getGenericReturnType();
        if (genericType instanceof ParameterizedType parameterizedType) {
            final Type rawType = parameterizedType.getRawType();
            if (rawType == ResponseBean.class) {
                DevUtils.error(method, "Swagger 注解不规范: 返回类型不再建议使用 ResponseBean<T>(已做全局响应处理), 请直接返回<T>");
            }
        }

    }

    private void checkMethodParamsUnfriendlyCode(final Method method) {
        final java.lang.reflect.Parameter[] parameters = method.getParameters();

        for (java.lang.reflect.Parameter parameter : parameters) {
            if (isParameterCheckSkip(parameter)) {
                continue;
            }
            final Parameter param = AnnotationUtils.findAnnotation(parameter, Parameter.class);

            if (param == null) {
                DevUtils.error(method, "Swagger 注解不规范: 参数[%s]缺少注解 @Parameter(description = \"参数描述\")", parameter.getName());
                return;
            }
//            if (param.name().trim().isEmpty()) {
//                DevUtils.error(method, "Swagger 注解不规范: 注解 @Parameter 参数 name 不能为空");
//            }
            if (param.description().trim().isEmpty()) {
                DevUtils.error(method, "Swagger 注解不规范: 注解 @Parameter 参数 description 不能为空");
            }

            final String name = getAndCheckParamName(method, parameter);
            if (name == null) {
                DevUtils.error(method, "Swagger 注解不规范: 参数[%s]缺少相关注解, 例如 @PathVariable @RequestParam @RequestHeader等", parameter.getName());
            }
        }

    }

    private static RequestMethod[] getRequestMethod(Annotation... annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof GetMapping
                    || annotation instanceof PostMapping
                    || annotation instanceof PatchMapping
                    || annotation instanceof PutMapping
                    || annotation instanceof DeleteMapping) {
                final Class<?> clz = annotation.annotationType();
                final RequestMapping requestMapping = clz.getDeclaredAnnotation(RequestMapping.class);
                return requestMapping == null ? null : requestMapping.method();
            } else if (annotation instanceof RequestMapping) {
                return ((RequestMapping) annotation).method();
            }
        }
        return new RequestMethod[0];
    }


    /**
     * 获取需要匹配 Swagger @Parameter 的参数的 注解参数名
     *
     * @param method    方法
     * @param parameter 参数
     * @return 注解参数名
     */
    private static String getAndCheckParamName(final Method method, final java.lang.reflect.Parameter parameter) {

        final String nameAttribute = "name";
        String name = null;
        Annotation paramAnnotation = null;
        for (Annotation annotation : parameter.getAnnotations()) {
            if (annotation.annotationType() == Parameter.class) {
                continue;
            }
            final Map<String, Object> attributes = AnnotationUtils.getAnnotationAttributes(annotation);
            if (!attributes.containsKey(nameAttribute)) {
                continue;
            }
            name = String.valueOf(attributes.get(nameAttribute));
            if (name.trim().isEmpty()) {
                DevUtils.error(method, "参数注解不规范: 注解 @%s 参数 %s 不能为空",
                        annotation.annotationType().getName(), nameAttribute);
            }
            if (paramAnnotation == null) {
                paramAnnotation = annotation;
            } else {
                DevUtils.error(method, "参数注解不规范: 注解@%s 与 @%s 冲突",
                        annotation.annotationType().getName(),
                        paramAnnotation.annotationType().getName());
                return null;
            }
        }
        return name;
    }


    /**
     * 判断参数是否需要 Swagger @ApiImplicitParam 注解
     *
     * @param parameter 参数
     * @return 是否需要
     */
    private boolean isParameterCheckSkip(final java.lang.reflect.Parameter parameter) {

        if (AnnotationUtils.findAnnotation(parameter, RequestBody.class) != null) {
            return true;
        }
        if (AnnotationUtils.findAnnotation(parameter, io.swagger.v3.oas.annotations.parameters.RequestBody.class) != null) {
            return true;
        }
        if (String.class == parameter.getType() || String[].class == parameter.getType()) {
            return false;
        }

        if (envConfig.getScanPackagesUtils().isComponentScanPackage(parameter.getType())) {
            return true;
        }

        final Class<?>[] ignoredClasses = new Class<?>[]{
                ServletRequest.class, ServletResponse.class, ServletContext.class,
                HttpServletRequest.class, HttpServletResponse.class, HttpSession.class,
                Reader.class, Writer.class,
                RequestEntity.class, Session.class,
                Model.class, Serializable.class,
                Principal.class
        };

        for (Class<?> cls : ignoredClasses) {
            if (cls.isAssignableFrom(parameter.getType())) {
                return true;
            }
        }
        return false;
    }
}
