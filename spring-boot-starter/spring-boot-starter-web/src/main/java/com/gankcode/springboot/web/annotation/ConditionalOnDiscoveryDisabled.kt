package com.gankcode.springboot.web.annotation

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import java.lang.annotation.Inherited

@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(
    AnnotationRetention.RUNTIME
)
@MustBeDocumented
@Inherited
@ConditionalOnProperty(
    prefix = "spring.cloud.discovery",
    value = ["enabled"],
    havingValue = "false",
    matchIfMissing = true
)
annotation class ConditionalOnDiscoveryDisabled 