package com.gankcode.springboot.utils

import org.springframework.util.StringUtils
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets


open class Terminal {

    var onPrintListening: ((text: String) -> Unit)? = null

    val onExitListening: (() -> Unit)? = null

    private var process: Process? = null

    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    private var threadInput: Thread? = null

    @Throws(IOException::class)
    protected fun createProcess(cmd: Array<String?>): Process {
        return ProcessBuilder(*cmd)
            .redirectErrorStream(true)
            .start()
    }

    @Throws(IOException::class)
    fun open(command: String?) {
        close()
        process = if (StringUtils.hasText(command)) {
            createProcess(arrayOf(command))
        } else {
            createProcess(COMMAND)
        }
        inputStream = process!!.inputStream
        outputStream = process!!.outputStream

        threadInput = Thread {
            try {
                val buffer = ByteArray(1024)
                while (!Thread.currentThread().isInterrupted) {
                    val size = inputStream?.read(buffer) ?: -1
                    if (size == -1) {
                        break
                    }
                    if (size == 0) {
                        continue
                    }
                    val text = String(
                        buffer,
                        0,
                        size,
                        CHARSET
                    )
                    onPrintListening?.invoke(text)
                }
            } catch (ignored: Exception) {
            }
            try {
                onExitListening?.invoke()
            } catch (ignored: Exception) {
            }
            close()
        }
        threadInput!!.start()
    }

    @Throws(InterruptedException::class)
    fun waitFor() {
        if (process != null) {
            process!!.waitFor()
        }
    }

    @Throws(IOException::class)
    fun write(cmd: String) {
        if (outputStream != null) {
            outputStream!!.write(cmd.toByteArray(CHARSET))
            outputStream!!.flush()
        }
    }

    fun setWindowSize(cols: Int, rows: Int) {

    }

    fun close() {
        if (inputStream != null) {
            try {
                inputStream!!.close()
            } catch (ignored: Exception) {
            }
            inputStream = null
        }
        if (outputStream != null) {
            try {
                outputStream!!.close()
            } catch (ignored: Exception) {
            }
            outputStream = null
        }
        if (threadInput != null) {
            threadInput!!.interrupt()
            threadInput = null
        }
        if (process != null) {
            process!!.destroy()
            process = null
        }
    }

    companion object {
        /**
         * 是否是windows系统
         */
        private val IS_WIN = "\\" == File.separator

        /**
         * 默认的编码, windows使用GBK
         */
        private val CHARSET: Charset = if (IS_WIN) {
            try {
                Charset.forName("GBK")
            } catch (ignored: Exception) {
                StandardCharsets.ISO_8859_1
            }
        } else {
            StandardCharsets.UTF_8
        }

        /**
         * @see [jPowerShell](https://github.com/profesorfalken/jPowerShell)
         */
        private val POWERSHELL =
            arrayOf<String?>("PowerShell", "-ExecutionPolicy", "Bypass", "-NoExit", "-NoProfile", "-Command", "-")

        private val BASH = arrayOf<String?>("sh")

        private val COMMAND: Array<String?> = if (IS_WIN) POWERSHELL else BASH
    }
}