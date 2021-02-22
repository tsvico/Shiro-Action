package cn.tsvico.common.information

/**
 * 內存相关信息
 */
class Mem {
    /**
     * 内存总量
     */
    private var total = 0.0

    /**
     * 已用内存
     */
    private var used = 0.0

    /**
     * 剩余内存
     */
    private var free = 0.0
    fun getTotal(): Double {
        return Arith.div(total, (1024 * 1024 * 1024).toDouble(), 2)
    }

    fun setTotal(total: Long) {
        this.total = total.toDouble()
    }

    fun getUsed(): Double {
        return Arith.div(used, (1024 * 1024 * 1024).toDouble(), 2)
    }

    fun setUsed(used: Long) {
        this.used = used.toDouble()
    }

    fun getFree(): Double {
        return Arith.div(free, (1024 * 1024 * 1024).toDouble(), 2)
    }

    fun setFree(free: Long) {
        this.free = free.toDouble()
    }

    val usage: Double
        get() = Arith.mul(Arith.div(used, total, 4), 100.0)
}