package im.zhaojun.common.aop

import im.zhaojun.common.util.IPUtils
import im.zhaojun.system.model.User
import im.zhaojun.system.service.LoginLogService
import org.apache.shiro.SecurityUtils
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Aspect
@Component
@ConditionalOnProperty(value = ["shiro-action.log.login"], havingValue = "true")
class LoginLogAspect {
    @Autowired
    private val loginLogService: LoginLogService? = null
    @Pointcut("execution(im.zhaojun.common.util.ResultBean im.zhaojun.system.controller..LoginController.login(im.zhaojun.system.model.User, String) )")
    fun loginLogPointCut() {
    }

    @After("loginLogPointCut()")
    fun recordLoginLog(joinPoint: JoinPoint) {
        // 获取登陆参数
        val args = joinPoint.args
        val user = args[0] as User
        val subject = SecurityUtils.getSubject()
        val ip = IPUtils.ipAddr
        loginLogService!!.addLog(user.username, subject.isAuthenticated, ip)
    }
}