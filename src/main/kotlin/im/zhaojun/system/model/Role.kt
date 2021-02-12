package im.zhaojun.system.model

import java.io.Serializable
import java.util.*

class Role : Serializable {
    var roleId: Int? = null
    var roleName: String? = null
    var remark: String? = null
    var createTime: Date? = null
    var modifyTime: Date? = null
}