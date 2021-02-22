package cn.tsvico.system.service

import cn.tsvico.common.util.IPUtils
import cn.tsvico.system.model.User
import cn.tsvico.system.model.UserOnline
import org.apache.shiro.session.mgt.eis.SessionDAO
import org.apache.shiro.subject.SimplePrincipalCollection
import org.apache.shiro.subject.support.DefaultSubjectContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserOnlineService {
    @Autowired
    private lateinit var sessionDAO: SessionDAO
    fun list(): List<UserOnline> {
        val list: MutableList<UserOnline> = ArrayList()
        val sessions = sessionDAO.activeSessions
        for (session in sessions) {
            val userOnline = UserOnline()
            if (session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY) == null) {
                continue
            } else {
                val principalCollection = session
                    .getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY) as SimplePrincipalCollection
                val user = principalCollection.primaryPrincipal as User
                userOnline.username = user.username
                userOnline.userId = user.userId
            }
            userOnline.id = session.id as String
            userOnline.ip = IPUtils.ipAddr
            userOnline.startTimestamp = session.startTimestamp
            userOnline.lastAccessTime = session.lastAccessTime
            val timeout = session.timeout
            if (timeout == 0L) {
                userOnline.status = "离线"
            } else {
                userOnline.status = "在线"
            }
            userOnline.timeout = timeout
            list.add(userOnline)
        }
        return list
    }

    fun forceLogout(sessionId: String?) {
        val session = sessionDAO.readSession(sessionId)
        if (session != null) {
            session.timeout = 0
            session.stop()
            sessionDAO.delete(session)
        }
    }

    fun count(): Int {
        var count = 0
        val sessions = sessionDAO.activeSessions
        for (session in sessions) {
            if (session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY) != null) {
                count++
            }
        }
        return count
    }
}