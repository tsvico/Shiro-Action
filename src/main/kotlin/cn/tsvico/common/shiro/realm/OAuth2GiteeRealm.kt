package cn.tsvico.common.shiro.realm

import cn.tsvico.common.constants.AuthcTypeEnum
import org.springframework.stereotype.Component

/**
 * Gitee OAuth2 Realm
 */
// @Component
class OAuth2GiteeRealm : OAuth2Realm() {
    override val authcTypeEnum: AuthcTypeEnum
        get() = AuthcTypeEnum.GITEE
}