package com.gankcode.springboot.db.config;

import com.gankcode.springboot.utils.JsonTemplate;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
public class DatasourceProperties {

    private String name;

    private String tablePrefix;

    private Map<String, Datasource> dynamic = new LinkedHashMap<>();

    private Properties druid;

    public Properties getDruid() {
        final Properties fixed = new Properties();
        druid.forEach((k, v) -> fixed.put("druid." + k, v));
        return fixed;
    }

//    public Map<String, Datasource> getDynamic() {
//        final Map<String, Datasource> fixed = new LinkedHashMap<>();
//        dynamic.forEach((k,v)->{
//            if(v instanceof Map<?,?>){
//                fixed.put(k, JsonTemplate.getInstance().fromJson((Map<?, ?>) v, Datasource.class));
//            }
//        });
//        return fixed;
//    }

    @Data
    public static class Datasource {
        private String type;
        private String username;
        private String password;
        private String url;
        private String driverClassName;
        private List<String> initSqls;

    }
}