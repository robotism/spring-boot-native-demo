package com.gankcode.demo

import com.gankcode.springboot.SpringApplicationKt
import com.gankcode.springboot.launch
import org.springframework.boot.autoconfigure.SpringBootApplication


@SpringBootApplication(proxyBeanMethods = false)
class DemoApplication : SpringApplicationKt()

fun main(args: Array<String>) {

    launch<DemoApplication>(*args)
}
