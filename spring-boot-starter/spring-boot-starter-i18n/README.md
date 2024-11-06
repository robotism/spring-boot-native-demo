# SpringCloud 基础库(配置)

## 基础配置

- [X] I18nMessageSource 添加多国语文件
    ```java
        // 示例
        @Configuration
        class I18nConfig {
            // library-spring-cloud 库的多国语配置文件
            // bean 名称自行修改, 请勿重复
            @Bean("customI18nMessageSource")
            public I18nMessageSource customI18nMessageSource() {
                final I18nMessageSource i18nMessageSource = new I18nMessageSource();
                i18nMessageSource.addBasenames("library-spring-cloud");
                return i18nMessageSource;
            }
        }
    ```
- [X] I18nUtils
    - 缓存所有装配的多国语翻译
    - 提供翻译方法
    - 提供获取语言名
    - 默认 zh_CN 简体中文
- [X] I18nString
    - 多国语字符串
    - 目前主要用于多国语字符串类型限定, 比如Http接口响应的错误消息