package com.gankcode.springboot.utils

import java.net.*
import java.util.regex.Pattern


object NetUtils {

    /**
     * 获取当前计算机的LAN网IpV4地址
     *
     * @return ipv4: xxx.xxx.xxx.xxx
     */
    @JvmStatic
    fun getLanIpv4(): String? {
        val map = getLanNetwork()
        for ((key) in map) {
            return key.hostAddress
        }

        try {
            return InetAddress.getLocalHost().hostAddress
        } catch (ignored: Exception) {
        }

        return null
    }

    /**
     * 获取当前局域网网卡地址
     *
     * @return mac地址
     */
    @JvmStatic
    fun getLanMac(): String? {
        return getLanNetwork().values.firstNotNullOfOrNull { getNetworkInterfaceMac(it) }
    }

    @JvmStatic
    private fun getLanNetwork(): Map<InetAddress, NetworkInterface> {
        return getLanNetwork(true, false)
    }

    /**
     * 获取当前局域网有效信息
     *
     * @param ipv4 包含IPV4
     * @param ipv6 包含IPV6
     * @return 网络信息
     */
    @JvmStatic
    private fun getLanNetwork(ipv4: Boolean, ipv6: Boolean): Map<InetAddress, NetworkInterface> {
        val map: MutableMap<InetAddress, NetworkInterface> = LinkedHashMap()
        try {
            val networks = NetworkInterface.getNetworkInterfaces()
            while (networks.hasMoreElements()) {
                try {
                    val ni = networks.nextElement()
                    val ias = ni.inetAddresses
                    if (!ni.isUp || !ni.supportsMulticast() || ni.isVirtual) {
                        continue
                    }

                    val name = ni.displayName.lowercase()

                    if (name.contains("#")
                        || name.contains("virtual")
                        || name.contains("vbox")
                        || name.contains("vmnet")
                        || name.contains("vnic")
                    ) {
                        continue
                    }

                    while (ias.hasMoreElements()) {
                        val ia = ias.nextElement()
                        if (ia.isLoopbackAddress || !ia.isSiteLocalAddress) {
                            continue
                        }

                        if (ia is Inet4Address && ipv4) {
                            map[ia] = ni
                        }
                        if (ia is Inet6Address && ipv6) {
                            map[ia] = ni
                        }
                    }
                } catch (ignored: Exception) {
                }
            }
        } catch (ignored: Exception) {
        }

        return map
    }


    /**
     * 获取网卡 mac 地址
     *
     * @param ni 网卡
     * @return mac地址
     */
    @JvmStatic
    fun getNetworkInterfaceMac(ni: NetworkInterface?): String? {
        if (ni == null) {
            return null
        }
        try {
            val sb = StringBuilder()
            val buffer = ni.hardwareAddress
            for (b in buffer) {
                sb.append(String.format("%02X", b))
            }
            return sb.toString()
        } catch (ignored: Exception) {
        }
        return null
    }

    /**
     * 将int数字转换成ipv4地址
     *
     * @param ip IP 整型
     * @return IP V4 字符串
     */
    @JvmStatic
    fun parseIpv4(ip: Int?): String? {
        if (ip == null) {
            return null
        }
        val sb = StringBuilder()
        sb.append(ip ushr 24)
        sb.append(".")
        sb.append((ip and 0x00FFFFFF) ushr 16)
        sb.append(".")
        sb.append((ip and 0x0000FFFF) ushr 8)
        sb.append(".")
        sb.append((ip and 0x000000FF))
        return sb.toString()
    }

    /**
     * IPv4地址转换为int类型数字
     *
     * @param ipv4 IP V4 字符串
     * @return IP 整型
     */
    @JvmStatic
    fun parseIpv4ToInt(ipv4: String): Int? {
        try {
            if (InetAddress.getByName(ipv4) is Inet4Address) {
                val ip = ipv4.split(Pattern.quote(".").toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                val ip0 = ip[0].toInt()
                val ip1 = ip[1].toInt()
                val ip2 = ip[2].toInt()
                val ip3 = ip[3].toInt()
                return (ip0 shl 24) + (ip1 shl 16) + (ip2 shl 8) + ip3
            }
        } catch (ignored: Exception) {
        }
        return null
    }


    /**
     * 判断是否为本地局域网ip地址
     *
     * @param ip ip地址
     * @return 是否为本地局域网ip地址
     */
    @JvmStatic
    fun isLocalIp(ip: String?): Boolean {
        try {
            val addr = InetAddress.getByName(ip).address
            return isLocalIp(addr)
        } catch (ignored: Exception) {
        }
        return false
    }

    /**
     * 判断是否为本地局域网ip地址
     *
     * @param addr ip地址
     * @return 是否为本地局域网ip地址
     */
    @JvmStatic
    fun isLocalIp(addr: ByteArray?): Boolean {
        if (addr == null) {
            return false
        }
        val b0 = addr[0]
        val b1 = addr[1]
        //10.x.x.x/8
        val section1: Byte = 0x0A
        //172.16.x.x/12
        val section2 = 0xAC.toByte()
        val section3 = 0x10.toByte()
        val section4 = 0x1F.toByte()
        //192.168.x.x/16
        val section5 = 0xC0.toByte()
        val section6 = 0xA8.toByte()
        when (b0) {
            section1 -> return true
            section2 -> if (b1 in section3..section4) {
                return true
            }

            section5 -> if (b1 == section6) {
                return true
            }

            else -> {}
        }
        return false
    }


    /**
     * 测试网络连通(socket模拟实现)
     *
     * @param host    域名或ip
     * @param port    端口
     * @param timeout 超时时间
     * @return 用时时间, 返回-1表示连接失败
     */
    @JvmStatic
    fun ping(host: String?, port: Int, timeout: Int): Long {
        try {
            val address = InetSocketAddress(host, port)
            return ping(address, timeout)
        } catch (ignored: Exception) {
            return -1
        }
    }

    /**
     * 测试网络连通(socket模拟实现)
     *
     * @param address 目标地址
     * @param timeout 超时时间
     * @return 用时时间, 返回-1表示连接失败
     */
    @JvmStatic
    fun ping(address: InetSocketAddress?, timeout: Int): Long {
        try {
            Socket().use { socket ->
                val startTime = System.currentTimeMillis()
                socket.connect(address, timeout)
                return System.currentTimeMillis() - startTime
            }
        } catch (ignored: Exception) {
            return -1
        }
    }
}