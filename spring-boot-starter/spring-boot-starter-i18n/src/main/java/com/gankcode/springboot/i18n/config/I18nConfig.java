package com.gankcode.springboot.i18n.config;

import com.gankcode.springboot.i18n.I18nLocale;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

@Configuration
@AllArgsConstructor
class I18nConfig {

    private final I18nProperties i18nProperties;

    @Primary
    @Bean("messageSource")
    public MessageSource messageSource(I18nMessageSource[] i18nMessageSources) {
        final List<String> scannerPaths = i18nProperties.getLocations();

        final I18nResourceBundleMessageSource messageSource = new I18nResourceBundleMessageSource();
        messageSource.setCacheSeconds(-1);
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setDefaultLocale(I18nLocale.getDefault().toLocale());

        for (I18nMessageSource i18nMessageSource : i18nMessageSources) {
            final Set<String> scannerFiles = i18nMessageSource.getBasenameSet();
            for (String scannerFile : scannerFiles) {
                for (String scannerPath : scannerPaths) {
                    final String path = "classpath:/" + scannerPath + "/" + scannerFile;
                    messageSource.addBasenames(path.replaceAll("//", "/"));
                }
            }
        }
        return messageSource;
    }

}
