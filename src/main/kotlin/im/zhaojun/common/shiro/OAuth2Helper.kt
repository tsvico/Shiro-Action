package im.zhaojun.common.shiro

import im.zhaojun.common.constants.AuthcTypeEnum
import im.zhaojun.common.exception.AuthcTypeNotSupportException
import me.zhyd.oauth.config.AuthConfig
import me.zhyd.oauth.request.AuthGiteeRequest
import me.zhyd.oauth.request.AuthGithubRequest
import me.zhyd.oauth.request.AuthRequest
import me.zhyd.oauth.request.BaseAuthRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * OAuth2 工具类
 */
@Component
class OAuth2Helper {
    @Autowired
    private lateinit var properties: ShiroActionProperties

    /**
     * 获取所有 OAuth2 配置对象
     */
    private val allProvider: Map<AuthcTypeEnum, ShiroActionProperties.Provider>
        get() = properties.oauth2Provider

    /**
     * 根据类型获取单个 OAuth2 配置对象.
     */
    fun getProvider(identifyType: AuthcTypeEnum?): ShiroActionProperties.Provider? {
        return allProvider[identifyType]
    }

    /**
     * 根据类型获取 AuthRequest 对象.
     */
    fun getAuthRequest(authcTypeEnum: AuthcTypeEnum): AuthRequest? {
        val provider = getProvider(authcTypeEnum)
            ?: throw AuthcTypeNotSupportException("系统暂未开启 " + authcTypeEnum.description + " 登录")
        val authRequest: BaseAuthRequest?
        val authConfig = AuthConfig.builder()
            .clientId(provider.clientId)
            .clientSecret(provider.clientSecret)
            .redirectUri(provider.redirectUrl)
            .build()
        authRequest = when (authcTypeEnum) {
            AuthcTypeEnum.GITHUB -> AuthGithubRequest(authConfig)
            AuthcTypeEnum.GITEE -> AuthGiteeRequest(authConfig)
        }
        return authRequest
    }

    /**
     * 根据回调地址获取当前认证的类型.
     * @param requestURI    回调地址. (尾匹配法)
     */
    fun getAuthcTypeByRedirectUrl(requestURI: String?): AuthcTypeEnum? {
        for (key in allProvider.keys) {
            val redirectUrl = getProvider(key)!!.redirectUrl
            if (redirectUrl!!.endsWith(requestURI!!)) {
                return key
            }
        }
        return null
    }
}