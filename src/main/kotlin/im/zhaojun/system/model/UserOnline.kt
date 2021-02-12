package im.zhaojun.system.model

import java.io.Serializable
import java.util.*

class UserOnline : Serializable {
    var id: String? = null
    var userId: Int? = null
    var username: String? = null
    var ip: String? = null
    var status: String? = null
    var startTimestamp: Date? = null
    var lastAccessTime: Date? = null
    var timeout: Long? = null

}