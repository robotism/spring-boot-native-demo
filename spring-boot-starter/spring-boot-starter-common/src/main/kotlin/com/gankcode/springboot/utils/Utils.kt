package com.gankcode.springboot.utils

import java.lang.management.ManagementFactory
import java.util.*


object Utils {

    @JvmStatic
    fun isTest(): Boolean {
        return FileManager.isRunningInTest()
    }

    @JvmStatic
    fun isNative(): Boolean {
        return FileManager.APPLICATION_SRC?.let { it.isFile && it.canExecute() } == true
    }

    @JvmStatic
    fun getPid(): Int {
        try {
            val runtime = ManagementFactory.getRuntimeMXBean()
            val name = runtime.name
            val index = name.indexOf('@')
            if (index != -1) {
                return name.substring(0, index).toInt()
            }
        } catch (ignored: Exception) {
        }
        return -1
    }

    @JvmStatic
    fun uuid(): String {
        return UUID.randomUUID().toString().replace("-".toRegex(), "")
    }

}