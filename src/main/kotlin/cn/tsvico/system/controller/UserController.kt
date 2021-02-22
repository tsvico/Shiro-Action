package cn.tsvico.system.controller

import com.github.pagehelper.PageInfo
import cn.tsvico.common.annotation.OperationLog
import cn.tsvico.common.util.PageResultBean
import cn.tsvico.common.util.ResultBean
import cn.tsvico.common.validate.groups.Create
import cn.tsvico.system.model.User
import cn.tsvico.system.service.RoleService
import cn.tsvico.system.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.annotation.Resource
import javax.validation.Valid

@Controller
@RequestMapping("/user")
class UserController {
    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var roleService: RoleService
    @GetMapping("/index")
    fun index(): String {
        return "user/user-list"
    }

    @OperationLog("获取用户列表")
    @GetMapping("/list")
    @ResponseBody
    fun getList(
        @RequestParam(value = "page", defaultValue = "1") page: Int,
        @RequestParam(value = "limit", defaultValue = "10") limit: Int,
        userQuery: User?
    ): PageResultBean<User> {
        val users = userService.selectAllWithDept(page, limit, userQuery)
        val userPageInfo = PageInfo(users)
        return PageResultBean(userPageInfo.total, userPageInfo.list)
    }

    @GetMapping
    fun add(model: Model): String {
        model.addAttribute("roles", roleService.selectAll())
        return "user/user-add"
    }

    @GetMapping("/{userId}")
    fun update(@PathVariable("userId") userId: Int?, model: Model): String {
        model.addAttribute("roleIds", userService.selectRoleIdsById(userId))
        model.addAttribute("user", userService.selectOne(userId))
        model.addAttribute("roles", roleService.selectAll())
        return "user/user-add"
    }

    @OperationLog("编辑角色")
    @PutMapping
    @ResponseBody
    fun update(
        user: @Valid User,
        @RequestParam(value = "role[]", required = false) roleIds: Array<Int>?
    ): ResultBean {
        userService.update(user, roleIds ?: emptyArray())
        return ResultBean.success()
    }

    @OperationLog("新增用户")
    @PostMapping
    @ResponseBody
    fun add(
        @Validated(Create::class) user: User,
        @RequestParam(value = "role[]", required = false) roleIds: Array<Int>?
    ): ResultBean {
        return ResultBean.success(userService.add(user, roleIds ?: emptyArray()))
    }

    @OperationLog("禁用账号")
    @PostMapping("/{userId:\\d+}/disable")
    @ResponseBody
    fun disable(@PathVariable("userId") userId: Int?): ResultBean {
        return ResultBean.success(userService.disableUserByID(userId))
    }

    @OperationLog("激活账号")
    @PostMapping("/{userId}/enable")
    @ResponseBody
    fun enable(@PathVariable("userId") userId: Int?): ResultBean {
        return ResultBean.success(userService.enableUserByID(userId))
    }

    @OperationLog("删除账号")
    @DeleteMapping("/{userId}")
    @ResponseBody
    fun delete(@PathVariable("userId") userId: Int?): ResultBean {
        userService.delete(userId)
        return ResultBean.success()
    }

    @GetMapping("/{userId}/reset")
    fun resetPassword(@PathVariable("userId") userId: Int?, model: Model): String {
        model.addAttribute("userId", userId)
        return "user/user-reset-pwd"
    }

    @OperationLog("重置密码")
    @PostMapping("/{userId}/reset")
    @ResponseBody
    fun resetPassword(@PathVariable("userId") userId: Int?, password: String?): ResultBean {
        userService.updatePasswordByUserId(userId, password)
        return ResultBean.success()
    }
}