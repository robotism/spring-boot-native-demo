package com.gankcode.demo.hello

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "HelloController", description = "HelloController")
@RestController
@RequestMapping("/hello")
class HelloController {

    @Operation(summary = "你好,世界")
    @GetMapping("/world")
    fun helloWorld(): String {
        return "hello world"
    }

}