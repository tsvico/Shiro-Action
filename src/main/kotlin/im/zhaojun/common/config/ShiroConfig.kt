package im.zhaojun.common.config

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect
import im.zhaojun.common.shiro.EnhanceModularRealmAuthenticator
import im.zhaojun.common.shiro.OAuth2Helper
import im.zhaojun.common.shiro.RestShiroFilterFactoryBean
import im.zhaojun.common.shiro.ShiroActionProperties
import im.zhaojun.common.shiro.credential.RetryLimitHashedCredentialsMatcher
import im.zhaojun.common.shiro.filter.OAuth2AuthenticationFilter
import im.zhaojun.common.shiro.filter.RestAuthorizationFilter
import im.zhaojun.common.shiro.filter.RestFormAuthenticationFilter
import im.zhaojun.common.shiro.realm.OAuth2GiteeRealm
import im.zhaojun.common.shiro.realm.OAuth2GithubRealm
import im.zhaojun.common.shiro.realm.UserNameRealm
import im.zhaojun.system.service.ShiroService
import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.credential.HashedCredentialsMatcher
import org.apache.shiro.authc.pam.ModularRealmAuthenticator
import org.apache.shiro.mgt.SecurityManager
import org.apache.shiro.realm.Realm
import org.apache.shiro.web.mgt.DefaultWebSecurityManager
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager
import org.crazycake.shiro.RedisCacheManager
import org.crazycake.shiro.RedisManager
import org.crazycake.shiro.RedisSessionDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import java.util.*

@Configuration
class ShiroConfig {
    @Autowired
    private val oAuth2Helper: OAuth2Helper? = null

    @Lazy
    @Autowired
    private lateinit var shiroService: ShiroService

    @Autowired
    private lateinit var shiroActionProperties: ShiroActionProperties

    @Value("\${spring.redis.host}")
    private val redisHost: String? = null

    @Value("\${spring.redis.port}")
    private val redisPort: Int? = null

    @Bean
    fun restShiroFilterFactoryBean(securityManager: SecurityManager?): RestShiroFilterFactoryBean {
        val shiroFilterFactoryBean = RestShiroFilterFactoryBean()
        shiroFilterFactoryBean.securityManager = securityManager
        shiroFilterFactoryBean.loginUrl = "/login"
        shiroFilterFactoryBean.unauthorizedUrl = "/403"
        val filters = shiroFilterFactoryBean.filters
        filters["authc"] = RestFormAuthenticationFilter()
        filters["perms"] = RestAuthorizationFilter()
        filters["oauth2Authc"] = OAuth2AuthenticationFilter(oAuth2Helper)
        val urlPermsMap = shiroService.urlPermsMap
        shiroFilterFactoryBean.filterChainDefinitionMap = urlPermsMap
        return shiroFilterFactoryBean
    }

    /**
     * 注入 securityManager
     */
    @Bean
    fun securityManager(): SecurityManager {
        val securityManager = DefaultWebSecurityManager()
        securityManager.sessionManager = sessionManager()
        securityManager.realms = listOf(userNameRealm(), oAuth2GithubRealm(), oAuth2GiteeRealm())
        val authenticator: ModularRealmAuthenticator = EnhanceModularRealmAuthenticator()
        securityManager.authenticator = authenticator
        authenticator.setRealms(listOf(userNameRealm(), oAuth2GithubRealm(), oAuth2GiteeRealm()))
        SecurityUtils.setSecurityManager(securityManager)
        return securityManager
    }

    /**
     * Github 登录 Realm
     */
    @Bean
    fun oAuth2GithubRealm(): OAuth2GithubRealm {
        return OAuth2GithubRealm()
    }

    /**
     * Gitee 登录 Realm
     */
    @Bean
    fun oAuth2GiteeRealm(): OAuth2GiteeRealm {
        return OAuth2GiteeRealm()
    }

    /**
     * 用户名密码登录 Realm
     */
    @Bean
    fun userNameRealm(): UserNameRealm {
        val userNameRealm = UserNameRealm()
        userNameRealm.credentialsMatcher = hashedCredentialsMatcher()
        userNameRealm.cacheManager = redisCacheManager()
        return userNameRealm
    }

    /**
     * 用户名密码登录密码匹配器
     */
    @Bean
    fun hashedCredentialsMatcher(): HashedCredentialsMatcher {
        // 指定加密算法
        return RetryLimitHashedCredentialsMatcher("md5")
    }

    @Bean
    fun shiroDialect(): ShiroDialect {
        return ShiroDialect()
    }

    @Bean
    fun redisCacheManager(): RedisCacheManager {
        val redisCacheManager = RedisCacheManager()
        redisCacheManager.redisManager = redisManager()
        redisCacheManager.expire =
            if (shiroActionProperties.permsCacheTimeout == null) 3600 else shiroActionProperties.permsCacheTimeout!!
        redisCacheManager.principalIdFieldName = "userId"
        return redisCacheManager
    }

    @Bean
    fun redisManager(): RedisManager {
        val redisManager = RedisManager()
        redisManager.host = "$redisHost:$redisPort"
        redisManager.timeout = 60 * 1000
        return redisManager
    }

    @Bean
    fun redisSessionDAO(): RedisSessionDAO {
        val redisSessionDAO = RedisSessionDAO()
        redisSessionDAO.expire =
            if (shiroActionProperties.sessionTimeout == null) 1800 else shiroActionProperties.sessionTimeout!!
        redisSessionDAO.redisManager = redisManager()
        redisSessionDAO.sessionInMemoryEnabled = false
        return redisSessionDAO
    }

    @Bean
    fun sessionManager(): DefaultWebSessionManager {
        val sessionManager = DefaultWebSessionManager()
        // 设置sessionDAO
        sessionManager.sessionDAO = redisSessionDAO()
        sessionManager.isSessionIdUrlRewritingEnabled = false
        // 删除无效session
        sessionManager.isDeleteInvalidSessions = true
        return sessionManager
    }
}