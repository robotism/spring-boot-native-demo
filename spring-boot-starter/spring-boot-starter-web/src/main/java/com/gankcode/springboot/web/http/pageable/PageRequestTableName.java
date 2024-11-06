package com.gankcode.springboot.web.http.pageable;


import java.lang.annotation.*;


@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface PageRequestTableName {

    String value();

}
