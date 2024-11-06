package com.gankcode.springboot.web.converter;

/**
 * @param <T> DO
 */
public interface ToDO<T> {

    /**
     * 转换为 DO
     *
     * @return T
     */
    default T toDO() {
        return (T) Converter.convert(this);
    }
}
