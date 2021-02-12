package im.zhaojun.system.controller

import im.zhaojun.common.annotation.OperationLog
import im.zhaojun.common.constants.AuthcTypeEnum
import im.zhaojun.common.shiro.OAuth2Helper
import im.zhaojun.common.util.ResultBean
import im.zhaojun.common.util.ShiroUtil
import im.zhaojun.system.model.vo.OAuth2VO
import im.zhaojun.system.service.UserAuthsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("oauth2")
class OAuth2Controller {
    @Autowired
    private lateinit var oAuth2Helper: OAuth2Helper

    @Autowired
    private lateinit var userAuthsService: UserAuthsService

    /**
     * 生成 Github 授权地址
     */
    @OperationLog("Github OAuth2 登录")
    @GetMapping("/render/github")
    @ResponseBody
    fun renderGithubAuth(response: HttpServletResponse?): ResultBean {
        val authRequest = oAuth2Helper.getAuthRequest(AuthcTypeEnum.GITHUB)
        if (authRequest != null) {
            return ResultBean.successData(authRequest.authorize())
        }
        return ResultBean.error("结果为空")
    }

    /**
     * 生成 Gitee 授权地址
     */
    @OperationLog("Gitee OAuth2 登录")
    @GetMapping("/render/gitee")
    @ResponseBody
    fun renderGiteeAuth(response: HttpServletResponse?): ResultBean {
        val authRequest = oAuth2Helper.getAuthRequest(AuthcTypeEnum.GITEE)
        return ResultBean.successData(authRequest!!.authorize())
    }

    @GetMapping("/index")
    fun index(): String {
        return "oauth2/oauth2-list"
    }

    @OperationLog("获取账号绑定信息")
    @GetMapping("/list")
    @ResponseBody
    fun list(): ResultBean {
        val authsList: MutableList<OAuth2VO> = ArrayList()
        for (type in AuthcTypeEnum.values()) {
            val auth = userAuthsService.selectOneByIdentityTypeAndUserId(type, ShiroUtil.currentUser.userId)
            val oAuth2VO = OAuth2VO()
            oAuth2VO.type = type.name
            oAuth2VO.description = type.description
            oAuth2VO.status = if (auth == null) "unbind" else "bind"
            oAuth2VO.username = if (auth == null) "" else auth.identifier
            authsList.add(oAuth2VO)
        }
        return ResultBean.success(authsList)
    }

    /**
     * 取消授权
     */
    @OperationLog("取消账号绑定")
    @GetMapping("/revoke/{provider}")
    @ResponseBody
    fun revokeAuth(@PathVariable("provider") provider: AuthcTypeEnum): Any {
        val userAuths = userAuthsService.selectOneByIdentityTypeAndUserId(provider, ShiroUtil.currentUser.userId)
            ?: return ResultBean.error("已经是未绑定状态!")
        userAuthsService.deleteByPrimaryKey(userAuths.id)
        return ResultBean.success()
    }

    @GetMapping("/success")
    fun success(): String {
        return "oauth2/authorize-success"
    }

    @GetMapping("/error")
    fun error(): String {
        return "oauth2/authorize-error"
    }
}