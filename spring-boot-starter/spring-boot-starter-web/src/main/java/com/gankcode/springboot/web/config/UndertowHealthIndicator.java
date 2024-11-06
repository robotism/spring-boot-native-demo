package com.gankcode.springboot.web.config;

import io.undertow.Undertow;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServer;
import org.springframework.boot.web.embedded.undertow.UndertowWebServer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;


@Slf4j
@Component
@AllArgsConstructor
public class UndertowHealthIndicator extends AbstractHealthIndicator {

    private final ServletWebServerApplicationContext context;

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {

        final WebServer webServer = context.getWebServer();

        builder.up();
        builder.withDetail("web-server", webServer.getClass().getSimpleName()
                .replaceAll("Servlet", "")
                .replace("WebServer", ""));

        builder.withDetail("active-threads", Thread.activeCount());


        if (webServer instanceof UndertowServletWebServer) {

//            final UndertowWebServer undertow = (UndertowWebServer) webServer;

            final Field field = UndertowWebServer.class.getDeclaredField("builder");
            field.setAccessible(true);

            final Undertow.Builder object = (Undertow.Builder) field.get(webServer);

            final Field ioThreads = Undertow.Builder.class.getDeclaredField("ioThreads");
            final Field workerThreads = Undertow.Builder.class.getDeclaredField("workerThreads");

            ioThreads.setAccessible(true);
            workerThreads.setAccessible(true);

            final Integer io = ioThreads.getInt(object);
            final Integer worker = workerThreads.getInt(object);

            builder.withDetail("io-threads", io);
            builder.withDetail("worker-threads", worker);
        }
    }
}
    