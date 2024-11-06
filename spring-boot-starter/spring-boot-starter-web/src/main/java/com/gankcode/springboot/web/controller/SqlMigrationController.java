package com.gankcode.springboot.web.controller;

import com.gankcode.springboot.annotation.ConditionalOnDebug;
import com.gankcode.springboot.config.EnvConfig;
import com.gankcode.springboot.db.config.mybatis.flex.DatasourceAutoConfiguration;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@ConditionalOnDebug
@Tag(name = "SqlMigrationController", description = "SqlMigrationController")
@RequiredArgsConstructor
@RestController
public class SqlMigrationController {

    private final DatasourceAutoConfiguration datasourceAutoConfiguration;

    private final EnvConfig envConfig;

    @Operation(summary = "下载SQL文件")
    @GetMapping(value = "sqls", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getSqls(final HttpServletResponse response) throws Exception {
        final String filename = envConfig.getApplicationName() + "-" + envConfig.getApplicationProfile();
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename + ".sql");
        return datasourceAutoConfiguration.getSqls();
    }
}
