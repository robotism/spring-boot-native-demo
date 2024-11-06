package com.gankcode.springboot.web.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public class JsonHttpMessageConverter extends MappingJackson2HttpMessageConverter {

    @Override
    protected void writeInternal(@NotNull Object object, Type type, @NotNull HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if (object instanceof String) {
            StreamUtils.copy((String) object, StandardCharsets.UTF_8, outputMessage.getBody());
        } else {
            super.writeInternal(object, type, outputMessage);
        }
    }


    @Override
    protected @NotNull Object readInternal(@NotNull Class<?> clazz, @NotNull HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        if (clazz == String.class) {
            return StreamUtils.copyToString(inputMessage.getBody(), StandardCharsets.UTF_8);
        } else {
            return super.readInternal(clazz, inputMessage);
        }
    }
}
