package cn.tsvico.system.controller

import com.github.pagehelper.PageInfo
import cn.tsvico.common.annotation.OperationLog
import cn.tsvico.common.util.PageResultBean
import cn.tsvico.system.model.LoginLog
import cn.tsvico.system.service.LoginLogService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/log/login")
class LoginLogController {
    @Autowired
    private val loginLogService: LoginLogService? = null
    @GetMapping("/index")
    fun index(): String {
        return "log/login-logs"
    }

    @OperationLog("查看登录日志")
    @GetMapping("/list")
    @ResponseBody
    fun getList(
        @RequestParam(value = "page", defaultValue = "1") page: Int,
        @RequestParam(value = "limit", defaultValue = "10") limit: Int
    ): PageResultBean<LoginLog> {
        val loginLogs = loginLogService!!.selectAll(page, limit)
        val rolePageInfo = PageInfo(loginLogs)
        return PageResultBean(rolePageInfo.total, rolePageInfo.list)
    }
}