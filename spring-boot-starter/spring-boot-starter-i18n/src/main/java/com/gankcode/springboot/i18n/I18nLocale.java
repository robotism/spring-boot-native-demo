package com.gankcode.springboot.i18n;

import com.ibm.icu.util.ULocale;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public final class I18nLocale {
    private final ULocale uLocale;

    private static final I18nLocale LOCALE_FALLBACK = I18nLocale.forLanguageTag(Locale.ENGLISH.toLanguageTag());

    private I18nLocale(String languageTag) {
        uLocale = ULocale.addLikelySubtags(ULocale.forLanguageTag(languageTag));
    }

    public static I18nLocale getDefault() {
        return LOCALE_FALLBACK;
    }

    public static List<Locale> getAvailableLocales() {
        final List<Locale> locales = new ArrayList<>();
        for (ULocale uLocale : ULocale.getAvailableLocales()) {
            locales.add(ULocale.addLikelySubtags(uLocale).toLocale());
        }
        return locales;
    }

    public static I18nLocale forLocale(Locale locale) {
        if (locale == null) {
            return getDefault();
        } else {
            return new I18nLocale(locale.toLanguageTag());
        }
    }

    public static I18nLocale forLanguageTag(String tag) {
        if (StringUtils.isEmpty(tag)) {
            return getDefault();
        } else {
            return new I18nLocale(tag);
        }
    }

    @Override
    public String toString() {
        return toLanguageTag();
    }

    public String toLanguageTag() {
        return uLocale.toLanguageTag();
    }

    public Locale toLocale() {
        return uLocale.toLocale();
    }

    public List<Locale> getLocalesWithFallback() {
        return getLocalesWithFallback(uLocale.toLocale());
    }


    /**
     * input: zh-Hant-TW
     * output: ["zh-Hant-TW", "zh-Hant", "zh"]
     *
     * @param locale 区域语言
     * @return 包含降级的语言列表
     */
    public static List<Locale> getLocalesWithFallback(Locale locale) {
        final String[] splits = locale.toLanguageTag().split("-");
        final List<Locale> locales = new ArrayList<>();
        for (int i = splits.length - 1; i >= 0; i--) {
            final StringBuilder sb = new StringBuilder();
            sb.append(splits[0]);
            for (int j = 1; j <= i; j++) {
                sb.append("-").append(splits[j]);
            }
            locales.add(Locale.forLanguageTag(sb.toString()));
        }
        locales.add(new Locale(""));
        return locales;
    }


}
