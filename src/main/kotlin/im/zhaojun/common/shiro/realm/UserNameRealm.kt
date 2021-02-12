package im.zhaojun.common.shiro.realm

import im.zhaojun.common.shiro.ShiroActionProperties
import im.zhaojun.common.shiro.realm.UserNameRealm
import im.zhaojun.common.util.ShiroUtil
import im.zhaojun.system.model.User
import im.zhaojun.system.service.UserService
import org.apache.shiro.authc.*
import org.apache.shiro.authz.AuthorizationInfo
import org.apache.shiro.authz.SimpleAuthorizationInfo
import org.apache.shiro.realm.AuthorizingRealm
import org.apache.shiro.session.mgt.eis.SessionDAO
import org.apache.shiro.subject.PrincipalCollection
import org.apache.shiro.subject.SimplePrincipalCollection
import org.apache.shiro.subject.support.DefaultSubjectContext
import org.apache.shiro.util.ByteSource
import org.slf4j.LoggerFactory
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired

/**
 * 根据用户名密码校验的 Realm.
 */
// @Component
class UserNameRealm : AuthorizingRealm() {
    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var sessionDAO: SessionDAO

    @Autowired
    private lateinit var shiroActionProperties: ShiroActionProperties
    override fun supports(token: AuthenticationToken): Boolean {
        return token is UsernamePasswordToken
    }

    override fun doGetAuthorizationInfo(principals: PrincipalCollection): AuthorizationInfo {
        log.info("从数据库获取权限信息")
        val user = principals.primaryPrincipal as User
        val username = user.username
        val roles = userService.selectRoleNameByUserName(username)
        val perms = userService.selectPermsByUsername(username)
        val authorizationInfo = SimpleAuthorizationInfo()
        authorizationInfo.roles = roles
        authorizationInfo.stringPermissions = perms
        return authorizationInfo
    }

    @Throws(AuthenticationException::class)
    override fun doGetAuthenticationInfo(token: AuthenticationToken): AuthenticationInfo {
        log.info("从数据库获取认证信息")
        val username = token.principal as String
        val user = userService.selectOneByUserName(username) ?: throw UnknownAccountException()
        // 如果账号被锁定, 则抛出异常, (超级管理员除外)
        if (ShiroUtil.USER_LOCK == user.status && shiroActionProperties.superAdminUsername != username) {
            throw LockedAccountException()
        }
        return SimpleAuthenticationInfo(user, user.password, ByteSource.Util.bytes(user.salt), super.getName())
    }

    fun clearAuthCacheByUserId(userId: Int) {
        // 获取所有 session
        val sessions = sessionDAO.activeSessions
        for (session in sessions) {
            // 获取 session 登录信息。
            val obj = session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY)
            if (obj is SimplePrincipalCollection) {
                // 强转
                val spc = obj
                val user = User()
                BeanUtils.copyProperties(spc.primaryPrincipal, user)
                // 判断用户, 匹配用户ID.
                if (userId == user.userId) {
                    doClearCache(spc)
                }
            }
        }
    }

    fun clearAllAuthCache() {
        // 获取所有 session
        val sessions = sessionDAO.activeSessions
        for (session in sessions) {
            // 获取 session 登录信息。
            val obj = session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY)
            if (obj is SimplePrincipalCollection) {
                // 强转
                val spc = obj
                val user = User()
                BeanUtils.copyProperties(spc.primaryPrincipal, user)
                doClearCache(spc)
            }
        }
    }

    /**
     * 超级管理员拥有所有权限
     */
    override fun isPermitted(principals: PrincipalCollection, permission: String): Boolean {
        val user = principals.primaryPrincipal as User
        return shiroActionProperties.superAdminUsername == user.username || super.isPermitted(
            principals,
            permission
        )
    }

    /**
     * 超级管理员拥有所有角色
     */
    override fun hasRole(principals: PrincipalCollection, roleIdentifier: String): Boolean {
        val user = principals.primaryPrincipal as User
        return shiroActionProperties.superAdminUsername == user.username || super.hasRole(
            principals,
            roleIdentifier
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserNameRealm::class.java)
    }
}