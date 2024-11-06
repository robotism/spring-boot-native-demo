package com.gankcode.springboot.web.config;

import com.gankcode.springboot.config.EnvConfig;
import com.gankcode.springboot.web.annotation.ConditionalOnDiscoveryDisabled;
import com.gankcode.springboot.web.utils.HttpUtils;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.ServerBaseUrlCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import static org.springdoc.core.utils.Constants.SPRINGDOC_SHOW_ACTUATOR;
import static org.springframework.beans.factory.support.AbstractBeanDefinition.AUTOWIRE_BY_TYPE;


@Slf4j
@Configuration
class SwaggerConfig {

    @Resource
    private EnvConfig envConfig;


    @PostConstruct
    public void init() throws BeansException {

        final BeanDefinitionRegistry beanFactory = envConfig.getBeanDefinitionRegistry();

        registry(
                getGroupPackages(envConfig.getApplicationContext(), envConfig.getDiscoveryEnabled()),
                beanFactory::registerBeanDefinition
        );
    }

    private void registry(Set<String> packages, BiConsumer<String, BeanDefinition> consumer) {
        for (String pkg : packages) {
            log.info("swagger registry package: {}", pkg);
            final String group = pkg.replaceAll("^\\w+\\.\\w+\\.", "")
                    .replaceAll("springboot\\.", "")
                    .replaceAll("springcloud\\.", "")
                    .replaceAll("\\.", "-");

            final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(
                    GroupedOpenApi.class,
                    () -> GroupedOpenApi.builder()
                            .group(group)
                            .packagesToScan(pkg)
                            .build()
            );
            builder.setAutowireMode(AUTOWIRE_BY_TYPE);
            final BeanDefinition beanDefinition = builder.getRawBeanDefinition();
            final String beanName = GroupedOpenApi.class.getSimpleName() + "_" + group.replaceAll("-", "_");

            consumer.accept(beanName, beanDefinition);
        }
    }

    private Set<String> getGroupPackages(final ApplicationContext applicationContext,
                                         final boolean discoveryEnabled) {
        final Set<String> list = new HashSet<>();

        if (discoveryEnabled) {
            list.addAll(envConfig.getScanPackagesUtils().getComponentScanPackages());
        } else {
            final Map<String, RequestMappingHandlerMapping> beanScanners = applicationContext.getBeansOfType(RequestMappingHandlerMapping.class);

            beanScanners.forEach((name, mapping) -> mapping.getHandlerMethods().forEach((info, method) -> {
                final Class<?> cls = method.getMethod().getDeclaringClass();
                if (!envConfig.getScanPackagesUtils().isComponentScanPackage(cls)) {
                    return;
                }
//                if (AnnotationUtils.findAnnotation(cls, Controller.class) == null) {
//                    return;
//                }
                final String pkg = ClassUtils.getPackageName(cls).replaceAll("\\.controller", "");
                list.add(pkg);
            }));
        }

        return list;
    }

    @Bean
    @ConditionalOnDiscoveryDisabled
    @ConditionalOnProperty(SPRINGDOC_SHOW_ACTUATOR)
    public GroupedOpenApi actuatorApi() {
        return GroupedOpenApi.builder()
                .group("actuator")
                .pathsToMatch("/actuator/**")
//                .pathsToExclude("/actuator/health/*")
                .build();
    }


    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION))
                .components(new Components()
                        .addSecuritySchemes(HttpHeaders.AUTHORIZATION,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("Bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .name(HttpHeaders.AUTHORIZATION)))
                .info(new Info()
                        .title(envConfig.getApplicationName().toUpperCase(Locale.ROOT))
                        .version(envConfig.getApplicationVersion() + "[" + envConfig.getApplicationProfile() + "]"));
    }


    @Bean
    public ServerBaseUrlCustomizer serverBaseUrlCustomizer() {
        return (serverBaseUrl, httpRequest) -> {
            try {
                final String uri = HttpUtils.getBaseUrl();
                if (uri == null) {
                    return serverBaseUrl;
                }
                final URL url = new URI(uri).toURL();
                return serverBaseUrl.replaceFirst("^https?:", url.getProtocol() + ":");
            } catch (Exception e) {
                log.error("", e);
                return serverBaseUrl;
            }
        };
    }
}
