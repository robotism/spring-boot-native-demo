package com.gankcode.springboot.web.config;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class StreamingHttpMessageConverter extends AbstractHttpMessageConverter<InputStream> {

    protected StreamingHttpMessageConverter(MediaType... supportedMediaTypes) {
        super(supportedMediaTypes);
    }

    @Override
    protected boolean supports(@NotNull Class clazz) {
        return InputStream.class.isAssignableFrom(clazz);
    }

    @Override
    protected @NotNull InputStream readInternal(@NotNull Class<? extends InputStream> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return inputMessage.getBody();
    }

    @Override
    protected void writeInternal(@NotNull InputStream inputStream, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        StreamUtils.copy(inputStream, outputMessage.getBody());
    }
}
