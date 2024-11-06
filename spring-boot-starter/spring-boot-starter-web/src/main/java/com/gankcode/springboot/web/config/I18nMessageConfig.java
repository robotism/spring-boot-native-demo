package com.gankcode.springboot.web.config;

import com.gankcode.springboot.i18n.config.I18nMessageSource;
import com.gankcode.springboot.i18n.config.I18nProperties;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


@Configuration
@AllArgsConstructor
public class I18nMessageConfig {

    private final I18nProperties i18nProperties;

    @Bean("defaultI18nMessageSource")
    public I18nMessageSource defaultI18nMessageSource() {
        final I18nMessageSource i18nMessageSource = new I18nMessageSource();
        i18nMessageSource.addBasenames("message");

        final List<String> names = new ArrayList<>();

        for (String name : findMessageResource()) {
            if (!names.contains(name)) {
                names.add(name);
            }
        }
        names.forEach(i18nMessageSource::addBasenames);
        return i18nMessageSource;
    }


    private List<String> findMessageResource() {

        final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        final List<String> list = new ArrayList<>();
        for (String location : i18nProperties.getLocations()) {
            try {
                final String path = ("classpath*:" + location + "/*.*.properties")
                        .replaceAll("/+", "/");
                final Resource[] resources = resolver.getResources(path);
                for (Resource resource : resources) {
                    final String filename = resource.getFilename();
                    final String strip = StringUtils.stripFilenameExtension(filename);
                    final String name = StringUtils.stripFilenameExtension(strip);
                    list.add(name);
                }
            } catch (Exception ignored) {

            }
        }
        return list;
    }

}
