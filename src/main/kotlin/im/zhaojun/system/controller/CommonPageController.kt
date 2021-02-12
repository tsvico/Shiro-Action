package im.zhaojun.system.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RequestParam
import im.zhaojun.common.util.ResultBean
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import org.springframework.web.method.HandlerMethod
import im.zhaojun.system.model.vo.UrlVO
import org.springframework.beans.factory.annotation.Autowired
import java.util.HashSet
import org.springframework.web.bind.annotation.RestController
import org.springframework.stereotype.Controller
import org.springframework.util.StringUtils

@Controller
class CommonPageController {
    @Autowired
    private lateinit var applicationContext: WebApplicationContext
    @GetMapping("/403")
    fun forbidden(): String {
        return "error/403"
    }

    @GetMapping("/404")
    fun unauthorizedPage(): String {
        return "error/404"
    }

    @GetMapping("/500")
    fun error(): String {
        return "error/500"
    }

    /**
     * 获取 @RequestMapping 中配置的所有 URL.
     * @param keyword   关键字: 过滤条件
     * @return          URL 列表.
     */
    @GetMapping("/system/urls")
    @ResponseBody
    fun getUrl(@RequestParam(defaultValue = "") keyword: String?): ResultBean {
        val mapping = applicationContext.getBean(
            RequestMappingHandlerMapping::class.java
        )
        // 获取url与类和方法的对应信息
        val map = mapping.handlerMethods
        val urlSet: MutableSet<UrlVO> = HashSet()
        for ((info, value) in map) {

            // URL 类型, JSON 还是 VIEW
            var type = "view"
            if (isResponseBodyUrl(value)) {
                type = "json"
            }

            // URL 地址和 URL 请求 METHOD
            val p = info.patternsCondition
            // 一个 @RequestMapping, 可能有多个 URL.
            for (url in p.patterns) {
                // 根据 keyword 过滤 URL
                if (url.contains(keyword!!)) {

                    // 获取这个 URL 支持的所有 http method, 多个以逗号分隔, 未配置返回 ALL.
                    val methods = info.methodsCondition.methods
                    var method: String? = "ALL"
                    if (methods.size != 0) {
                        method = StringUtils.collectionToDelimitedString(methods, ",")
                    }
                    urlSet.add(UrlVO(url, method, type))
                }
            }
        }
        return ResultBean.success(urlSet)
    }

    /**
     * 判断是否返回 JSON, 判断方式有两种:
     * 1. 类上标有 ResponseBody 或 RestController 注解
     * 2. 方法上标有 ResponseBody 注解
     */
    private fun isResponseBodyUrl(handlerMethod: HandlerMethod): Boolean {
        return handlerMethod.beanType.getDeclaredAnnotation(RestController::class.java) != null || handlerMethod.beanType.getDeclaredAnnotation(
            ResponseBody::class.java
        ) != null || handlerMethod.getMethodAnnotation(ResponseBody::class.java) != null
    }
}