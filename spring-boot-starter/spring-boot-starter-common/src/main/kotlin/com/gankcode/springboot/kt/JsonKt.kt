package com.gankcode.springboot.kt

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonNode
import com.gankcode.springboot.utils.JsonTemplate
import java.lang.reflect.Type
import kotlin.reflect.KClass

fun Any?.javaType(): JavaType? {
    this ?: return null
    return JsonTemplate.getInstance().getMapper().typeFactory.constructType(this::class.java)
}

fun Any?.toJsonString(pretty: Boolean = false): String? {
    return JsonTemplate.getInstance().toJson(this, pretty)
}

fun <T> String?.toJsonObject(type: Type?): T? {
    return JsonTemplate.getInstance().fromJson(this, type)
}

fun <T> String?.toJsonObject(typeToken: TypeReference<T>?): T? {
    return JsonTemplate.getInstance().fromJson(this, typeToken)
}

fun <T : Any> String?.toJsonObject(clazz: KClass<T>): T? {
    return JsonTemplate.getInstance().fromJson(this, clazz.java)
}


fun <T> String?.toJsonObject(type: Class<T>?): T? {
    return JsonTemplate.getInstance().fromJson(this, type)
}

fun <T> String?.toJsonArray(type: Class<T>?): Array<T?>? {
    return JsonTemplate.getInstance().fromJson(
        this,
        type?.arrayType() as Type
    )
}


fun Any?.toJsonNode(): JsonNode? {
    try {
        this ?: return null
        return JsonTemplate.getInstance().getMapper().valueToTree(this)
    } catch (e: Exception) {
        return null
    }
}