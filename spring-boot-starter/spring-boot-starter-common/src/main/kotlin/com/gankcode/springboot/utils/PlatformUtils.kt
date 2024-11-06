package com.gankcode.springboot.utils

import com.gankcode.springboot.utils.Shell.execute
import org.springframework.util.StringUtils
import java.io.File
import java.net.InetAddress
import java.util.*
import java.util.regex.Pattern


object PlatformUtils {

    @JvmField
    val OS_NAME: String = System.getProperty("os.name").lowercase(Locale.SIMPLIFIED_CHINESE)

    @JvmField
    val OS_ARCH: String = System.getProperty("os.arch").lowercase(Locale.SIMPLIFIED_CHINESE)

    @JvmField
    val IS_VERSION: String = System.getProperty("os.version").lowercase(Locale.SIMPLIFIED_CHINESE)


    @JvmField
    val IS_WINDOWS: Boolean =
        OS_NAME.contains("windows") && "\\" == File.separator

    @JvmField
    val IS_DARWIN: Boolean =
        OS_NAME.contains("mac") && "/" == File.separator

    @JvmField
    val IS_LINUX: Boolean =
        OS_NAME.contains("linux") && "/" == File.separator

    @JvmStatic
    fun getHostname(): String? {
        try {
            val addr = InetAddress.getLocalHost()
            return addr.hostName
        } catch (ignored: Exception) {
        }
        return null
    }

    @JvmStatic
    fun findCommand(cmdName: String?): File? {
        if (!StringUtils.hasText(cmdName)) {
            return null
        }

        val path: String?
        val result = if (!IS_WINDOWS) {
            execute("which", cmdName)
        } else {
            execute("PowerShell", "Get-Command ", cmdName)
        }
        if (!StringUtils.hasText(result)) {
            path = null
        } else {
            val regex = "([A-Za-z]:\\\\.*\\.exe)"
            val pattern = Pattern.compile(regex)
            val matcher = pattern.matcher(result)
            path = if (matcher.find()) {
                matcher.group(1)
            } else {
                result
            }
        }

        val file = if (path == null) null else File(path.trim { it <= ' ' })

        return if (file != null && file.exists() && file.canExecute()) {
            file
        } else {
            null
        }
    }
}