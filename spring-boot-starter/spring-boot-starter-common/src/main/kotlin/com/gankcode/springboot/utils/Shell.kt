package com.gankcode.springboot.utils

import com.gankcode.springboot.kt.log



object Shell {
    @JvmStatic
    fun execute(vararg command: String?): String {
        log.debug("#_> " + java.lang.String.join(" ", *command))
        val sb = StringBuilder()
        val terminal = Terminal()
        try {
            terminal.open(null)
            terminal.onPrintListening = { sb.append(it) }
            terminal.write(java.lang.String.join(" ", *command) + " \n")
            terminal.write("\n \nexit\n")
            terminal.waitFor()
        } catch (e: Exception) {
            Shell.log.error("", e)
        } finally {
            terminal.close()
        }
        if (Utils.isTest()) {
            Shell.log.debug(sb.toString())
        }
        return sb.toString()
    }
}