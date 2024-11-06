package com.gankcode.springboot.db.annotation;

import org.springframework.beans.factory.annotation.Value;

import java.lang.annotation.*;


@Inherited
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Value("${spring.datasource.name:}")
public @interface DataSourceName {
}
