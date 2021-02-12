package im.zhaojun.common.aop

import im.zhaojun.system.service.ShiroService
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * 更新过滤器链
 */
@Aspect
@Component
class RefreshFilterChainAspect {
    @Autowired
    private val shiroService: ShiroService? = null
    @Pointcut("@annotation(im.zhaojun.common.annotation.RefreshFilterChain)")
    fun updateFilterChain() {
    }

    @AfterReturning("updateFilterChain()")
    fun doAfter() {
        shiroService!!.updateFilterChain()
    }
}