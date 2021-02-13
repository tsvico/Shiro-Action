package im.zhaojun.system.service

import com.github.pagehelper.PageHelper
import im.zhaojun.common.util.ShiroUtil
import im.zhaojun.system.mapper.LoginLogMapper
import im.zhaojun.system.model.LoginLog
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

/**
 * 登陆日志 Service
 */
@Service
class LoginLogService {
    @Autowired
    private lateinit var loginLogMapper: LoginLogMapper
    fun addLog(username: String?, isAuthenticated: Boolean, ip: String?) {
        val loginLog = LoginLog()
        loginLog.loginTime = LocalDateTime.now()
        loginLog.username = username
        loginLog.loginStatus = if (isAuthenticated) "1" else "0"
        loginLog.ip = ip
        loginLogMapper.insert(loginLog)
    }

    /**
     * 最近一周登陆次数
     */
    fun recentlyWeekLoginCount(): List<Int> {
        val user = ShiroUtil.currentUser
        return loginLogMapper.recentlyWeekLoginCount(user.username)
    }

    fun selectAll(page: Int, limit: Int): List<LoginLog> {
        PageHelper.startPage<Any>(page, limit)
        return loginLogMapper.selectAll()
    }

    fun count(): Int {
        return loginLogMapper.count()
    }
}