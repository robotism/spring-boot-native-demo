package com.gankcode.springboot.i18n.config;

import com.gankcode.springboot.i18n.I18nLocale;
import com.gankcode.springboot.utils.LogUtils;
import com.gankcode.springboot.utilsdev.DevUtils;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

class I18nResourceBundleMessageSource extends ReloadableResourceBundleMessageSource {

    static {
        LogUtils.setLoggerLevel(I18nResourceBundleMessageSource.class.getName(), "WARN");
    }

    @Override
    protected Properties newProperties() {
        return new LinkedProperties();
    }

    /**
     * 计算所有语言文件, 包含回退的语言文件
     * 如果当前 locale 匹配的文件没有找到, 会向下搜索回退的 语言文件
     * 注意, List会影响最后加载顺序, 已经加载的翻译, 不会被后加载翻译覆盖
     *
     * @param basename 名称前缀
     * @param locale   语言
     * @return 包含语言回退的文件名
     */
    @Override
    protected List<String> calculateAllFilenames(String basename, Locale locale) {
        final List<String> list = new ArrayList<>(6);
        final List<Locale> locales = I18nLocale.getLocalesWithFallback(locale);

        for (Locale item : locales) {
            final String filename = String.format("%s.%s", basename, item.toLanguageTag());
            if (!list.contains(filename)) {
                list.add(filename);
            }
        }
        for (String item : super.calculateAllFilenames(basename, locale)) {
            if (!list.contains(item)) {
                list.add(item);
            }
        }
        return list;
    }

    @Override
    protected List<String> calculateFilenamesForLocale(String basename, Locale locale) {
        final List<String> result = new ArrayList<>(6);
        final String language = locale.getLanguage();
        final String country = locale.getCountry();
        final String variant = locale.getVariant();

        final String[] splits = new String[]{".", "_"};
        for (String split : splits) {

            final StringBuilder temp = new StringBuilder(basename);

            temp.append(split);
            if (!language.isEmpty()) {
                temp.append(language);
                result.add(0, temp.toString());
            }

            temp.append('_');
            if (!country.isEmpty()) {
                temp.append(country);
                result.add(0, temp.toString());
            }

            if (!variant.isEmpty()) {
                if (!language.isEmpty() || !country.isEmpty()) {
                    temp.append('_').append(variant);
                    result.add(0, temp.toString());
                }
            }
        }

        return result;
    }

}
