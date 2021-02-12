package im.zhaojun.common.information

/**
 * CPU相关信息
 *
 */
class Cpu {
    /**
     * 核心数
     */
    var cpuNum = 0

    /**
     * CPU总的使用率
     */
    var _total = 0.0

    /**
     * CPU系统使用率
     */
    var sys = 0.0
        get() = Arith.round(Arith.mul(field / _total, 100.0), 2)

    /**
     * CPU用户使用率
     */
    var used = 0.0
        get() = Arith.round(Arith.mul(field / _total, 100.0), 2)

    /**
     * CPU当前等待率
     */
    var wait = 0.0
        get() = Arith.round(Arith.mul(field / _total, 100.0), 2)

    /**
     * CPU当前空闲率
     */
    var free = 0.0
        get() = Arith.round(Arith.mul(field / _total, 100.0), 2)

    fun getTotal(): Double {
        return Arith.round(Arith.mul(_total, 100.0), 2)
    }

    fun setTotal(total: Double) {
        this._total = total
    }
}