package cn.tsvico.common.annotation

/**
 * 标记注解, 用于记录方法调用日志.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class OperationLog(val value: String)