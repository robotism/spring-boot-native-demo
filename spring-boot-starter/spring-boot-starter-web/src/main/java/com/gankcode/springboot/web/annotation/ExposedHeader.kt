package com.gankcode.springboot.web.annotation

import java.lang.annotation.Inherited


@Inherited
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class ExposedHeader 