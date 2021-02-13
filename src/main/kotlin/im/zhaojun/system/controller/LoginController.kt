package im.zhaojun.system.controller

import cn.hutool.core.util.IdUtil
import im.zhaojun.common.annotation.OperationLog
import im.zhaojun.common.exception.CaptchaIncorrectException
import im.zhaojun.common.shiro.ShiroActionProperties
import im.zhaojun.common.util.CaptchaUtil
import im.zhaojun.common.util.ResultBean
import im.zhaojun.system.model.User
import im.zhaojun.system.service.MailService
import im.zhaojun.system.service.UserService
import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.UsernamePasswordToken
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.io.IOException
import javax.servlet.http.HttpServletResponse

@Controller
class LoginController {
    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var mailService: MailService

    @Autowired
    private lateinit var templateEngine: TemplateEngine

    @Autowired
    private lateinit var shiroActionProperties: ShiroActionProperties

    @Autowired(required = false)
    private lateinit var captcha: CaptchaUtil

    @GetMapping("/login")
    fun loginPage(model: Model): String {
        model.addAttribute("loginVerify", shiroActionProperties.loginVerify)
        return "login"
    }

    @GetMapping("/register")
    fun register(): String {
        return "register"
    }

    @PostMapping("/login")
    @ResponseBody
    fun login(user: User, @RequestParam(value = "captcha", required = false) captcha: String): ResultBean {
        val subject = SecurityUtils.getSubject()

        // 如果开启了登录校验
        if (shiroActionProperties.loginVerify) {
            val realCaptcha = SecurityUtils.getSubject().session.getAttribute("captcha")?: null
            // session 中的验证码过期了
            if (realCaptcha == null || realCaptcha != captcha.toLowerCase()) {
                throw CaptchaIncorrectException()
            }
        }
        val token = UsernamePasswordToken(user.username, user.password)
        subject.login(token)
        userService.updateLastLoginTimeByUsername(user.username)
        return ResultBean.success("登录成功")
    }

    @OperationLog("注销")
    @GetMapping("/logout")
    fun logout(): String {
        SecurityUtils.getSubject().logout()
        return "redirect:login"
    }

    @PostMapping("/register")
    @ResponseBody
    fun register(user: User): ResultBean {
        userService.checkUserNameExistOnCreate(user.username)
        val activeCode = IdUtil.fastSimpleUUID()
        user.activeCode = activeCode
        user.status = "0"
        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
        val url = (request.scheme + "://"
                + request.serverName
                + ":"
                + request.serverPort
                + "/active/"
                + activeCode)
        val context = Context()
        context.setVariable("url", url)
        val mailContent = templateEngine.process("mail/registerTemplate", context)
        Thread { user.email?.let { mailService.sendHTMLMail(it, "Shiro-Action 激活邮件", mailContent) } }
            .start()

        // 注册后默认的角色, 根据自己数据库的角色表 ID 设置
        val initRoleIds = arrayOf(2)
        return ResultBean.success(userService.add(user, initRoleIds))
    }

    @GetMapping("/captcha")
    @Throws(IOException::class)
    fun captcha(response: HttpServletResponse) {
        // 设置请求头为输出图片类型
        response.contentType = "image/gif";
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        // 获取运算的结果
        val captcha = captcha.getCaptcha()
        val session = SecurityUtils.getSubject().session
        session.setAttribute("captcha", captcha!!.text())
        captcha.out(response.outputStream)
    }

    @OperationLog("激活注册账号")
    @GetMapping("/active/{token}")
    fun active(@PathVariable("token") token: String?, model: Model): String {
        val user = userService.selectByActiveCode(token)
        val msg: String
        if (user == null) {
            msg = "请求异常, 激活地址不存在!"
        } else if ("1" == user.status) {
            msg = "用户已激活, 请勿重复激活!"
        } else {
            msg = "激活成功!"
            user.status = "1"
            userService.activeUserByUserId(user.userId)
        }
        model.addAttribute("msg", msg)
        return "active"
    }
}