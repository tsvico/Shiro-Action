package cn.tsvico.system.model

import java.time.LocalDateTime

class LoginLog {
    var id: Int? = null
    var loginTime: LocalDateTime? = null
    var username: String? = null
    var loginStatus: String? = null
    var ip: String? = null
}