package im.zhaojun.common.shiro.filter

import im.zhaojun.common.shiro.filter.RestFormAuthenticationFilter
import im.zhaojun.common.util.IPUtils
import im.zhaojun.common.util.ResultBean
import im.zhaojun.common.util.WebHelper
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter
import org.apache.shiro.web.util.WebUtils
import org.slf4j.LoggerFactory
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

/**
 * 修改后的 authc 过滤器, 添加对 AJAX 请求的支持.
 */
class RestFormAuthenticationFilter : FormAuthenticationFilter() {
    override fun pathsMatch(path: String, request: ServletRequest): Boolean {
        val flag: Boolean
        val requestURI = getPathWithinApplication(request)
        val strings = path.split("==").toTypedArray()
        flag = if (strings.size <= 1) {
            // 普通的 URL, 正常处理
            this.pathsMatch(strings[0], requestURI)
        } else {
            // 获取当前请求的 http method.
            val httpMethod = WebUtils.toHttp(request).method.toUpperCase()
            // 匹配当前请求的 url 和 http method 与过滤器链中的的是否一致
            httpMethod == strings[1].toUpperCase() && this.pathsMatch(strings[0], requestURI)
        }
        if (flag) {
            log.debug("URL : [{}] matching authc filter : [{}]", requestURI, path)
        }
        return flag
    }

    /**
     * 当没有权限被拦截时:
     * 如果是 AJAX 请求, 则返回 JSON 数据.
     * 如果是普通请求, 则跳转到配置 UnauthorizedUrl 页面.
     */
    @Throws(Exception::class)
    override fun onAccessDenied(
        request: ServletRequest,
        response: ServletResponse
    ): Boolean {
        val httpServletRequest = request as HttpServletRequest
        return if (isLoginRequest(request, response)) {
            if (isLoginSubmission(request, response)) {
                if (log.isTraceEnabled) {
                    log.trace("Login submission detected.  Attempting to execute login.")
                }
                executeLogin(request, response)
            } else {
                if (log.isTraceEnabled) {
                    log.trace("Login page view.")
                }
                //allow them to see the login page ;)
                true
            }
        } else {
            if (log.isTraceEnabled) {
                log.trace(
                    "Attempting to access a path which requires authentication.  Forwarding to the " +
                            "Authentication url [" + loginUrl + "]"
                )
            }
            if (WebHelper.isAjaxRequest(WebUtils.toHttp(request))) {
                if (log.isDebugEnabled) {
                    log.debug(
                        "sessionId: [{}], ip: [{}] 请求 restful url : {}, 未登录被拦截.",
                        httpServletRequest.requestedSessionId,
                        IPUtils.ipAddr,
                        getPathWithinApplication(request)
                    )
                }
                WebHelper.writeJson(ResultBean.error("未登录"), response)
            } else {
                saveRequestAndRedirectToLogin(request, response)
            }
            false
        }
    }

    companion object {
        private val log = LoggerFactory
            .getLogger(RestFormAuthenticationFilter::class.java)
    }
}