package com.gankcode.springboot.web.converter;

import java.util.function.Consumer;

/**
 * @param <T> DTO
 */
public interface ToDTO<T> {

    /**
     * 转换为 DTO
     *
     * @return T
     */
    default T toDTO() {
        return (T) Converter.convert(this);
    }

    default T toDTO(Consumer<T> consumer) {
        final T dto = (T) Converter.convert(this);
        if (consumer != null) {
            consumer.accept(dto);
        }
        return dto;
    }
}
