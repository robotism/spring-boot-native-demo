package com.gankcode.demokt

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "KotlinController", description = "KotlinController")
@RequestMapping("/kotlin")
@RestController
class KotlinController {

    @Operation(summary = "你好,Kotlin")
    @GetMapping("/hello")
    fun helloWorld(): String {
        return "hello kotlin"
    }

}