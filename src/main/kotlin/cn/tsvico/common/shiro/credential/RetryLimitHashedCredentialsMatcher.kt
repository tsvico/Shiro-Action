package cn.tsvico.common.shiro.credential

import cn.tsvico.common.shiro.ShiroActionProperties
import cn.tsvico.common.util.IPUtils
import org.apache.shiro.authc.*
import org.apache.shiro.authc.credential.HashedCredentialsMatcher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import javax.annotation.Resource

/**
 * MD5 密码匹配器
 *
 * 密码校验失败后计数, 当超出 ${shiro-action.retry-count} 次后, 禁止登录 ${shiro-action.retry-timeout} 秒.
 */
class RetryLimitHashedCredentialsMatcher(hashAlgorithmName: String?) : HashedCredentialsMatcher(hashAlgorithmName) {
    @Resource
    private lateinit var redisTemplate: RedisTemplate<String, AtomicInteger>

    @Autowired
    private lateinit var shiroActionProperties: ShiroActionProperties
    override fun doCredentialsMatch(
        token: AuthenticationToken,
        info: AuthenticationInfo
    ): Boolean {
        val opsForValue = redisTemplate.opsForValue()
        val username = token.principal as String
        val key = username + IPUtils.ipAddr

        // 超级管理员不进行登录次数校验.
        if (shiroActionProperties.superAdminUsername != key) {
            var retryCount = opsForValue[key]
            if (retryCount == null) {
                retryCount = AtomicInteger(0)
            }
            if (retryCount.incrementAndGet() > shiroActionProperties.retryCount ?: 0) {
                throw ExcessiveAttemptsException()
            }
            val retryTimeout =
                if (shiroActionProperties.retryTimeout == null) 300 else shiroActionProperties.retryTimeout
            opsForValue[key, retryCount, retryTimeout!!.toLong()] = TimeUnit.SECONDS
        }
        val matches = super.doCredentialsMatch(token, info)
        if (matches) {
            redisTemplate.delete(key)
        }
        return matches
    }
}