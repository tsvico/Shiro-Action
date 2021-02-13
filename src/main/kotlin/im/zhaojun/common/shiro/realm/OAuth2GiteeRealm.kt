package im.zhaojun.common.shiro.realm

import im.zhaojun.common.constants.AuthcTypeEnum
import org.springframework.stereotype.Component

/**
 * Gitee OAuth2 Realm
 */
// @Component
class OAuth2GiteeRealm : OAuth2Realm() {
    override val authcTypeEnum: AuthcTypeEnum
        get() = AuthcTypeEnum.GITEE
}