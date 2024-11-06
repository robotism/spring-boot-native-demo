package com.gankcode.springboot.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
class StringToInstantConverter : Converter<String, Instant> {

    override fun convert(source: String): Instant {
        return Instant.parse(source);
    }

}