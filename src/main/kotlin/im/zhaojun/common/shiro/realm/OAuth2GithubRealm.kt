package im.zhaojun.common.shiro.realm

import im.zhaojun.common.constants.AuthcTypeEnum
import org.springframework.stereotype.Component

/**
 * Github OAuth2 Realm
 */
// @Component
class OAuth2GithubRealm : OAuth2Realm() {
    override val authcTypeEnum: AuthcTypeEnum
        get() = AuthcTypeEnum.GITHUB
}