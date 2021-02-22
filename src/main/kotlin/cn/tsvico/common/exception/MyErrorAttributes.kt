package cn.tsvico.common.exception

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.WebRequest
import java.util.*

@Component
class MyErrorAttributes : DefaultErrorAttributes() {
    override fun getErrorAttributes(webRequest: WebRequest, includeStackTrace: Boolean): Map<String, Any> {
        val map: MutableMap<String, Any> = HashMap()
        val code = webRequest.getAttribute("code", RequestAttributes.SCOPE_REQUEST)
        val message = webRequest.getAttribute("msg", RequestAttributes.SCOPE_REQUEST)
        map["code"] = code ?: 0
        map["msg"] = message ?: ""
        return map
    }
}