package com.gankcode.springboot.web.config;

import io.undertow.UndertowOptions;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
class UndertowConfig {

    @Bean
    public UndertowServletWebServerFactory embeddedServletContainerFactory() {

        final UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();

        factory.addBuilderCustomizers(
                builder -> {
                    final int ioThreads = Math.max(Runtime.getRuntime().availableProcessors(), 8);
                    final int workerThreads = Math.max(ioThreads * 8, 128);
                    builder.setIoThreads(ioThreads);
                    builder.setWorkerThreads(workerThreads);
                    builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true);
                },
                builder -> {

                });

        return factory;
    }


}
