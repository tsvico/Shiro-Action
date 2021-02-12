package im.zhaojun.common.information

import im.zhaojun.common.util.*
import java.lang.management.ManagementFactory

/**
 * JVM相关信息
 */
class Jvm {
    /**
     * 当前JVM占用的内存总数(M)
     */
    var total = 0.0
        get() = Arith.div(field, (1024 * 1024).toDouble(), 2)


    /**
     * JVM最大可用内存总数(M)
     */
    var max = 0.0
        get() = Arith.div(field, (1024 * 1024).toDouble(), 2)

    /**
     * JVM空闲内存(M)
     */
    var free = 0.0
        get() = Arith.div(field, (1024 * 1024).toDouble(), 2)

    /**
     * JDK版本
     */
    var version: String? = null

    /**
     * JDK路径
     */
    var home: String? = null


    val used: Double
        get() = Arith.div(total - free, (1024 * 1024).toDouble(), 2)
    val usage: Double
        get() = Arith.mul(Arith.div(total - free, total, 4), 100.0)

    /**
     * 获取JDK名称
     */
    val name: String
        get() = ManagementFactory.getRuntimeMXBean().vmName

    /**
     * JDK启动时间
     */
    val startTime: String
        get() = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, DateUtils.serverStartDate)

    /**
     * JDK运行时间
     */
    val runTime: String
        get() = DateUtils.getDatePoor(DateUtils.nowDate, DateUtils.serverStartDate)
}