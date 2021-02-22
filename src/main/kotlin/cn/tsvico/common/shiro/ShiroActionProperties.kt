package cn.tsvico.common.shiro

import cn.tsvico.common.constants.AuthcTypeEnum
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.util.*
import javax.annotation.PostConstruct

@Component
@ConfigurationProperties(prefix = "shiro-action")
class ShiroActionProperties {
    var superAdminUsername: String? = null
    var retryCount: Int? = null
    var loginVerify: Boolean = false
    var retryTimeout: Int? = null
    var sessionTimeout: Int? = null
    var permsCacheTimeout: Int? = null
    var oauth2Provider: Map<AuthcTypeEnum, Provider> = HashMap()
    @PostConstruct
    fun validate() {
        val set: MutableSet<String?> = HashSet()
        for (provider in oauth2Provider.values) {
            // ClientId 不能为空
            check(StringUtils.hasText(provider.clientId)) { "Client id must not be empty." }

            // ClientSecret 不能为空
            check(StringUtils.hasText(provider.clientSecret)) { "Client secret must not be empty." }

            // 回调地址不能重复.
            check(set.add(provider.redirectUrl)) { "redirectUrl must not be duplicate." }
        }
    }

    class Provider {
        var clientId: String? = null
        var redirectUrl: String? = null
        var clientSecret: String? = null
    }
}