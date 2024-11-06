package com.gankcode.springboot.utils

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.gankcode.springboot.config.EnvConfig
import com.gankcode.springboot.kt.log
import jakarta.annotation.PostConstruct
import jakarta.annotation.Resource
import org.springframework.aop.support.AopUtils
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.util.StringUtils
import java.lang.reflect.Type
import java.math.BigInteger
import java.text.SimpleDateFormat


@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
class JsonTemplate {

    private val mapper = ObjectMapper()

    @Resource
    private val envConfig: EnvConfig? = null

    init {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true)
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
        mapper.registerModule(
            SimpleModule()
                .addSerializer(Long::class.java, ToStringSerializer.instance)
                .addSerializer(BigInteger::class.java, ToStringSerializer.instance)
        )
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        mapper.registerModule(JavaTimeModule())
    }

    fun getMapper(): ObjectMapper {
        return mapper
    }

    fun newMapper(): ObjectMapper {
        return mapper.copy()
    }

    fun newMapper(activateDefaultTyping: Boolean? = false): ObjectMapper {
        return mapper.copy().apply {
            if (activateDefaultTyping == true) {
                activateDefaultTyping(
                    LaissezFaireSubTypeValidator.instance,
                    ObjectMapper.DefaultTyping.NON_FINAL,
                    JsonTypeInfo.As.PROPERTY
                )
            }
        }
    }

    @PostConstruct
    fun init() {
        if (StringUtils.hasText(envConfig?.dateFormat)) {
            mapper.setDateFormat(SimpleDateFormat(envConfig?.dateFormat!!))
            instance = this
        }
    }

    fun <T> deepClone(src: T?): T? {
        try {
            src ?: return null
            val srcCls = AopUtils.getTargetClass(src)
            val jsonElement = mapper.valueToTree<JsonNode>(src)
            return fromJson<T>(jsonElement, srcCls as Type?)
        } catch (e: Exception) {
            JsonTemplate.log.error("", e)
        }
        return null
    }

    fun <T> deepCloneTo(src: Any?, to: Class<T>): T? {
        try {
            src ?: return null
            val jsonElement = mapper.valueToTree<JsonNode>(src)
            return fromJson<T>(jsonElement, to as Type?)
        } catch (e: Exception) {
            JsonTemplate.log.error("", e)
        }
        return null
    }

    fun <T> fromJson(data: Map<*, *>?, type: Class<T>): T? {
        return fromJson(toJson(data), type)
    }

    fun <T> fromJson(data: JsonNode?, typeToken: TypeReference<T>?): T? {
        try {
            return fromJson<T>(data, typeToken?.type)
        } catch (e: Exception) {
            JsonTemplate.log.error("", e)
        }
        return null
    }

    fun <T> fromJson(data: JsonNode?, type: Type?): T? {
        if (data == null || type == null) {
            return null
        }
        try {
            return mapper.treeToValue(data, mapper.typeFactory.constructType(type))
        } catch (e: Exception) {
            JsonTemplate.log.error("", e)
        }
        return null
    }


    fun <T> fromJson(data: JsonNode?, type: Class<T>?): T? {
        if (data == null || type == null) {
            return null
        }
        try {
            return mapper.treeToValue(data, type)
        } catch (e: Exception) {
            JsonTemplate.log.error("", e)
        }
        return null
    }


    fun <T> fromJson(data: String?, type: Type?): T? {
        if (data == null || type == null) {
            return null
        }
        try {
            return mapper.readValue(data, mapper.typeFactory.constructType(type))
        } catch (e: Exception) {
            JsonTemplate.log.error("", e)
        }
        return null
    }

    fun <T> fromJson(data: String?, typeToken: TypeReference<T>?): T? {
        if (data == null) {
            return null
        }
        try {
            return mapper.readValue(data, typeToken)
        } catch (e: Exception) {
            JsonTemplate.log.error("", e)
        }
        return null
    }

    fun <T> fromJson(data: String?, clz: Class<T>?): T? {
        if (data == null) {
            return null
        }
        try {
            return mapper.readValue(data, clz)
        } catch (e: Exception) {
            JsonTemplate.log.error("", e)
        }
        return null
    }


    fun toJsonPretty(src: Any?): String? {
        return toJson(src, true)
    }

    fun toJsonTree(src: Any?): JsonNode? {
        try {
            if (src is String) {
                return mapper.readValue(src, JsonNode::class.java)
            } else {
                return mapper.valueToTree(src)
            }
        } catch (e: Exception) {
            JsonTemplate.log.error("", e)
        }
        return null
    }

    @JvmOverloads
    fun toJson(src: Any?, pretty: Boolean = false): String? {
        if (src == null) {
            return null
        }
        if (src is String) {
            return src
        }
        try {
            return if (pretty) {
                mapper.writerWithDefaultPrettyPrinter().writeValueAsString(src)
            } else {
                mapper.writeValueAsString(src)
            }
        } catch (e: StackOverflowError) {
            val msg = String.format("[%s] is not a valid json object", src.javaClass.name)
            log.error(msg, e)
        } catch (e: Exception) {
            log.error("", e)
        }
        return null
    }

    companion object {

        private var instance = JsonTemplate()


        @JvmStatic
        fun getInstance(): JsonTemplate {
            return instance
        }
    }
}