package cn.tsvico.common.config

import cn.tsvico.common.interceptor.LogMDCInterceptor
import cn.tsvico.common.interceptor.RequestLogHandlerInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.util.*

@Configuration
class WebMvcConfig : WebMvcConfigurer {
    @Autowired
    private lateinit var logHandlerInterceptor: RequestLogHandlerInterceptor

    @Autowired
    private lateinit var shiroMDCInterceptor: LogMDCInterceptor

    /**
     * 添加拦截器
     */
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(shiroMDCInterceptor)
            .excludePathPatterns(listOf("/css/**", "/fonts/**", "/images/**", "/js/**", "/lib/**", "/error"))
        registry.addInterceptor(logHandlerInterceptor)
            .excludePathPatterns(listOf("/css/**", "/fonts/**", "/images/**", "/js/**", "/lib/**", "/error"))
    }
}