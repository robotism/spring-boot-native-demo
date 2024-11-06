package com.gankcode.springboot.i18n.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties("spring.boot.i18n")
public class I18nProperties {

    private List<String> locations = Arrays.asList(
            "/i18n/",
            "/WEB-INF/i18n/",
            "/META-INF/resources/i18n/"
    );

}
