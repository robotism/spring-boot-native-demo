package com.gankcode.springboot.annotation

import org.springframework.context.annotation.Profile
import java.lang.annotation.Inherited

@Inherited
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.ANNOTATION_CLASS
)
@Retention(
    AnnotationRetention.RUNTIME
)
@MustBeDocumented
@Profile("debug")
annotation class ConditionalOnDebug 