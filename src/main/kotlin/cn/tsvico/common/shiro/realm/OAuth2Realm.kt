package cn.tsvico.common.shiro.realm

import cn.hutool.core.lang.UUID
import cn.tsvico.common.constants.AuthcTypeEnum
import cn.tsvico.common.shiro.OAuth2Helper
import cn.tsvico.common.shiro.realm.OAuth2Realm
import cn.tsvico.common.shiro.token.OAuth2Token
import cn.tsvico.common.util.ShiroUtil
import cn.tsvico.common.util.WebHelper
import cn.tsvico.system.model.User
import cn.tsvico.system.model.UserAuths
import cn.tsvico.system.service.UserAuthsService
import cn.tsvico.system.service.UserService
import me.zhyd.oauth.model.AuthUser
import me.zhyd.oauth.request.ResponseStatus
import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.AuthenticationInfo
import org.apache.shiro.authc.AuthenticationToken
import org.apache.shiro.authc.SimpleAuthenticationInfo
import org.apache.shiro.crypto.hash.Md5Hash
import org.apache.shiro.realm.AuthenticatingRealm
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

/**
 * OAuth2 通用 Realm.
 */
abstract class OAuth2Realm : AuthenticatingRealm() {
    @Autowired
    private val oAuth2Helper: OAuth2Helper? = null

    @Autowired
    private val userService: UserService? = null

    @Autowired
    private val userAuthsService: UserAuthsService? = null

    /**
     * 授权类型, 需子类实现来表示是什么认证类型.
     */
    abstract val authcTypeEnum: AuthcTypeEnum

    /**
     * 调用方法： [.getAuthcTypeEnum] 获取认证类型.
     * 用来判断该 Realm 是否用来处理此认证类型.
     * 并获取该类型对应的 clientId 和 clientSecret 和 redirectUrl.
     */
    override fun supports(token: AuthenticationToken): Boolean {
        if (token is OAuth2Token) {
            val authcTypeEnum = token.authcTypeEnum
            return authcTypeEnum == authcTypeEnum
        }
        return false
    }

    /**
     * 根据 token 获取用户信息.
     */
    @Throws(AuthenticationException::class)
    override fun doGetAuthenticationInfo(token: AuthenticationToken): AuthenticationInfo {
        val oAuth2Token = token as OAuth2Token

        // 获取 code.
        val code = oAuth2Token.authCode

        // 根据 code 去 OAuth2 服务商获取用户信息.
        val oauthUser = extractUserInfo(code)

        // 获取该账号与当前系统的绑定关系.
        var userAuths: UserAuths?
        val subject = SecurityUtils.getSubject()
        val isAuthenticated = subject.isAuthenticated
        userAuths = if (isAuthenticated) {
            userAuthsService!!.selectOneByIdentityTypeAndUserId(authcTypeEnum, ShiroUtil.currentUser.userId)
        } else {
            userAuthsService!!.selectOneByIdentityTypeAndIdentifier(authcTypeEnum, oauthUser.username)
        }

        // 如果未绑定.
        if (userAuths == null) {
            val userId: Int?

            // 如果未登录则创建一个用于与之关联.
            if (!subject.isAuthenticated) {
                // 创建用户  (这里没有处理用户名重复的问题. 请自行根据业务处理.)
                val user = User()
                val initRoleIds = arrayOf(2)
                user.username = oauthUser.username

                // oauth2 登录的用户, 给予一个随机的密码.
                val password = UUID.fastUUID().toString()
                val salt = System.currentTimeMillis().toString()
                val encryptPassword = Md5Hash(password, salt).toString()
                user.password = encryptPassword
                user.salt = salt
                user.email = oauthUser.email
                user.status = "1"
                userService!!.add(user, initRoleIds)
                userId = user.userId
            } else {
                userId = (subject.principal as User).userId
            }
            // 绑定用户关系.
            userAuths = UserAuths()
            userAuths.userId = userId
            userAuths.identifier = oauthUser.username
            userAuths.identityType = authcTypeEnum.description

            // 这里存起来 assessToken 用于后续再次调用服务提供商的接口获取相关信息. 虽然此系统后面没用到该参数.
            userAuths.credential = oauthUser.token.accessToken
            userAuthsService.insert(userAuths)
        }
        val user = userService!!.selectOne(userAuths.userId)
        log.info(user.toString())
        return SimpleAuthenticationInfo(user, code, name)
    }

    // 获取用户信息
    private fun extractUserInfo(code: String?): AuthUser {
        val authRequest = oAuth2Helper!!.getAuthRequest(authcTypeEnum)
        val authResponse = authRequest!!.login(code)

        // 如果认证失败. 则输出日志, 并将用户重定向到错误页面.
        // 这里出错一般原因为程序的 OAuth2 ClientID 或 clientSecret 或 redirectUrl 配置错误.
        if (authResponse.code == ResponseStatus.FAILURE.code) {
            log.error(authResponse.msg)
            WebHelper.redirectUrl("/oauth2/error")
        }
        return authResponse.data as AuthUser
    }

    companion object {
        private val log = LoggerFactory.getLogger(OAuth2Realm::class.java)
    }
}