package com.gankcode.springboot.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Component
class StringToOffsetDateTimeConverter : Converter<String, OffsetDateTime> {

    override fun convert(source: String): OffsetDateTime {
        return OffsetDateTime.parse(source, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

}