package im.zhaojun.common.shiro.filter

import im.zhaojun.common.shiro.filter.RestAuthorizationFilter
import im.zhaojun.common.util.IPUtils
import im.zhaojun.common.util.ResultBean
import im.zhaojun.common.util.WebHelper
import org.apache.shiro.util.StringUtils
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter
import org.apache.shiro.web.util.WebUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 修改后的 perms 过滤器, 添加对 AJAX 请求的支持.
 */
class RestAuthorizationFilter : PermissionsAuthorizationFilter() {
    val log: Logger = LoggerFactory
        .getLogger(RestAuthorizationFilter::class.java)

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

            // 匹配当前请求的 http method 与 过滤器链中的的是否一致
            httpMethod == strings[1].toUpperCase() && this.pathsMatch(strings[0], requestURI)
        }
        if (flag) {
            log.debug("URL : [{}] matching perms filter : [{}]", requestURI, path)
        }
        return flag
    }

    /**
     * 当没有权限被拦截时:
     * 如果是 AJAX 请求, 则返回 JSON 数据.
     * 如果是普通请求, 则跳转到配置 UnauthorizedUrl 页面.
     */
    @Throws(IOException::class)
    override fun onAccessDenied(request: ServletRequest, response: ServletResponse): Boolean {
        val subject = getSubject(request, response)
        val httpServletRequest = request as HttpServletRequest
        // 如果未登录
        if (subject.principal == null) {
            // AJAX 请求返回 JSON
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
                // 其他请求跳转到登陆页面
                saveRequestAndRedirectToLogin(request, response)
            }
        } else {
            // 如果已登陆, 但没有权限
            // 对于 AJAX 请求返回 JSON
            if (WebHelper.isAjaxRequest(WebUtils.toHttp(request))) {
                if (log.isDebugEnabled) {
                    log.debug(
                        "用户: [{}] 请求 restful url : {}, 无权限被拦截.",
                        subject.principal,
                        getPathWithinApplication(request)
                    )
                }
                WebHelper.writeJson(ResultBean.error("无权限"), response)
            } else {
                // 对于普通请求, 跳转到配置的 UnauthorizedUrl 页面.
                // 如果未设置 UnauthorizedUrl, 则返回 401 状态码
                val unauthorizedUrl = unauthorizedUrl
                if (StringUtils.hasText(unauthorizedUrl)) {
                    WebUtils.issueRedirect(request, response, unauthorizedUrl)
                } else {
                    WebUtils.toHttp(response).sendError(HttpServletResponse.SC_UNAUTHORIZED)
                }
            }
        }
        return false
    }
}