
package com.gankcode.springboot.db.config;

import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

public interface DynamicTableNameProcessor {

    String process(String tableName);

    @RequiredArgsConstructor
    class PrefixDynamicTableNameProcessor implements DynamicTableNameProcessor {

        private final String prefix;

        @Override
        public String process(String tableName) {
            if (!StringUtils.hasText(tableName) || !StringUtils.hasText(prefix)) {
                return tableName;
            } else {
                return tableName.replaceFirst("^t_", prefix);
            }
        }

    }

}
