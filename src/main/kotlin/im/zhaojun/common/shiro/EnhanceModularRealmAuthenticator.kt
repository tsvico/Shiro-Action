package im.zhaojun.common.shiro

import org.apache.shiro.authc.AuthenticationInfo
import org.apache.shiro.authc.AuthenticationToken
import org.apache.shiro.authc.pam.ModularRealmAuthenticator
import org.apache.shiro.realm.Realm
import org.slf4j.LoggerFactory

/**
 * 在 Shiro 使用多 Realm 时, 对于 Realm 中抛出的异常, 他都会进行捕获, 然后输出日志.
 * 但我们系统有统一异常处理, 所以不需要他捕获我们的自定义异常, 这里将异常抛出.
 */
class EnhanceModularRealmAuthenticator : ModularRealmAuthenticator() {
    private val log = LoggerFactory.getLogger(EnhanceModularRealmAuthenticator::class.java)
    /**
     * 抛出 realm 中第一个遇到的异常
     */
    override fun doMultiRealmAuthentication(realms: Collection<Realm>, token: AuthenticationToken): AuthenticationInfo {
        val strategy = authenticationStrategy
        var aggregate = strategy.beforeAllAttempts(realms, token)
        if (log.isTraceEnabled) {
            log.trace("Iterating through {} realms for PAM authentication", realms.size)
        }
        for (realm in realms) {
            aggregate = strategy.beforeAttempt(realm, token, aggregate)
            if (realm.supports(token)) {
                log.trace("Attempting to authenticate token [{}] using realm [{}]", token, realm)
                // 有异常从此处抛出
                val info: AuthenticationInfo? = realm.getAuthenticationInfo(token)
                aggregate = strategy.afterAttempt(realm, token, info, aggregate, null)
            } else {
                log.debug("Realm [{}] does not support token {}.  Skipping realm.", realm, token)
            }
        }
        aggregate = strategy.afterAllAttempts(token, aggregate)
        return aggregate
    }

}