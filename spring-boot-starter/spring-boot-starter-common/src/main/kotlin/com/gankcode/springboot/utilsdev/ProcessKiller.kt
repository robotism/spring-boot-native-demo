package com.gankcode.springboot.utilsdev

import com.gankcode.springboot.utils.PlatformUtils
import com.gankcode.springboot.utils.Shell.execute
import com.gankcode.springboot.utils.Utils.getPid
import org.springframework.util.StringUtils
import java.util.regex.Pattern

object ProcessKiller {

    @JvmStatic
    fun killMyself() {
        killProcessByPid(getPid())
    }


    @JvmStatic
    fun killProcessByPid(vararg pids: Int?) {
        if (pids == null) {
            return
        }
        for (pid in pids) {
            if (pid == null || pid == 0) {
                continue
            }
            if (PlatformUtils.IS_WINDOWS) {
                execute("TaskKill", "/F", "/pid", "" + pid)
            } else if (PlatformUtils.IS_DARWIN) {
                execute("kill", "-9", "" + pid)
            } else if (PlatformUtils.IS_LINUX) {
                execute("kill", "" + pid)
            }
        }
    }

    @JvmStatic
    fun killProcessByPort(vararg ports: Int?) {
        if (ports == null) {
            return
        }

        for (port in ports) {
            if (port == null) {
                continue
            }
            val pids = findPidByPort(port) ?: continue
            killProcessByPid(*pids.toTypedArray<Int>())
        }
    }

    @JvmStatic
    fun findPidByPort(vararg ports: Any): Set<Int>? {
        for (port in ports) {
            val item = port.toString()
            try {
                val p = item.toInt()
                if (p < 80) {
                    continue
                }
                if (PlatformUtils.IS_WINDOWS) {
                    return findPidByPortInWindows(p)
                } else if (PlatformUtils.IS_DARWIN) {
                    return findPidByPortInDarwin(p)
                } else if (PlatformUtils.IS_LINUX) {
                    return findPidByPortInLinux(p)
                }
            } catch (ignored: Exception) {
            }
        }
        return null
    }

    @JvmStatic
    private fun findPidByPortInDarwin(port: Int): Set<Int> {
        val list: MutableSet<Int> = HashSet()
        val result = execute("lsof", "-i", "tcp:$port")
        if (!StringUtils.hasText(result)) {
            return list
        }
        val lines = result.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (line in lines) {
            try {
                val regex = "^java\\s*(\\d+)\\s*.*"
                val pattern = Pattern.compile(regex)
                val matcher = pattern.matcher(line)
                if (!matcher.find()) {
                    continue
                }
                val p = matcher.group(1).toInt()
                list.add(p)
            } catch (ignored: Exception) {
            }
        }
        return list
    }


    @JvmStatic
    private fun findPidByPortInLinux(port: Int): Set<Int> {
        val list: MutableSet<Int> = HashSet()
        val result = execute("netstat", "-tunl", "|", "grep", "" + port)
        if (!StringUtils.hasText(result)) {
            return list
        }
        val lines = result.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (line in lines) {
            if (!StringUtils.hasText(line) && !line.contains("LISTEN")) {
                continue
            }
            try {
                val str = line.replace(" ".toRegex(), "")
                val p =
                    str.split("LISTEN".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].trim { it <= ' ' }
                        .split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].toInt()
                list.add(p)
            } catch (ignored: Exception) {
            }
        }
        return list
    }


    @JvmStatic
    private fun findPidByPortInWindows(port: Int): Set<Int> {
        val list: MutableSet<Int> = HashSet()
        val result = execute(
            "NetStat", "-ano", "|", "FindStr",
            "\":$port \""
        )
        if (!StringUtils.hasText(result)) {
            return list
        }
        val lines = result.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (line in lines) {
            if (!StringUtils.hasText(line) && !line.contains("LISTENING")) {
                continue
            }
            try {
                val str = line.replace(" ".toRegex(), "")
                val p =
                    str.split("LISTENING".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].trim { it <= ' ' }
                        .toInt()
                list.add(p)
            } catch (ignored: Exception) {
            }
        }
        return list
    }
}