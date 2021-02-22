package cn.tsvico.system.controller

import cn.tsvico.common.util.PageResultBean
import cn.tsvico.common.util.ResultBean
import cn.tsvico.system.model.UserOnline
import cn.tsvico.system.service.UserOnlineService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/online")
class UserOnlineController {
    @Autowired
    private lateinit var userOnlineService: UserOnlineService
    @GetMapping("/index")
    fun index(): String {
        return "online/user-online-list"
    }

    @GetMapping("/list")
    @ResponseBody
    fun online(): PageResultBean<UserOnline> {
        val list = userOnlineService.list()
        return PageResultBean(list.size.toLong(), list)
    }

    @PostMapping("/kickout")
    @ResponseBody
    fun forceLogout(sessionId: String?): ResultBean {
        userOnlineService.forceLogout(sessionId)
        return ResultBean.success()
    }
}