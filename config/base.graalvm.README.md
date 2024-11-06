## Spring Boot 3.x 在使用的过程中遇到的问题

- 解决native-image反射、代理、类序列化等问题。

```bash

java -agentlib:native-image-agent=config-output-dir=$(pwd)/config/resources/main/META-INF/native-image! -jar D:\Projects\GankCode\gankcloud\spring-cloud-webapp/spring-cloud-demo\build\libs\spring-cloud-demo-debug-2024.0501.0000.jar

```
执行完该命令会在 resources/META-INF/native-image 文件夹下面生成 reflect-config.json 、 proxy-config.json 、 serialization-config.json 等文件。

- 解决DNS问题
使用 http interface 会出现如下错误: Caused by: java.lang.NoSuchFieldError: sun.net.dns.ResolverConfigurationImpl.os_searchlist

### 解决办法：共下面两个步骤。

(1).在resources/META-INF/native-image/jni-config.json 文件添加如下配置
```
{
  "name": "sun.net.dns.ResolverConfigurationImpl",
  "fields": [
    {
      "name": "os_searchlist"
    },
    {
      "name": "os_nameservers"
    }
  ]
}
```

(2).在 pom.xml 文件里面加入plugins标签里面添加 `--initialize-at-run-time=sun.net.dns.ResolverConfigurationImpl` 参数，完整plugin标签配置如下：

```
<plugin>
    <groupId>org.graalvm.buildtools</groupId>
    <artifactId>native-maven-plugin</artifactId>
    <configuration>
        <buildArgs>
            --initialize-at-run-time=sun.net.dns.ResolverConfigurationImpl
        </buildArgs>
    </configuration>
</plugin>
```