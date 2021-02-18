package im.zhaojun.common.shiro.filter

import im.zhaojun.common.exception.UnknownRedirectUrlException
import im.zhaojun.common.shiro.OAuth2Helper
import im.zhaojun.common.shiro.token.OAuth2Token
import im.zhaojun.common.util.WebHelper
import org.apache.shiro.authc.AuthenticationToken
import org.apache.shiro.subject.Subject
import org.apache.shiro.web.filter.authc.AuthenticatingFilter
import org.apache.shiro.web.util.WebUtils
import org.springframework.util.StringUtils
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

/**
 * OAuth2 认证过滤器
 */
class OAuth2AuthenticationFilter(private val oAuth2Helper: OAuth2Helper?) : AuthenticatingFilter() {
    // oauth2 authc code 参数名
    private val AUTHC_CODE_PARAM = "code"
    /**
     * 使用 OAuth2 服务提供商返回的 code 构建 token
     */
    override fun createToken(request: ServletRequest, response: ServletResponse): AuthenticationToken {
        val httpRequest = request as HttpServletRequest
        val code = httpRequest.getParameter(AUTHC_CODE_PARAM)

        // 根据回调地址, 判断当前的认证类型是什么
        val requestURI = httpRequest.requestURI
        val authcType = oAuth2Helper!!.getAuthcTypeByRedirectUrl(requestURI)
            ?: throw UnknownRedirectUrlException("未知的回调地址:$requestURI")

        // 创建 token 到 realm 中执行.
        return OAuth2Token(code, authcType)
    }

    /**
     * 是否允许访问, 直接返回为不允许, 因为本页面只是为了进行认证, 并跳转到首页.
     */
    override fun isAccessAllowed(request: ServletRequest, response: ServletResponse, mappedValue: Any): Boolean {
        return false
    }

    /**
     * 当 isAccessAllowed 不允许访问时, 判断 oauth2 服务提供商是否返回了错误信息
     *
     *
     * 如果没有返回错误信息, 则判断
     */
    override fun onAccessDenied(request: ServletRequest, response: ServletResponse): Boolean {
        val error = request.getParameter("error")
        val errorDescription = request.getParameter("error_description")
        if (!StringUtils.isEmpty(error)) { // 如果服务端返回了错误
            WebUtils.issueRedirect(request, response, "/error?error=" + error + "error_description=" + errorDescription)
            return false
        }
        if (StringUtils.isEmpty(request.getParameter(AUTHC_CODE_PARAM))) {
            // 如果用户没有身份验证, 且没有 auth code, 则重定向到登录页面.
            saveRequestAndRedirectToLogin(request, response)
            return false
        }

        // 执行登录操作.
        return executeLogin(request, response)
    }

    // 当登录成功, 跳转到  shiro 配置的 successUrl.
    override fun onLoginSuccess(
        token: AuthenticationToken, subject: Subject, request: ServletRequest,
        response: ServletResponse
    ): Boolean {
        WebHelper.redirectUrl("/oauth2/success")
        return false
    }
}