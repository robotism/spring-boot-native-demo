package com.gankcode.springboot.db.migration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;


@Data
@Configuration
@ConfigurationProperties(prefix = "spring.migration")
public class MigrationProperties {

    /**
     * r开头的sql文件(如: R1.0.1__init.sql), 每次都会执行
     * 其他sql文件(如: V1.0.1__data.sql)只会执行一次
     */
    private final List<String> locations = Arrays.asList(
            "classpath*:/db",
            "classpath*:/sql",
            "classpath*:/META-INF/db",
            "classpath*:/META-INF/sql",
            "classpath*:/META-INF/resources/db",
            "classpath*:/META-INF/resources/sql");

}
