package com.gankcode.springboot.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
class StringToLocalTimeConverter : Converter<String, LocalTime> {

    override fun convert(source: String): LocalTime {
        return LocalTime.parse(source, DateTimeFormatter.ISO_LOCAL_TIME);
    }

}