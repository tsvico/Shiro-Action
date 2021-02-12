package im.zhaojun.system.controller

import im.zhaojun.common.annotation.OperationLog
import im.zhaojun.common.information.Server
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class SystemController {
    @OperationLog("查看系统信息")
    @GetMapping("/system/index")
    @Throws(Exception::class)
    fun index(model: Model): String {
        val server = Server()
        server.copyTo()
        model.addAttribute("server", server)
        return "system/index"
    }
}