package im.zhaojun.common.util

import im.zhaojun.system.model.User
import org.apache.shiro.SecurityUtils
import org.apache.shiro.authz.UnauthenticatedException
import org.springframework.stereotype.Component

@Component
object ShiroUtil {
    const val USER_LOCK = "0"

    /**
     * 获取当前登录用户.
     */
    val currentUser: User
        get() = (SecurityUtils.getSubject().principal ?: null) as User?
            ?: throw UnauthenticatedException("未登录被拦截")
}