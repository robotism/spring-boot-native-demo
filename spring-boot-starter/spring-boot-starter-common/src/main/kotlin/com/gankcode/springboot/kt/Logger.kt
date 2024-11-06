package com.gankcode.springboot.kt

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

//val Any.log: Logger
//    get() = logger()

fun Any.logger(): Logger {
    return LoggerFactory.getLogger(javaClass)
}

val Any.log by LoggerHolder()


internal class LoggerHolder : ReadOnlyProperty<Any, Logger> {

    companion object {
        @JvmStatic
        private val cache = mutableMapOf<Class<*>, Logger>()
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): Logger {
        val clazz = when (thisRef) {
            is Class<*> -> thisRef
            is KClass<*> -> thisRef.java
            else -> thisRef::class.java
        }
        return cache.computeIfAbsent(clazz) {
            LoggerFactory.getLogger(it)
        }
    }

}
