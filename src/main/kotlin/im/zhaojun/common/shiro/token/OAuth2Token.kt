package im.zhaojun.common.shiro.token
import im.zhaojun.common.constants.AuthcTypeEnum
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