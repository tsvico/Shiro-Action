package im.zhaojun.common.interceptor

import cn.hutool.json.JSONUtil
import im.zhaojun.common.interceptor.RequestLogHandlerInterceptor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class RequestLogHandlerInterceptor : HandlerInterceptor {
    private val log = LoggerFactory.getLogger(RequestLogHandlerInterceptor::class.java)
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (log.isDebugEnabled) {
            log.debug("请求 URL:" + request.requestURI)
            log.debug("请求参数:" + JSONUtil.toJsonStr(request.parameterMap))
        }
        return true
    }
}