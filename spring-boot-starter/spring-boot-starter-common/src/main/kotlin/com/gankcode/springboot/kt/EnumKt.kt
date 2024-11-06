package com.gankcode.springboot.kt


fun <T : Enum<T>> Class<T>.valuesOf(name: String?): T? {
    name ?: return null
    if (this.isEnum) {
        this.enumConstants?.forEach {
            if (it.name.equals(name, true)) {
                return it
            }
        }
    }
    return null
}

fun <T : Enum<T>> Class<T>.valuesOf(ordinal: Int?): T? {
    ordinal ?: return null
    if (this.isEnum) {
        this.enumConstants?.forEach {
            if (it.ordinal == ordinal) {
                return it
            }
        }
    }
    return null
}
