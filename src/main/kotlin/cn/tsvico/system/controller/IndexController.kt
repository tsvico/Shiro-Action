package cn.tsvico.system.controller

import cn.tsvico.common.annotation.OperationLog
import cn.tsvico.system.service.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import javax.annotation.Resource

@Controller
class IndexController {
    @Autowired
    private lateinit var menuService: MenuService

    @Autowired
    private lateinit var loginLogService: LoginLogService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var roleService: RoleService

    @Autowired
    private lateinit var sysLogService: SysLogService

    @Autowired
    private lateinit var userOnlineService: UserOnlineService
    @GetMapping(value = ["/", "/main"])
    fun index(model: Model): String {
        val menuTreeVOS = menuService.selectCurrentUserMenuTree()
        model.addAttribute("menus", menuTreeVOS)
        return "index"
    }

    @OperationLog("访问我的桌面")
    @GetMapping("/welcome")
    fun welcome(model: Model): String {
        val userCount = userService.count()
        val roleCount = roleService.count()
        val menuCount = menuService.count()
        val loginLogCount = loginLogService.count()
        val sysLogCount = sysLogService.count()
        val userOnlineCount = userOnlineService.count()
        model.addAttribute("userCount", userCount)
        model.addAttribute("roleCount", roleCount)
        model.addAttribute("menuCount", menuCount)
        model.addAttribute("loginLogCount", loginLogCount)
        model.addAttribute("sysLogCount", sysLogCount)
        model.addAttribute("userOnlineCount", userOnlineCount)
        return "welcome"
    }

    @OperationLog("查看近七日登录统计图")
    @GetMapping("/weekLoginCount")
    @ResponseBody
    fun recentlyWeekLoginCount(): List<Int> {
        return loginLogService.recentlyWeekLoginCount()
    }
}