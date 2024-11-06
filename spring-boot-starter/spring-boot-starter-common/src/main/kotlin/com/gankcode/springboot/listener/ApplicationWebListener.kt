package com.gankcode.springboot.listener

import com.gankcode.springboot.config.EnvConfig
import com.gankcode.springboot.kt.log
import com.gankcode.springboot.utils.NetUtils.getLanIpv4
import jakarta.annotation.Resource
import org.springframework.boot.web.server.WebServer
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order


@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
class ApplicationWebListener : ApplicationListener<ApplicationEvent> {


    @Resource
    private val envConfig: EnvConfig? = null

    override fun onApplicationEvent(event: ApplicationEvent) {
        val cls: Class<*> = event.javaClass
        if (!cls.simpleName.endsWith(WEB_SERVER_INIT_EVENT)) {
            return
        }

        val source = event.source
        if (source is WebServer) {
            init(source)
        }
    }

    private fun init(webServer: WebServer) {
        val port = webServer.port
        val baseUrl = "${envConfig?.applicationProfile} @ http://${getLanIpv4()}:${port}/${envConfig?.contextPath}"
        log.warn("============================================================")
        log.warn(baseUrl)
        log.warn(baseUrl + "druid")
        log.warn(baseUrl + "swagger-ui.html")
        log.warn("============================================================")
    }

    companion object {
        private const val WEB_SERVER_INIT_EVENT = "WebServerInitializedEvent"
    }
}