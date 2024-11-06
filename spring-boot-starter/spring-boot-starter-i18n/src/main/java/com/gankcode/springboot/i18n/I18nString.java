package com.gankcode.springboot.i18n;

import lombok.Getter;
import lombok.Setter;

import java.util.Locale;


public class I18nString {

    @Getter
    private final String code;

    @Setter
    private Object[] arguments;

    public I18nString(String code, Object... arguments) {
        this.code = code;
        if (arguments != null && arguments.length > 0) {
            this.arguments = arguments;
        }
    }

    @Override
    public String toString() {
        return I18nUtils.translate(code, arguments);
    }

    public String toString(Locale lang) {
        return I18nUtils.translate(lang, code, arguments);
    }

    public String toString(String lang) {
        return I18nUtils.translate(lang, code, arguments);
    }

}
