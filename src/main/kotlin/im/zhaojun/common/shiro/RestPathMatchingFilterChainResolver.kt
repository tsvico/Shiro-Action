package im.zhaojun.common.shiro

import im.zhaojun.common.util.WebHelper
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver
import org.slf4j.LoggerFactory
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse

class RestPathMatchingFilterChainResolver : PathMatchingFilterChainResolver() {
    private val log = LoggerFactory.getLogger(RestPathMatchingFilterChainResolver::class.java)
    override fun getChain(request: ServletRequest, response: ServletResponse, originalChain: FilterChain): FilterChain? {
        val filterChainManager = filterChainManager
        if (!filterChainManager.hasChains()) {
            return null
        }
        val requestURI = getPathWithinApplication(request)

        // the 'chain names' in this implementation are actually path patterns defined by the user.  We just use them
        // as the chain name for the FilterChainManager's requirements
        for (pathPattern in filterChainManager.chainNames) {
            val pathPatternArray = pathPattern.split("==").toTypedArray()
            var httpMethodMatchFlag = true
            if (pathPatternArray.size > 1) {
                httpMethodMatchFlag = pathPatternArray[1] == WebHelper.requestHTTPMethod
            }

            // 只用过滤器链的 URL 部分与请求的 URL 进行匹配
            if (pathMatches(pathPatternArray[0], requestURI) && httpMethodMatchFlag) {
                if (log.isTraceEnabled) {
                    log.trace(
                        "Matched path pattern [" + pathPattern + "] for requestURI [" + requestURI + "].  " +
                                "Utilizing corresponding filter chain..."
                    )
                }
                return filterChainManager.proxy(originalChain, pathPattern)
            }
        }
        return null
    }
}