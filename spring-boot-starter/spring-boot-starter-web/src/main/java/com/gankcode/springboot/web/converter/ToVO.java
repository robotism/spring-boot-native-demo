package com.gankcode.springboot.web.converter;

/**
 * @param <T> VO
 */
public interface ToVO<T> {

    /**
     * 转换为 VO
     *
     * @return T
     */
    default T toVO() {
        return (T) Converter.convert(this);
    }
}
