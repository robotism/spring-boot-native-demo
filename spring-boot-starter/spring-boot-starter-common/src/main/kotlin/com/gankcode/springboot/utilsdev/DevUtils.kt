package com.gankcode.springboot.utilsdev

import com.gankcode.springboot.utils.Utils.isTest
import com.gankcode.springboot.utilsdev.ProcessKiller.killMyself
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.lang.reflect.Method

object DevUtils {
    /**
     * 打印错误信息并结束进程
     *
     * @param clz  类信息
     * @param fmt  格式化格式
     * @param args 格式化参数
     */
    @JvmStatic
    fun error(clz: Class<*>?, fmt: String?, vararg args: Any?) {
        error(clz, null as Method?, fmt, *args)
    }

    /**
     * 打印错误信息并结束进程
     *
     * @param method 方法信息
     * @param fmt    格式化格式
     * @param args   格式化参数
     */
    @JvmStatic
    fun error(method: Method, fmt: String?, vararg args: Any?) {
        val clz = method.declaringClass
        error(clz, method, fmt, *args)
    }

    /**
     * 打印错误信息并结束进程
     *
     * @param clz    类信息
     * @param method 方法信息
     * @param fmt    格式化格式
     * @param args   格式化参数
     */
    @JvmStatic
    fun error(clz: Class<*>?, method: Method?, fmt: String?, vararg args: Any?) {
        val message = String.format(fmt!!, *args)
        exit(
            "代码规范检查错误: %s %s%s  ",
            message,
            if (clz == null) "" else "\\n  class  : ${clz.toGenericString()}",
            if (method == null) "" else "\\n  method : ${method.toGenericString()}"
        )
    }

    @JvmStatic
    fun exit(throwable: Throwable?) {
        if (throwable != null) {
            val writer: Writer = StringWriter()
            val printWriter = PrintWriter(writer)
            throwable.printStackTrace(printWriter)
            exit("%s", writer.toString())
        } else {
            exit(null as String?)
        }
    }

    @JvmStatic
    fun exit(fmt: String?, vararg args: Any?) {
        val message = if (fmt == null) null else String.format(fmt, *args)
        val lines = message?.replace("\r\n".toRegex(), "\n")?.split("\n".toRegex())?.dropLastWhile { it.isEmpty() }
            ?.toTypedArray()
            ?: arrayOf()
        val prefix = "->* "

        val sb: MutableList<String> = ArrayList()
        sb.add("\n")
        sb.add(prefix)
        sb.add(prefix + "Application " + (if (isTest()) "Error" else "Exit") + "!")
        for (line in lines) {
            sb.add(prefix + line)
        }
        sb.add(prefix)
        sb.add("\n")

        System.err.println(java.lang.String.join("\n", sb))

        if (!isTest()) {
//            System.exit(1);
            killMyself()
        }
    }
}