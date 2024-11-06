package com.gankcode.springboot.web.http.pageable;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @param <T> Iterable Object List
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseBean<T extends Iterable<?>> {

    @Parameter(description = "偏移量")
    private long offset;
    @Parameter(description = "数量")
    private long limit;

    @Parameter(description = "总数量")
    private long total;

    @Parameter(description = "数据列表")
    private T list;

    @Parameter(description = "元数据")
    private Object meta;

}
