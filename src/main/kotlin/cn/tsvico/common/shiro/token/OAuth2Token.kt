package cn.tsvico.common.shiro.token
import cn.tsvico.common.constants.AuthcTypeEnum
import org.apache.shiro.authc.AuthenticationToken

class OAuth2Token(var authCode: String, var authcTypeEnum: AuthcTypeEnum) : AuthenticationToken {
    private var principal: String? = null
    override fun getPrincipal(): String {
        return principal!!
    }

    fun setPrincipal(principal: String?) {
        this.principal = principal
    }

    override fun getCredentials(): Any {
        return authCode
    }
}