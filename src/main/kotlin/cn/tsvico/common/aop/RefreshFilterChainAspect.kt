package cn.tsvico.common.aop

import cn.tsvico.system.service.ShiroService
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
    @Pointcut("@annotation(cn.tsvico.common.annotation.RefreshFilterChain)")
    fun updateFilterChain() {
    }

    @AfterReturning("updateFilterChain()")
    fun doAfter() {
        shiroService!!.updateFilterChain()
    }
}