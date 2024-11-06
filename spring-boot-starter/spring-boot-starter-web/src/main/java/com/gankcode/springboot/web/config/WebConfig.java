package com.gankcode.springboot.web.config;

import com.gankcode.springboot.utils.JsonTemplate;
import com.gankcode.springboot.web.utils.HttpUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;


@Slf4j
@EnableScheduling
@AllArgsConstructor
@Configuration
class WebConfig {

    @Bean
    @Primary
    public StringHttpMessageConverter stringHttpMessageConverter() {
        return new StringHttpMessageConverter(StandardCharsets.UTF_8);
    }

    @Bean
    @Primary
    public JsonHttpMessageConverter jsonHttpMessageConverters(JsonTemplate jsonTemplate) {
        final JsonHttpMessageConverter converter = new JsonHttpMessageConverter();
        converter.setObjectMapper(jsonTemplate.getMapper());
        converter.setSupportedMediaTypes(Arrays.asList(
                MediaType.APPLICATION_JSON,
                MediaType.TEXT_PLAIN
        ));
        return converter;
    }

    @Bean
    @Primary
    public StreamingHttpMessageConverter streamingHttpMessageConverter() {
        return new StreamingHttpMessageConverter(MediaType.APPLICATION_OCTET_STREAM);
    }

    @LoadBalanced
    @Primary
    @Bean
    public RestTemplate restTemplate(StringHttpMessageConverter stringHttpMessageConverter,
                                     MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter) {
        final RestTemplate restTemplate = HttpUtils.newRestTemplate();
        final List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        messageConverters.removeIf(converter -> converter instanceof AbstractHttpMessageConverter);
        messageConverters.addFirst(stringHttpMessageConverter);
        messageConverters.addFirst(mappingJackson2HttpMessageConverter);
        return restTemplate;
    }


    @Bean
    public CharacterEncodingFilter characterEncodingFilter() {
        final CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceRequestEncoding(true);
        filter.setForceResponseEncoding(true);
        return filter;
    }


}
