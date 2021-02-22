package cn.tsvico.common.shiro.realm

import cn.tsvico.common.constants.AuthcTypeEnum
import org.springframework.stereotype.Component

/**
 * Github OAuth2 Realm
 */
// @Component
class OAuth2GithubRealm : OAuth2Realm() {
    override val authcTypeEnum: AuthcTypeEnum
        get() = AuthcTypeEnum.GITHUB
}