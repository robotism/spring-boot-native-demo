package com.gankcode.springboot.web.http;

import com.gankcode.springboot.i18n.I18nException;
import jakarta.annotation.Nonnull;
import lombok.Getter;


public class RequestException extends I18nException {

    @Getter
    private final int code;

    public RequestException(@Nonnull final BaseErrorCode errorCode,
                            final Object... arguments) {
        super(errorCode.getMessage(), arguments);
        this.code = errorCode.getCode();
    }

    public RequestException(final int code,
                            final String message) {
        super(message);
        this.code = code;
    }

}
