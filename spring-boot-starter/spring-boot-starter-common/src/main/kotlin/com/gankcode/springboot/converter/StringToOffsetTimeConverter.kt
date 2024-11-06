package com.gankcode.springboot.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;

@Component
class StringToOffsetTimeConverter : Converter<String, OffsetTime> {

    override fun convert(source: String): OffsetTime {
        return OffsetTime.parse(source, DateTimeFormatter.ISO_OFFSET_TIME);
    }

}