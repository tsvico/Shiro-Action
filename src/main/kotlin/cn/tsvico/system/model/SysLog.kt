package cn.tsvico.system.model

import java.util.*

class SysLog {
    var id: Int? = null

    /**
     * 用户名
     */
    var username: String? = null

    /**
     * 操作
     */
    var operation: String? = null

    /**
     * 响应时间/耗时
     */
    var time: Int? = null

    /**
     * 请求方法
     */
    var method: String? = null

    /**
     * 请求参数
     */
    var params: String? = null

    /**
     * IP
     */
    var ip: String? = null

    /**
     * 创建时间
     */
    var createTime: Date? = null
}