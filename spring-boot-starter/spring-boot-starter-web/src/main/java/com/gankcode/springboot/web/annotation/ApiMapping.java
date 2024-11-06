package com.gankcode.springboot.web.annotation;


import java.lang.annotation.*;

/**
 * 注意: 如果放到 package-info.java, 必须在 src/main/java 内, 如果放到 src/main/kotlin 则会失效
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PACKAGE, ElementType.TYPE})
public @interface ApiMapping {

    String value();

}
