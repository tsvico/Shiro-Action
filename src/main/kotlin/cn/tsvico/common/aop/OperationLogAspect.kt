package cn.tsvico.common.aop

import cn.tsvico.common.annotation.OperationLog
import cn.tsvico.common.util.IPUtils
import cn.tsvico.common.util.ShiroUtil
import cn.tsvico.system.mapper.SysLogMapper
import cn.tsvico.system.model.SysLog
import org.apache.shiro.SecurityUtils
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.LocalVariableTableParameterNameDiscoverer
import org.springframework.stereotype.Component

/**
 * 操作日志切面.
 */
@Aspect
@Component
@ConditionalOnProperty(value = ["shiro-action.log.operation"], havingValue = "true")
class OperationLogAspect {
    @Autowired
    private val sysLogMapper: SysLogMapper? = null
    @Pointcut("@annotation(cn.tsvico.common.annotation.OperationLog)")
    fun pointcut() {
    }

    @Around(value = "pointcut()")
    @Throws(Throwable::class)
    fun around(point: ProceedingJoinPoint): Any {
        val result: Any
        val beginTime = System.currentTimeMillis()
        // 执行方法
        result = point.proceed()
        // 执行时长
        val time = System.currentTimeMillis() - beginTime
        // 保存日志
        saveLog(point, time)
        return result
    }

    private fun saveLog(joinPoint: ProceedingJoinPoint, time: Long) {
        val signature = joinPoint.signature as MethodSignature
        val method = signature.method
        val sysLog = SysLog()

        // 获取注解上的操作描述
        val operationLogAnnotation = method.getAnnotation(OperationLog::class.java)
        if (operationLogAnnotation != null) {
            sysLog.operation = operationLogAnnotation.value
        }

        // 请求的方法名
        val className = joinPoint.target.javaClass.name
        val methodName = signature.name
        sysLog.method = "$className.$methodName()"

        // 请求的方法参数
        val args = joinPoint.args
        val parameterNameDiscoverer = LocalVariableTableParameterNameDiscoverer()
        val paramNames = parameterNameDiscoverer.getParameterNames(method)
        if (args != null && paramNames != null) {
            val params = StringBuilder()
            for (i in args.indices) {
                params.append("  ").append(paramNames[i]).append(": ").append(args[i])
            }
            sysLog.params = params.toString()
        }
        sysLog.ip = IPUtils.ipAddr

        // 获取当前登录用户名
        if (SecurityUtils.getSubject().isAuthenticated) {
            val user = ShiroUtil.currentUser
            sysLog.username = user.username
        }
        sysLog.time = time.toInt()
        sysLogMapper!!.insert(sysLog)
    }
}