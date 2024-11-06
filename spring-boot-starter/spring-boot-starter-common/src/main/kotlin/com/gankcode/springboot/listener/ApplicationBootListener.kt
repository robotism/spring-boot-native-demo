package com.gankcode.springboot.listener

import com.gankcode.springboot.kt.log
import com.gankcode.springboot.utils.Utils.isTest
import com.gankcode.springboot.utilsdev.DevUtils
import com.gankcode.springboot.utilsdev.ProcessKiller
import kotlinx.coroutines.launch
import library.kt.IO
import library.kt.ioScope
import lombok.extern.slf4j.Slf4j
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationListener
import org.springframework.core.env.Environment
import org.springframework.util.StringUtils


@Slf4j
class ApplicationBootListener : ApplicationListener<ApplicationEvent> {

    private val scope = ioScope()

    override fun onApplicationEvent(event: ApplicationEvent) {
        if (event !is ApplicationEnvironmentPreparedEvent) {
            return
        }

        val environment: Environment = event.environment
        if (environment.activeProfiles.isEmpty()) {
            val profile = "spring.profiles.active"
            DevUtils.exit("请指定一个 '%s'", profile)
        }

        val portValue = environment.getProperty("server.port")

        if (!StringUtils.hasText(portValue)) {
            DevUtils.exit("请指定端口号 'server.port'")
            return
        }
        try {
            val port = portValue!!.toInt(10)
            if (!isTest() && port == 0) {
                scope.launch(IO) {
                    log.warn("Server.Port is random")
                }
            } else {
                ProcessKiller.killProcessByPort(port)
            }
        } catch (e: Exception) {
            DevUtils.exit("端口号可能是非法的: 'server.port'=$portValue", e.message)
        }
    }
}