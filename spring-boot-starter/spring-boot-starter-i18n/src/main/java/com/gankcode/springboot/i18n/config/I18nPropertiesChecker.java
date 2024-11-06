package com.gankcode.springboot.i18n.config;

import com.gankcode.springboot.annotation.ConditionalOnDebug;
import com.gankcode.springboot.i18n.I18nLocale;
import com.gankcode.springboot.utilsdev.DevUtils;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;


@ConditionalOnDebug
@Slf4j
@AllArgsConstructor
@Configuration
class I18nPropertiesChecker {

    private static final Map<String, Map<String, String>> I18N_MAP = new LinkedHashMap<>(0);

    private final MessageSource[] messageSources;


    @PostConstruct
    public void initAsync() {
        new Thread(this::init).start();
    }

    public void init() {
        for (MessageSource messageSource : messageSources) {
            loadMessageSource(messageSource);
        }
        checkAllMissing();
    }


    private void loadMessageSource(MessageSource messageSource) {
        if (messageSource == null) {
            return;
        }
        if (messageSource instanceof ReloadableResourceBundleMessageSource) {
            final List<Locale> locales = I18nLocale.getAvailableLocales();
            for (Locale locale : locales) {
                try {
                    final Method method = ReloadableResourceBundleMessageSource.class.getDeclaredMethod("getMergedProperties", Locale.class);
                    method.setAccessible(true);
                    final Object obj = method.invoke(messageSource, locale);
                    if (obj == null) {
                        continue;
                    }
                    if (obj.getClass().getDeclaredFields().length == 0) {
                        continue;
                    }
                    final Field field = obj.getClass().getDeclaredField("properties");
                    field.setAccessible(true);
                    final Properties properties = (Properties) field.get(obj);
                    if (properties.isEmpty()) {
                        continue;
                    }
                    loadLanguageProperties(locale, properties);
                } catch (Exception e) {
                    log.debug("", e);
                }
            }
        }
    }

    private static void loadLanguageProperties(Locale lang, Properties properties) {
        if (lang == null || properties == null || properties.isEmpty()) {
            return;
        }
        final Map<String, String> map = new LinkedHashMap<>();
        final Set<Map.Entry<Object, Object>> entrySet = properties.entrySet();
        for (Map.Entry<Object, Object> entry : entrySet) {
            final String key = entry.getKey().toString();
            final String value = entry.getValue().toString();
            map.put(key, value);
        }
        cacheLang(lang.toLanguageTag(), map);
    }

    private static void cacheLang(String lang, Map<String, String> properties) {
        if (!StringUtils.hasText(lang) || properties == null || properties.isEmpty()) {
            return;
        }
        final Map<String, String> map = I18N_MAP.computeIfAbsent(lang, compute -> new LinkedHashMap<>(0));
        map.putAll(properties);
    }


    private static void checkAllMissing() {
        final Map<String, List<String>> missingMap = new LinkedHashMap<>(0);
        final Map<String, Map<String, String>> cache = I18N_MAP;
        cache.forEach((lang, map) -> map.forEach((k, v) -> cache.forEach((l, m) -> {
            if (l.equals(lang)) {
                return;
            }
            if (!StringUtils.hasText(m.get(k))) {
                final List<String> missList = missingMap.computeIfAbsent(l, compute -> new ArrayList<>());
                if (!missList.contains(k)) {
                    missList.add(k);
                }
            }
        })));
        final StringBuilder sb = new StringBuilder();
        missingMap.forEach((lang, list) -> {
            if (!list.isEmpty()) {
                sb.append("  lang = ").append(lang).append("\n");
                list.forEach(k -> sb.append("      missing : ").append(k).append("\n"));
            }
        });
        if (!sb.isEmpty()) {
            DevUtils.exit("多国语言配置文件翻译缺失:\n%s", sb.toString());
        }
    }

}
