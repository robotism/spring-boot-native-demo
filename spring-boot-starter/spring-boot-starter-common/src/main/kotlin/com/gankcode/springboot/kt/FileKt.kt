package com.gankcode.springboot.kt

import java.io.File


fun File.isSymLink(): Boolean {
    return try {
        this.canonicalPath != this.absolutePath
    } catch (e: Exception) {
        false
    }
}

fun File.canonicalFile(): File {
    return try {
        this.canonicalFile
    } catch (e: Exception) {
        this
    }
}


fun String?.asFile(): File? {
    return if (this != null) File(this) else null
}

fun String?.toFile(): File? {
    return if (this != null) File(this) else null
}

fun File?.deleteRecursive(): Long {
    if (this == null || !this.exists()) {
        return 0L
    }
    var count = 0L
    if (this.isDirectory) {
        this.listFiles()?.forEach { count += it.deleteRecursive() }
    }
    if (this.delete()) {
        count++
    }
    return count
}

fun File?.size(): Long {
    if (this == null || this.exists().not()) {
        return 0
    }
    if (this.isFile) {
        return this.length()
    }
    if (this.isDirectory) {
        val items = this.listFiles() ?: return 0
        var size = 0L
        for (item in items) {
            size += if (item.isFile) {
                item.length()
            } else {
                item.size()
            }
        }
        return size
    }
    return 0
}

