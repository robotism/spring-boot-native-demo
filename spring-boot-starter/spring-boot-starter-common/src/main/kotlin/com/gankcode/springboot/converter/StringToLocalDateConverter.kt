package com.gankcode.springboot.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
class StringToLocalDateConverter : Converter<String, LocalDateTime> {

    override fun convert(source: String): LocalDateTime {
        return LocalDateTime.parse(source, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

}