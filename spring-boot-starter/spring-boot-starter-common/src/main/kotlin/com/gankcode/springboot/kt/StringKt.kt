package com.gankcode.springboot.kt

import org.springframework.util.StringUtils


fun String?.hasText(): Boolean {
    return StringUtils.hasText(this)
}