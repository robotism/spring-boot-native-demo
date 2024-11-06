package com.gankcode.springboot.redis.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.gankcode.springboot.utils.JsonTemplate;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.io.IOException;


@Configuration
@RequiredArgsConstructor
@ConditionalOnExpression("T(org.springframework.util.StringUtils).hasText('${spring.data.redis.port}')")
public class RedisConfig {

    @Resource
    private final JsonTemplate jsonTemplate;

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    @Primary
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();

        template.setKeySerializer(redisStringSerializer());
        template.setValueSerializer(redisObjectSerializer());

        template.setHashKeySerializer(redisStringSerializer());
        template.setHashValueSerializer(redisObjectSerializer());

        template.setConnectionFactory(connectionFactory);
        template.afterPropertiesSet();
        return template;
    }


    public RedisSerializer<String> redisStringSerializer() {
        return RedisSerializer.string();
    }

    public RedisSerializer<Object> redisObjectSerializer() {
        return new GenericJackson2JsonRedisSerializer(jsonTemplate.newMapper(true));
    }

    @RequiredArgsConstructor
    public static class JacksonSerializer<T> extends JsonSerializer<T> {
        private final JsonTemplate jsonTemplate;
        private final Class<T> type;

        @Override
        public Class<T> handledType() {
            return type;
        }

        @Override
        public void serializeWithType(T value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
            serialize(value, gen, serializers);
        }

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(jsonTemplate.toJson(value));
        }
    }

    @RequiredArgsConstructor
    public static class JacksonDeserializer<T> extends JsonDeserializer<Object> {
        private final JsonTemplate jsonTemplate;
        private final Class<T> type;

        @Override
        public Class<T> handledType() {
            return type;
        }

        @Override
        public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
            return deserialize(p, ctxt);
        }

        @Override
        public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return jsonTemplate.fromJson(p.getValueAsString(), type);
        }
    }

}
