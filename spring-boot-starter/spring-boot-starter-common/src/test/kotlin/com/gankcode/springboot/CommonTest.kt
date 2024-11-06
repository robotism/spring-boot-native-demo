package com.gankcode.springboot

import com.gankcode.springboot.kt.*
import com.gankcode.springboot.test.BaseJunitTest
import com.gankcode.springboot.utils.FileManager.getTempDirectory
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@ExtendWith(SpringExtension::class)
class CommonTest : BaseJunitTest() {

    @Order(1)
    @Test
    @DisplayName("Test Logger")
    fun testLogger() {
        System.err.println("this is system err println")
        log.error("this is log error print")
    }


    @Order(2)
    @Test
    @DisplayName("Test FileManager")
    fun testFileManager() {
        getTempDirectory()
    }

    @Order(3)
    @Test
    @DisplayName("Test Json")
    fun testJson() {

        data class A(
            var b: Int? = 0
        ) {
            constructor() : this(0)
        }

        val obj = A()
        val str = "{\"b\":0}"
        val arr = arrayOf(obj)

        log.info("obj.toJsonString() = {}", obj.toJsonString())
        log.info("obj.copy().toJsonString() = {}", obj.copy().toJsonString())
        log.info("str.toJsonObject(A::class) = {}", str.toJsonObject(A::class))
        assert(str == obj.toJsonString())
        assert(str == obj.copy().toJsonString())
        assert(str.toJsonObject(A::class) == obj)

        log.info("obj.toJsonNode().toString() = {}", obj.toJsonNode().toString())
        assert(obj.toJsonNode() != null)
        assert(str.toJsonNode() != null)

        log.info("obj.arrayType() = {}", A::class.java.arrayType())
        log.info("arr.toJsonNode() = {}", arr.toJsonNode())
        log.info("arr.toJsonArray() = {}", arr.toJsonString().toJsonArray(A::class.java))
        assert(arr.toJsonString().toJsonArray(A::class.java)?.get(0) == obj)

        log.info("obj.javaClass = {}", obj.javaClass)
        log.info("obj.javaType()?.rawClass = {}", obj.javaType()?.rawClass)
        assert(obj.javaClass == obj.javaType()?.rawClass)
    }
}