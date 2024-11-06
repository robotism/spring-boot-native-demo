package com.gankcode.springboot.config

import com.gankcode.springboot.utils.ScanPackagesUtils
import jakarta.annotation.Resource
import lombok.Data
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.context.ApplicationContext
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Configuration

@Data
@Configuration
data class EnvConfig(

    @Value("\${spring.application.group:}")
    final val applicationGroup: String,

    @Value("\${spring.application.id:}")
    final val applicationId: String,

    @Value("\${spring.application.name:}")
    final val applicationName: String,

    @Value("\${spring.application.version:}")
    final val applicationVersion: String,

    @Value("\${spring.profiles.active:debug}")
    final val applicationProfile: String,

    @Value("\${server.servlet.context-path:}")
    final val contextPath: String,

    @Value("\${server.port:0}")
    final val serverPort: Long,

    @Value("\${spring.cloud.discovery.enabled:false}")
    final val discoveryEnabled: Boolean,

    @Value("\${spring.jackson.dateFormat:yyyy-MM-dd'T'HH:mm:ss.SSSZ}")
    final val dateFormat: String,

    @Resource
    final val applicationContext: ApplicationContext,

    @Resource
    final val scanPackagesUtils: ScanPackagesUtils

) {

    final val isRelease = "release".equals(applicationProfile, ignoreCase = true)

    final val configurableApplicationContext = applicationContext as ConfigurableApplicationContext;

    final val beanDefinitionRegistry = configurableApplicationContext.beanFactory as BeanDefinitionRegistry


}