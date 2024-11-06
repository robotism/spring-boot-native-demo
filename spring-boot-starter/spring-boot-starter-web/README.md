# SpringCloud 基础库(配置)

## 基础配置

- [X] 代码检查
    - [X] Swagger 注解检查
        - 类必须使用 @Tag(name = "Controller 类标签", description="描述") 注解声明
        - 方法必须使用 @Operation(summary="描述") 注解声明
        - 方法参数必须使用 @Parameter(name="参数名", description="描述") 或者 RequestBody(description = "描述", required = true, content = @Content(schema = @Schema(type = "类型"))) 注解声明
    - [X] Http 请求类型注解检查
        - Controller 类只能用@RequestMapping注解
        - Controller 方法只能用@GetMapping,@PostMapping...等注解
- [X] API分组: `@ApiMapping`
    - 主要用于接口分组: 效果如
        - `@ApiMapping("v1")`: `/api/helloword`->`/api/v1/helloword`
        - `@ApiMapping("v2")`: `/api/helloword`->`/api/v2/helloword`
    - 作用范围
        - 注解到`package-info`: 分组作用范围为当前包下所有Controller
        - 注解到`*Controller`: 分组作用范围为当前Controller
        - 注解在包和类同时存在时, 按包层次自动拼接
- [X] 错误码: `@ErrorCode 和 BaseErrorCode`
    - `@ErrorCode` 用于错误码声明, 类必须继承 `BaseErrorCode`
    - `ErrorCode` 默认错误码(错误, 成功, 失败)
    - 自动扫描所有错误码 并校验是否存在冲突
    - 消息内容为  `I18n 多国语的 Key`, 根据请求自动翻译
- [X] 全局响应处理和异常处理: 接口返回具体bean 或者 void 即可
- [X] 请求错误:  
    - 系统内部错误: 无法预估的代码逻辑错误
    - 业务逻辑错误: 业务逻辑手动抛出 RequestException(指明业务错误错误码和错误消息)
    - 参数校验错误: 