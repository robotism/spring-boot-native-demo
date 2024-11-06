package com.gankcode.springboot.web.config;

import com.gankcode.springboot.utils.JsonTemplate;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
class SwaggerConfigJsonBodyAdvice {

    @ControllerAdvice
    public static class OpenApiWebMvcResource implements ResponseBodyAdvice<Object> {

        @Override
        public Object beforeBodyWrite(Object body,
                                      @NotNull MethodParameter returnType,
                                      @NotNull MediaType selectedContentType,
                                      @NotNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                      @NotNull ServerHttpRequest request,
                                      @NotNull ServerHttpResponse response) {

            try {
                if (!selectedContentType.equals(MediaType.APPLICATION_JSON)) {
                    return body;
                }
                if (!(body instanceof byte[])) {
                    return body;
                }
                final String content = new String((byte[]) body, StandardCharsets.UTF_8);
                return JsonTemplate.getInstance().toJsonTree(content);
            } catch (Exception e) {
                log.error("", e);
                return body;
            }
        }


        @Override
        public boolean supports(MethodParameter methodParameter, @NotNull Class aClass) {
            return methodParameter.getDeclaringClass().getName().startsWith("org.springdoc");
        }

    }

//
//    @ControllerAdvice
//    @ResponseBody
//    class SwaggerWelcomeWebMvcJsonBodyAdvice implements ResponseBodyAdvice<TreeMap<String, Object>> {
//
//
//        @Override
//        public TreeMap<String, Object> beforeBodyWrite(TreeMap<String, Object> body,
//                                                       MethodParameter returnType,
//                                                       MediaType selectedContentType,
//                                                       Class<? extends HttpMessageConverter<?>> selectedConverterType,
//                                                       ServerHttpRequest request,
//                                                       ServerHttpResponse response) {
//
//            try {
//                final String redirectUrl = (String) body.get("oauth2RedirectUrl");
//                final String url = (String) body.get("url");
//                final HashSet<AbstractSwaggerUiConfigProperties.SwaggerUrl> urls
//                        = (HashSet<AbstractSwaggerUiConfigProperties.SwaggerUrl>) body.get("urls");
//                final URI uri = URI.create(redirectUrl);
//                if (url != null) {
//                    body.put("url", uri.resolve(url).toString());
//                }
//                for (AbstractSwaggerUiConfigProperties.SwaggerUrl item : urls) {
//                    item.setUrl(uri.resolve(item.getUrl()).toString());
//                }
//            } catch (Exception e) {
//                log.error("", e);
//            }
//            return body;
//        }
//
//
//        @Override
//        public boolean supports(MethodParameter methodParameter, Class aClass) {
//            final boolean isSwaggerMvc = methodParameter.getDeclaringClass().equals(SwaggerWelcomeWebMvc.class);
//            if (!isSwaggerMvc) {
//                return false;
//            }
//            if (methodParameter.getMethod() == null) {
//                return false;
//            }
//            return methodParameter.getMethod().getName().equals("openapiJson");
//        }
//
//    }

}