package com.gankcode.springboot.web.http;

import com.gankcode.springboot.utils.JsonTemplate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * @param <T> 泛型: 数据类型
 */
@Schema(description = "Http 统一数据响应结果")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseBean<T> {

    @Builder.Default
    @Schema(description = "错误码(用于逻辑判断)", required = true, accessMode = Schema.AccessMode.READ_ONLY)
    private int code = ErrorCode.FAILURE.getCode();

    @Builder.Default
    @Schema(description = "错误信息(多国语, 不用于逻辑判断)", required = true, accessMode = Schema.AccessMode.READ_ONLY)
    private String message = ErrorCode.FAILURE.getI18nMessage();

    @Schema(description = "数据", accessMode = Schema.AccessMode.READ_ONLY)
    private T data;

    @Builder.Default
    @Schema(description = "时间", accessMode = Schema.AccessMode.READ_ONLY)
    private final OffsetDateTime time = OffsetDateTime.now();

    public boolean isSuccess() {
        return this.code == ErrorCode.SUCCESS.getCode();
    }

    public ResponseBean<T> success(T data) {
        this.code = ErrorCode.SUCCESS.getCode();
        this.message = ErrorCode.SUCCESS.getI18nMessage();
        this.data = data;
        return this;
    }

    public ResponseBean<T> failure(BaseErrorCode errorCode, T data) {
        this.code = errorCode.getCode();
        this.message = errorCode.getI18nMessage();
        this.data = data;
        return this;
    }


    @Override
    public String toString() {
        return JsonTemplate.getInstance().toJson(this);
    }
}

