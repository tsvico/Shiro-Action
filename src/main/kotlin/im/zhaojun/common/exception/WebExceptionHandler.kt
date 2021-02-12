package im.zhaojun.common.exception

import im.zhaojun.common.util.ResultBean
import org.apache.catalina.connector.ClientAbortException
import org.apache.shiro.authc.ExcessiveAttemptsException
import org.apache.shiro.authc.IncorrectCredentialsException
import org.apache.shiro.authc.LockedAccountException
import org.apache.shiro.authc.UnknownAccountException
import org.apache.shiro.authz.UnauthenticatedException
import org.apache.shiro.authz.UnauthorizedException
import org.apache.shiro.spring.web.ShiroFilterFactoryBean
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.validation.BindException
import org.springframework.web.HttpMediaTypeNotAcceptableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.servlet.NoHandlerFoundException

@ControllerAdvice
class WebExceptionHandler {
    @Autowired
    private val shiroFilterFactoryBean: ShiroFilterFactoryBean? = null
    @ExceptionHandler
    fun authcTypeNotSupport(e: AuthcTypeNotSupportException): String {
        if (log.isDebugEnabled) {
            log.debug(e.message, e)
        }
        return generateErrorInfo(ResultBean.FAIL, e.message, HttpStatus.INTERNAL_SERVER_ERROR.value())
    }

    @ExceptionHandler
    fun unauthenticatedException(e: UnauthenticatedException?): String {
        return "redirect:" + shiroFilterFactoryBean!!.loginUrl
    }

    @ExceptionHandler
    fun unauthorized(e: NoHandlerFoundException?): String {
        if (log.isDebugEnabled) {
            log.debug("请求的地址不存在", e)
        }
        return generateErrorInfo(ResultBean.FAIL, "请求的地址不存在", HttpStatus.NOT_FOUND.value())
    }

    @ExceptionHandler(value = [UnauthorizedException::class])
    fun unauthorized(e: Exception?): String {
        if (log.isDebugEnabled) {
            log.debug("无权限")
        }
        return generateErrorInfo(ResultBean.FAIL, "无权限")
    }

    @ExceptionHandler
    fun unknownAccount(e: UnknownAccountException?): String {
        if (log.isDebugEnabled) {
            log.debug("账号不存在")
        }
        return generateErrorInfo(ResultBean.FAIL, "账号不存在")
    }

    @ExceptionHandler
    fun incorrectCredentials(e: IncorrectCredentialsException?): String {
        if (log.isDebugEnabled) {
            log.debug("密码错误")
        }
        return generateErrorInfo(ResultBean.FAIL, "密码错误")
    }

    @ExceptionHandler
    fun excessiveAttemptsException(e: ExcessiveAttemptsException?): String {
        if (log.isDebugEnabled) {
            log.debug("登录失败次数过多")
        }
        return generateErrorInfo(ResultBean.Companion.FAIL, "登录失败次数过多, 请稍后再试")
    }

    @ExceptionHandler
    fun lockedAccount(e: LockedAccountException?): String {
        if (log.isDebugEnabled) {
            log.debug("账号已锁定")
        }
        return generateErrorInfo(ResultBean.Companion.FAIL, "账号已锁定")
    }

    @ExceptionHandler
    fun lockedAccount(e: CaptchaIncorrectException?): String {
        if (log.isDebugEnabled) {
            log.debug("验证码错误")
        }
        return generateErrorInfo(ResultBean.Companion.FAIL, "验证码错误")
    }

    @ExceptionHandler
    fun lockedAccount(e: DuplicateNameException?): String {
        if (log.isDebugEnabled) {
            log.debug("用户名已存在")
        }
        return generateErrorInfo(ResultBean.Companion.FAIL, "用户名已存在")
    }

    @ExceptionHandler
    fun missingRequestParameter(e: MissingServletRequestParameterException?): String {
        if (log.isDebugEnabled) {
            log.debug("请求参数无效")
        }
        return generateErrorInfo(ResultBean.Companion.FAIL, "请求参数缺失")
    }

    @ExceptionHandler
    fun methodArgumentNotValid(e: BindException): String {
        if (log.isDebugEnabled) {
            log.debug("参数校验失败", e)
        }
        val allErrors = e.bindingResult.allErrors
        val errorMessage = StringBuilder()
        for (i in allErrors.indices) {
            val error = allErrors[i]
            errorMessage.append(error.defaultMessage)
            if (i != allErrors.size - 1) {
                errorMessage.append(",")
            }
        }
        return generateErrorInfo(ResultBean.Companion.FAIL, errorMessage.toString())
    }

    @ExceptionHandler
    fun all(e: Exception): String {
        val msg = if (e.message == null) "系统出现异常" else e.message!!
        log.error(msg, e)
        generateErrorInfo(ResultBean.Companion.FAIL, msg, HttpStatus.INTERNAL_SERVER_ERROR.value())
        return "forward:/error"
    }

    /**
     * 生成错误信息, 放到 request 域中.
     *
     * @param code       错误码
     * @param msg        错误信息
     * @param httpStatus HTTP 状态码
     * @return SpringBoot 默认提供的 /error Controller 处理器
     */
    private fun generateErrorInfo(
        code: Int,
        msg: String?,
        httpStatus: Int = HttpStatus.INTERNAL_SERVER_ERROR.value()
    ): String {
        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
        request.setAttribute("code", code)
        request.setAttribute("msg", msg)
        request.setAttribute("javax.servlet.error.status_code", httpStatus)
        return "forward:/error"
    }

    /**
     * 捕获 ClientAbortException 异常, 不做任何处理, 防止出现大量堆栈日志输出, 此异常不影响功能.
     */
    @ExceptionHandler(HttpMediaTypeNotAcceptableException::class, ClientAbortException::class)
    @ResponseBody
    @ResponseStatus
    fun clientAbortException(ex: Exception?) {
        if (log.isDebugEnabled) {
            log.debug("出现了断开异常:", ex)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(WebExceptionHandler::class.java)
    }
}