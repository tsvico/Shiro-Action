package cn.tsvico.common.util

import cn.hutool.json.JSONUtil
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.io.IOException
import java.io.PrintWriter
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

object WebHelper {
    /**
     * 是否是Ajax请求
     */
    fun isAjaxRequest(request: HttpServletRequest): Boolean {
        val requestedWith = request.getHeader("x-requested-with")
        return "XMLHttpRequest".equals(requestedWith, ignoreCase = true)
    }

    /**
     * 输出JSON
     */
    fun writeJson(`object`: Any?, response: ServletResponse) {
        var out: PrintWriter? = null
        try {
            response.characterEncoding = "UTF-8"
            response.contentType = "application/json; charset=utf-8"
            out = response.writer
            out.write(JSONUtil.toJsonStr(`object`))
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            out?.close()
        }
    }

    fun redirectUrl(redirectUrl: String?) {
        val response: HttpServletResponse = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).response!!
        try {
            response.sendRedirect(redirectUrl)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 获取当前请求的 Http Method
     * @return
     */
    val requestHTTPMethod: String
        get() {
            val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
            return request.method
        }
}