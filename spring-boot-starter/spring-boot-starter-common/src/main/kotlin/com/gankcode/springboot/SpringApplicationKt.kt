package com.gankcode.springboot

import com.gankcode.springboot.listener.ApplicationBootListener
import com.gankcode.springboot.utils.FileManager
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.ComponentScan


inline fun <reified T : Any> launch(vararg args: String): ConfigurableApplicationContext =
    runApplication(T::class.java, *args)


@ComponentScan("com.gankcode")
open class SpringApplicationKt


@Throws(Exception::class)
fun <T> runApplication(clazz: Class<T>, vararg args: String): ConfigurableApplicationContext {

    System.setProperty("log4j.skipJansi", "false")
    System.setProperty("user.home", FileManager.DIR_TMP.absolutePath)
    System.setProperty("workdir.tmp", FileManager.DIR_TMP.absolutePath)
    System.setProperty("workdir.log", FileManager.DIR_LOG.absolutePath) // log4j2.xml 使用

    val app = SpringApplication(clazz)
    app.addListeners(ApplicationBootListener())
    return app.run(*args)
}

