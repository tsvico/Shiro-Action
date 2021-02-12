package im.zhaojun.system.service

import cn.hutool.core.util.ReflectUtil
import im.zhaojun.common.shiro.realm.UserNameRealm
import im.zhaojun.system.service.ShiroService
import org.apache.shiro.ShiroException
import org.apache.shiro.spring.web.ShiroFilterFactoryBean
import org.apache.shiro.web.filter.PathMatchingFilter
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver
import org.apache.shiro.web.servlet.AbstractShiroFilter
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import java.util.*

@Service
class ShiroService {
    @Lazy
    @Autowired
    private lateinit var shiroFilterFactoryBean: ShiroFilterFactoryBean

    @Autowired
    private lateinit var menuService: MenuService

    @Autowired
    private lateinit var operatorService: OperatorService

    @Autowired
    private lateinit var userNameRealm: UserNameRealm// 系统默认过滤器

    /**
     * 从数据库加载用户拥有的菜单权限和 API 权限.
     */
    val urlPermsMap: Map<String, String>
        get() {
            val filterChainDefinitionMap: MutableMap<String, String> = LinkedHashMap()

            // 系统默认过滤器
            filterChainDefinitionMap["/favicon.ico"] = "anon"
            filterChainDefinitionMap["/css/**"] = "anon"
            filterChainDefinitionMap["/fonts/**"] = "anon"
            filterChainDefinitionMap["/images/**"] = "anon"
            filterChainDefinitionMap["/js/**"] = "anon"
            filterChainDefinitionMap["/lib/**"] = "anon"
            filterChainDefinitionMap["/active/**"] = "anon"
            filterChainDefinitionMap["/login"] = "anon"
            filterChainDefinitionMap["/register"] = "anon"
            filterChainDefinitionMap["/403"] = "anon"
            filterChainDefinitionMap["/404"] = "anon"
            filterChainDefinitionMap["/500"] = "anon"
            filterChainDefinitionMap["/error"] = "anon"
            filterChainDefinitionMap["/oauth2/callback/*"] = "oauth2Authc"
            filterChainDefinitionMap["/oauth2/render/*"] = "anon"
            filterChainDefinitionMap["/oauth2/error"] = "anon"
            filterChainDefinitionMap["/captcha"] = "anon"
            val menuList = menuService.leafNodeMenu
            for (menu in menuList) {
                val url = menu.url
                if (url != null) {
                    val perms = "perms[" + menu.perms + "]"
                    filterChainDefinitionMap[url] = perms
                }
            }
            val operatorList = operatorService.selectAll() ?: emptyList()
            for (operator in operatorList) {
                var url = operator.url
                if (url != null) {
                    if (operator.httpMethod != null
                        && "" != operator.httpMethod
                    ) {
                        url += "==" + operator.httpMethod
                    }
                    val perms = "perms[" + operator.perms + "]"
                    filterChainDefinitionMap[url] = perms
                }
            }
            filterChainDefinitionMap["/**"] = "authc"
            return filterChainDefinitionMap
        }

    /**
     * 更新 Shiro 过滤器链
     */
    fun updateFilterChain() {
        synchronized(shiroFilterFactoryBean) {
            val shiroFilter: AbstractShiroFilter
            shiroFilter = try {
                shiroFilterFactoryBean
                    .getObject() as AbstractShiroFilter
            } catch (e: Exception) {
                throw ShiroException(
                    "get ShiroFilter from shiroFilterFactoryBean error!"
                )
            }
            val filterChainResolver = shiroFilter
                .filterChainResolver as PathMatchingFilterChainResolver
            val manager = filterChainResolver
                .filterChainManager as DefaultFilterChainManager
            // 清空老的权限控制
            manager.filterChains.clear()
            shiroFilterFactoryBean.filterChainDefinitionMap.clear()
            shiroFilterFactoryBean.filterChainDefinitionMap = urlPermsMap
            userNameRealm.clearAllAuthCache()

            // 清除每个 Filter 中的 appliedPaths 信息
            for ((_, value) in manager.filters) {
                if (value is PathMatchingFilter) {
                    val appliedPaths = ReflectUtil.getFieldValue(value, "appliedPaths") as MutableMap<String, Any>
                    synchronized(appliedPaths) { appliedPaths.clear() }
                }
            }

            // 重新构建生成
            val chains = shiroFilterFactoryBean
                .filterChainDefinitionMap
            for ((url, value) in chains) {
                val chainDefinition = value.trim { it <= ' ' }.replace(" ", "")
                manager.createChain(url, chainDefinition)
            }
            log.info("更新 Shiro 过滤器链")
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(ShiroService::class.java)
    }
}