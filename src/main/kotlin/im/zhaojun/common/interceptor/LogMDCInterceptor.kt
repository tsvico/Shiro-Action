package im.zhaojun.common.interceptor

import im.zhaojun.common.util.IPUtils
import im.zhaojun.system.model.User
import org.apache.shiro.SecurityUtils
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * MDC 拦截器, 用于将当前操作人的用户名及 IP 添加到 MDC 中. 以在日志中进行显示.
 */
@Component
class LogMDCInterceptor : HandlerInterceptor {
    private val MDC_USERNAME = "username"
    private val IP = "ip"

    override fun preHandle(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        o: Any
    ): Boolean {
        // 如已进行登录, 则获取当前登录者的用户名放入 MDC 中.
        var username = ""
        val user = (SecurityUtils.getSubject().principal as User?)
        if (user != null) {
            username = user.username!!
        }
        MDC.put(IP, IPUtils.ipAddr)
        MDC.put(MDC_USERNAME, username)
        return true
    }

    override fun postHandle(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        o: Any,
        modelAndView: ModelAndView?
    ) {
        val username = MDC.get(MDC_USERNAME)
        MDC.remove(username)
    }
}