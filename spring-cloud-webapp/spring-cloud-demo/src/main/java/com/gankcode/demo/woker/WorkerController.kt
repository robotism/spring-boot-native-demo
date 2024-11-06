package com.gankcode.demo.woker

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.annotation.Resource
import lombok.RequiredArgsConstructor
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RequiredArgsConstructor
@Tag(name = "WorkerController", description = "WorkerController")
@RequestMapping("/workers")
@RestController
class WorkerController(
    @Resource
    private val workerMapper: WorkerMapper
) {

    @Operation(summary = "查询节点")
    @GetMapping("/{id}")
    fun getId(@PathVariable(name = "id") id: Long?): WorkerDO? {
        return workerMapper.selectOneById(id);
    }

}