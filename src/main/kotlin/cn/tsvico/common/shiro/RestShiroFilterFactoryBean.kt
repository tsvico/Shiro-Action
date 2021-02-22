package cn.tsvico.common.shiro

import org.apache.shiro.spring.web.ShiroFilterFactoryBean
import org.apache.shiro.web.filter.mgt.FilterChainResolver
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver
import org.apache.shiro.web.mgt.WebSecurityManager
import org.apache.shiro.web.servlet.AbstractShiroFilter
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.BeanInitializationException

class RestShiroFilterFactoryBean : ShiroFilterFactoryBean() {
    private val log = LoggerFactory.getLogger(RestShiroFilterFactoryBean::class.java)
    override fun createInstance(): AbstractShiroFilter {
        log.debug("Creating Shiro Filter instance.")
        val securityManager = securityManager
        if (securityManager == null) {
            val msg = "SecurityManager property must be set."
            throw BeanInitializationException(msg)
        }
        if (securityManager !is WebSecurityManager) {
            val msg = "The security manager does not implement the WebSecurityManager interface."
            throw BeanInitializationException(msg)
        }
        val manager = createFilterChainManager()

        //Expose the constructed FilterChainManager by first wrapping it in a
        // FilterChainResolver implementation. The AbstractShiroFilter implementations
        // do not know about FilterChainManagers - only resolvers:
        val chainResolver: PathMatchingFilterChainResolver = RestPathMatchingFilterChainResolver()
        chainResolver.filterChainManager = manager

        //Now create a concrete ShiroFilter instance and apply the acquired SecurityManager and built
        //FilterChainResolver.  It doesn't matter that the instance is an anonymous inner class
        //here - we're just using it because it is a concrete AbstractShiroFilter instance that accepts
        //injection of the SecurityManager and FilterChainResolver:
        return SpringShiroFilter(securityManager, chainResolver)
    }

    private class SpringShiroFilter(webSecurityManager: WebSecurityManager?, resolver: FilterChainResolver?) :
        AbstractShiroFilter() {
        init {
            requireNotNull(webSecurityManager) { "WebSecurityManager property cannot be null." }
            securityManager = webSecurityManager
            if (resolver != null) {
                filterChainResolver = resolver
            }
        }
    }
}