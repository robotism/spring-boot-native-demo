package com.gankcode.springboot.utils

import com.gankcode.springboot.kt.asFile
import com.gankcode.springboot.utilsdev.DevUtils
import org.springframework.boot.system.ApplicationHome
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.util.FileCopyUtils
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.file.Files
import java.time.Duration
import java.util.*


object FileManager {

    @JvmField
    val APPLICATION_HOME = ApplicationHome(javaClass).let {
        if (it.source?.isFile == true) it else ApplicationHome()
    }

    @JvmField
    val APPLICATION_SRC: File? = APPLICATION_HOME.source ?: getResource("")?.file

    @JvmField
    val APPLICATION_DIR: File = APPLICATION_HOME.dir

    private val isTest = APPLICATION_SRC != null
            && APPLICATION_SRC.absolutePath.contains("classes")
            && APPLICATION_SRC.absolutePath.contains("test")

    init {
        System.err.println("app home src : $APPLICATION_SRC")
        System.err.println("app home dir : $APPLICATION_DIR")
    }


    @JvmField
    val DIR_ROOT: File = if (APPLICATION_SRC?.isFile == true) {
        APPLICATION_DIR.resolve(APPLICATION_SRC.nameWithoutExtension)
    } else if (APPLICATION_SRC?.absolutePath?.contains("classes") == true) {
        APPLICATION_SRC.absolutePath.split("classes")[0].asFile()!!
    } else {
        APPLICATION_SRC ?: APPLICATION_DIR
    }

    /**
     * 本地文件存储目录
     */
    @JvmField
    val DIR_FILE: File = DIR_ROOT.resolve("files").normalize()

    init {
        System.err.println("usr dir root : $DIR_ROOT")
        System.err.println("usr dir file : $DIR_FILE")
    }

    /**
     * 用于存放资源文件
     */
    @JvmField
    val DIR_RESOURCES: File = DIR_FILE.resolve("resources").normalize()

    /**
     * 用于存放 GUID 值管理的文件
     */
    @JvmField
    val DIR_GUID: File = DIR_FILE.resolve("guid").normalize()

    /**
     * 用于存放缩略图
     */
    @JvmField
    val DIT_THUMB: File = DIR_FILE.resolve("thumb").normalize()

    /**
     * 用于存放日志目录
     */
    @JvmField
    val DIR_LOG: File = DIR_FILE.resolve("logs").normalize()

    /**
     * 用于存放临时文件
     */
    @JvmField
    val DIR_TMP: File = DIR_FILE.resolve("tmp").normalize()


    init {
        mkdirs(DIR_FILE, true)
        mkdirs(DIR_RESOURCES, true)
        mkdirs(DIR_GUID, true)
        mkdirs(DIT_THUMB, true)
        mkdirs(DIR_LOG, true)
        mkdirs(DIR_TMP, true)
    }

    @JvmStatic
    fun isRunningInTest(): Boolean {
        return isTest
    }

    /**
     * 创建目录
     *
     * @param dir          目录
     * @param exitIfFailed 如果创建失败则退出程序
     */
    @JvmStatic
    fun mkdirs(dir: File?, exitIfFailed: Boolean = false) {
        dir ?: return
        if (!dir.exists() && !dir.mkdirs()) {
            if (exitIfFailed) {
                DevUtils.exit("File IOException : mkdir failed [$dir]")
            } else {
                System.err.println("File IOException : mkdir failed [$dir]")
            }
        }
    }


    @JvmStatic
    @Throws(Exception::class)
    fun getResourceFile(path: String): File {
        val target = File(DIR_RESOURCES, path)
        val folder = target.parentFile
        val resource = getResource(path)
        if (resource == null || !resource.exists()) {
            throw FileNotFoundException("resource not found :$path")
        }
        if (!folder.exists() && !folder.mkdirs()) {
            throw IOException("create directory error: $folder")
        }
        if (target.exists() && target.length() != resource.contentLength()) {
            target.delete()
        }
        if (!target.exists() && !target.createNewFile()) {
            throw IOException("create file error: $target")
        }
        if (target.exists() && target.length() == 0L) {
            FileCopyUtils.copy(resource.inputStream, Files.newOutputStream(target.toPath()))
        }
        return target
    }


    @JvmStatic
    fun getResource(path: String): Resource? {
        val resourceLoader: ResourceLoader = PathMatchingResourcePatternResolver()
        val resource = resourceLoader.getResource(path)
        if (resource.exists()) {
            return resource
        }

        val resources = getResources(path)
        if (resources.isNotEmpty()) {
            return resources[0]
        }
        return null
    }

    @JvmStatic
    fun getResources(path: String): Array<Resource?> {
        val resolver = PathMatchingResourcePatternResolver()
        try {
            val pattern = "classpath*:/$path".replace("/+".toRegex(), "/")
            return resolver.getResources(pattern)
        } catch (ignored: Exception) {
        }
        return arrayOfNulls(0)
    }

    @JvmStatic
    fun getTempDirectory(): File {
        return File(System.getProperty("java.io.tmpdir"))
    }

    @JvmStatic
    fun deleteRecursively(vararg files: File?): Long {
        var size: Long = 0
        for (file in files) {
            if (file == null || !file.exists()) {
                continue
            }
            if (file.isFile) {
                val len = file.length()
                if (file.delete()) {
                    size += len
                }
            }
            if (file.isDirectory) {
                size += file.listFiles()?.let { deleteRecursively(*it) } ?: 0
            }
        }
        return size
    }

    @JvmStatic
    fun cleanTempFiles(before: Duration): Long {
        var size: Long = 0

        val time = System.currentTimeMillis() - before.toMillis()

        val folder = getTempDirectory()

        val items = folder.listFiles() ?: return 0
        for (item in items) {
            if (item.lastModified() < time) {
                size += deleteRecursively(item)
            }
        }
        return size
    }
}