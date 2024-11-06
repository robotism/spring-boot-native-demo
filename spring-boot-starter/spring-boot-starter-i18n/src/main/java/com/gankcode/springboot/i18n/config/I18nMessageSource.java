package com.gankcode.springboot.i18n.config;

import lombok.Getter;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.LinkedHashSet;
import java.util.Set;


@Getter
public class I18nMessageSource {

    private final Set<String> basenameSet = new LinkedHashSet<>(4);

    public void setBasenames(String... baseNames) {
        this.basenameSet.clear();
        addBasenames(baseNames);
    }

    public void addBasenames(String... baseNames) {
        if (!ObjectUtils.isEmpty(baseNames)) {
            for (String baseName : baseNames) {
                Assert.hasText(baseName, "Basename must not be empty");
                this.basenameSet.add(baseName.trim());
            }
        }
    }

}
