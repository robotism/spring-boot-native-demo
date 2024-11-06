package com.gankcode.springboot.i18n;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;


@AllArgsConstructor
@Configuration
public class I18nUtils {

    private static final Set<MessageSource> MESSAGE_SOURCES = new HashSet<>();

    @Resource
    public void setMessageSource(MessageSource[] messageSources) {
        MESSAGE_SOURCES.addAll(Arrays.asList(messageSources));
    }

    public static String translate(String code, Object... arguments) {
        return translate((String) null, code, arguments);
    }

    public static String translate(String languageTag, String code, Object... arguments) {
        if (!StringUtils.hasText(languageTag)) {
            final ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            final HttpServletRequest request = attributes == null ? null : attributes.getRequest();
            final Locale locale = request == null ? null : request.getLocale();
            return translate(locale, code, arguments);
        } else {
            return translate(Locale.forLanguageTag(languageTag), code, arguments);
        }
    }

    public static String translate(Locale locale, String code, Object... arguments) {
        if (StringUtils.hasText(code)) {
            for (MessageSource messageSource : MESSAGE_SOURCES) {
                try {
                    return messageSource.getMessage(code, arguments, locale);
                } catch (Exception ignored) {

                }
            }
            try {
                return MessageFormat.format(code, arguments);
            } catch (Exception ignored) {

            }
        }
        final List<Object> list = new ArrayList<>();
        list.add(String.valueOf(code));
        list.addAll(Arrays.asList(arguments));
        return list.stream()
                .map(String::valueOf)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(","));
    }


}
