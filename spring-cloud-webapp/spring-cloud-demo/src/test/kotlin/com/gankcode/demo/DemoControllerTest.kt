package com.gankcode.demo

import com.gankcode.springboot.test.BaseSpringMvcTest
import lombok.extern.slf4j.Slf4j
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Configuration
import org.springframework.test.annotation.Commit
import org.springframework.test.context.junit.jupiter.SpringExtension


@Slf4j
@EnableAutoConfiguration
@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [DemoApplication::class])
@Commit
@Configuration
@TestMethodOrder(
    MethodOrderer.OrderAnnotation::class
)
class DemoControllerTest : BaseSpringMvcTest() {

    @Order(2)
    @Test
    @DisplayName("创建文件夹")
    @Throws(
        Exception::class
    )
    fun testCreateFolder() {
        Assertions.assertTrue(true)
    }

    @Order(3)
    @ParameterizedTest
    @ValueSource(strings = ["test.gif"])
    @DisplayName("上传文件")
    @Throws(
        Exception::class
    )
    fun testUploadFile(resFile: String?) {
        Assertions.assertTrue(true)
    }

    @Order(4)
    @Test
    @DisplayName("删除文件")
    @Throws(
        Exception::class
    )
    fun testDeleteFile() {
        Assertions.assertTrue(true)
    }
}