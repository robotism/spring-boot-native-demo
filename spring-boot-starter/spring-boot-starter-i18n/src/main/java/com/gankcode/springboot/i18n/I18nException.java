package com.gankcode.springboot.i18n;


abstract public class I18nException extends RuntimeException {

    public I18nException(final String message, final Object... arguments) {
        super(new I18nString(message, arguments).toString());
    }

}
