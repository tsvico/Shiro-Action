package im.zhaojun.common.util

import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.net.InetAddress
import java.net.UnknownHostException

object IPUtils {//根据网卡取本机配置的 IP
    // 多个代理的情况，第一个 IP 为客户端真实 IP,多个 IP 按照','分割
    /**
     * 获取请求 IP (WEB 服务)
     *
     * @return IP 地址
     */
    val ipAddr: String?
        get() {
            val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
            var ip = request.getHeader("x-forwarded-for")
            if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
                ip = request.getHeader("Proxy-Client-IP")
            }
            if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
                ip = request.getHeader("WL-Proxy-Client-IP")
            }
            if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
                ip = request.remoteAddr
                if (ip == "127.0.0.1") {
                    //根据网卡取本机配置的 IP
                    val inet: InetAddress?
                    try {
                        inet = InetAddress.getLocalHost()
                        ip = inet.hostAddress
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            // 多个代理的情况，第一个 IP 为客户端真实 IP,多个 IP 按照','分割
            if (ip != null && ip.length > 15) {
                if (ip.indexOf(",") > 0) {
                    ip = ip.substring(0, ip.indexOf(","))
                }
            }
            return ip
        }

    /**
     * 获取当前计算机 IP
     */
    val hostIp: String
        get() {
            try {
                return InetAddress.getLocalHost().hostAddress
            } catch (e: UnknownHostException) {
                e.printStackTrace()
            }
            return "127.0.0.1"
        }

    /**
     * 获取当前计算机名称
     */
    val hostName: String
        get() {
            try {
                return InetAddress.getLocalHost().hostName
            } catch (e: UnknownHostException) {
                e.printStackTrace()
            }
            return "未知"
        }
}