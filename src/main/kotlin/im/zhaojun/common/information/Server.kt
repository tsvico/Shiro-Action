package im.zhaojun.common.information

import im.zhaojun.common.util.IPUtils
import oshi.SystemInfo
import oshi.hardware.CentralProcessor
import oshi.hardware.CentralProcessor.TickType
import oshi.hardware.GlobalMemory
import oshi.software.os.OperatingSystem
import oshi.util.Util
import java.util.*

/**
 * 服务器相关信息
 */
class Server {
    /**
     * CPU相关信息
     */
    var cpu = Cpu()

    /**
     * 內存相关信息
     */
    var mem = Mem()

    /**
     * JVM相关信息
     */
    var jvm = Jvm()

    /**
     * 服务器相关信息
     */
    var sys = Sys()

    /**
     * 磁盘相关信息
     */
    private var sysFiles: MutableList<SysFile> = LinkedList()
    fun getSysFiles(): List<SysFile> {
        return sysFiles
    }

    fun setSysFiles(sysFiles: MutableList<SysFile>) {
        this.sysFiles = sysFiles
    }

    @Throws(Exception::class)
    fun copyTo() {
        val si = SystemInfo()
        val hal = si.hardware
        setCpuInfo(hal.processor)
        setMemInfo(hal.memory)
        setSysInfo()
        setJvmInfo()
        setSysFiles(si.operatingSystem)
    }

    /**
     * 设置CPU信息
     */
    private fun setCpuInfo(processor: CentralProcessor) {
        // CPU信息
        val prevTicks = processor.systemCpuLoadTicks
        Util.sleep(OSHI_WAIT_SECOND.toLong())
        val ticks = processor.systemCpuLoadTicks
        val nice = ticks[TickType.NICE.index] - prevTicks[TickType.NICE.index]
        val irq = ticks[TickType.IRQ.index] - prevTicks[TickType.IRQ.index]
        val softirq = ticks[TickType.SOFTIRQ.index] - prevTicks[TickType.SOFTIRQ.index]
        val steal = ticks[TickType.STEAL.index] - prevTicks[TickType.STEAL.index]
        val cSys = ticks[TickType.SYSTEM.index] - prevTicks[TickType.SYSTEM.index]
        val user = ticks[TickType.USER.index] - prevTicks[TickType.USER.index]
        val iowait = ticks[TickType.IOWAIT.index] - prevTicks[TickType.IOWAIT.index]
        val idle = ticks[TickType.IDLE.index] - prevTicks[TickType.IDLE.index]
        val totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal
        cpu.cpuNum = processor.logicalProcessorCount
        cpu.setTotal(totalCpu.toDouble())
        cpu.sys = cSys.toDouble()
        cpu.used = user.toDouble()
        cpu.wait = iowait.toDouble()
        cpu.free = idle.toDouble()
    }

    /**
     * 设置内存信息
     */
    private fun setMemInfo(memory: GlobalMemory) {
        mem.setTotal(memory.total)
        mem.setUsed(memory.total - memory.available)
        mem.setFree(memory.available)
    }

    /**
     * 设置服务器信息
     */
    private fun setSysInfo() {
        val props = System.getProperties()
        sys.computerName = IPUtils.hostName
        sys.computerIp = IPUtils.hostIp
        sys.osName = props.getProperty("os.name")
        sys.osArch = props.getProperty("os.arch")
        sys.userDir = props.getProperty("user.dir")
    }

    /**
     * 设置Java虚拟机
     */
    private fun setJvmInfo() {
        val props = System.getProperties()
        jvm.total = Runtime.getRuntime().totalMemory().toDouble()
        jvm.max = Runtime.getRuntime().maxMemory().toDouble()
        jvm.free = Runtime.getRuntime().freeMemory().toDouble()
        jvm.version = props.getProperty("java.version")
        jvm.home = props.getProperty("java.home")
    }

    /**
     * 设置磁盘信息
     */
    private fun setSysFiles(os: OperatingSystem) {
        val fileSystem = os.fileSystem
        val fsArray = fileSystem.fileStores
        for (fs in fsArray) {
            val free = fs.usableSpace
            val total = fs.totalSpace
            val used = total - free
            val sysFile = SysFile()
            sysFile.dirName = fs.mount
            sysFile.sysTypeName = fs.type
            sysFile.typeName = fs.name
            sysFile.total = convertFileSize(total)
            sysFile.free = convertFileSize(free)
            sysFile.used = convertFileSize(used)
            sysFile.usage = Arith.mul(Arith.div(used.toDouble(), total.toDouble(), 4), 100.0)
            sysFiles.add(sysFile)
        }
    }

    /**
     * 字节转换
     *
     * @param size 字节大小
     * @return 转换后值
     */
    fun convertFileSize(size: Long): String {
        val kb: Long = 1024
        val mb = kb * 1024
        val gb = mb * 1024
        return if (size >= gb) {
            String.format("%.1f GB", size.toFloat() / gb)
        } else if (size >= mb) {
            val f = size.toFloat() / mb
            String.format(if (f > 100) "%.0f MB" else "%.1f MB", f)
        } else if (size >= kb) {
            val f = size.toFloat() / kb
            String.format(if (f > 100) "%.0f KB" else "%.1f KB", f)
        } else {
            String.format("%d B", size)
        }
    }

    companion object {
        private const val OSHI_WAIT_SECOND = 1000
    }
}