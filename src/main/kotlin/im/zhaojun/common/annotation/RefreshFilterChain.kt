package im.zhaojun.common.annotation

/**
 * 标记某个方法会引发 Shiro 过滤器链的变化, 将刷新过滤器链.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class RefreshFilterChain 