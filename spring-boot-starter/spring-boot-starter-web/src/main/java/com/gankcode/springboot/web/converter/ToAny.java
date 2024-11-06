package com.gankcode.springboot.web.converter;


public interface ToAny {

    /**
     * 任意转换
     *
     * @param cls 目标类
     * @param <T> 目标类型
     * @return 转换后的目标实体
     */
    default <T> T toAny(Class<T> cls) {
        return Converter.convert(this, cls);
    }
}
