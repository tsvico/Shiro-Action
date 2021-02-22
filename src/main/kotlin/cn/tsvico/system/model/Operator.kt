package cn.tsvico.system.model

import java.util.*

class Operator {
    /**
     * 菜单 ID
     */
    var operatorId: Int? = null

    /**
     * 所属菜单 ID
     */
    var menuId: Int? = null

    /**
     * 资源名称
     */
    var operatorName: String? = null

    /**
     * 资源 URL
     */
    var url: String? = null

    /**
     * 权限标识符
     */
    var perms: String? = null

    /**
     * 资源需要的 HTTP 请求方法
     */
    var httpMethod: String? = null

    /**
     * 创建时间
     */
    var createTime: Date? = null

    /**
     * 修改时间
     */
    var modifyTime: Date? = null
}