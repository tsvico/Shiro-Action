package cn.tsvico.common.information

/**
 * 系统文件相关信息
 */
class SysFile {
    /**
     * 盘符路径
     */
    var dirName: String? = null

    /**
     * 盘符类型
     */
    var sysTypeName: String? = null

    /**
     * 文件类型
     */
    var typeName: String? = null

    /**
     * 总大小
     */
    var total: String? = null

    /**
     * 剩余大小
     */
    var free: String? = null

    /**
     * 已经使用量
     */
    var used: String? = null

    /**
     * 资源的使用率
     */
    var usage = 0.0
}