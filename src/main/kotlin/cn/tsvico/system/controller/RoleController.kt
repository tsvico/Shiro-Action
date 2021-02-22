package cn.tsvico.system.controller

import com.github.pagehelper.PageInfo
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.GetMapping
import cn.tsvico.common.annotation.OperationLog
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RequestParam
import cn.tsvico.common.util.ResultBean
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import cn.tsvico.system.service.RoleService
import cn.tsvico.common.util.PageResultBean
import cn.tsvico.system.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import javax.annotation.Resource

@Controller
@RequestMapping("/role")
class RoleController {
    @Autowired
    private lateinit var roleService: RoleService
    @GetMapping("/index")
    fun index(): String {
        return "role/role-list"
    }

    @OperationLog("查询角色列表")
    @GetMapping("/list")
    @ResponseBody
    fun getList(
        @RequestParam(value = "page", defaultValue = "1") page: Int,
        @RequestParam(value = "limit", defaultValue = "10") limit: Int,
        roleQuery: Role?
    ): PageResultBean<Role> {
        val roles = roleService.selectAll(page, limit, roleQuery)
        val rolePageInfo = PageInfo(roles)
        return PageResultBean(rolePageInfo.total, rolePageInfo.list)
    }

    @GetMapping
    fun add(): String {
        return "role/role-add"
    }

    @OperationLog("新增角色")
    @PostMapping
    @ResponseBody
    fun add(role: Role?): ResultBean {
        roleService.add(role)
        return ResultBean.success()
    }

    @GetMapping("/{roleId}")
    fun update(@PathVariable("roleId") roleId: Int?, model: Model): String {
        val role = roleService.selectOne(roleId)
        model.addAttribute("role", role)
        return "role/role-add"
    }

    @OperationLog("修改角色")
    @PutMapping
    @ResponseBody
    fun update(role: Role?): ResultBean {
        roleService.update(role)
        return ResultBean.success()
    }

    @OperationLog("删除角色")
    @DeleteMapping("/{roleId}")
    @ResponseBody
    fun delete(@PathVariable("roleId") roleId: Int?): ResultBean {
        roleService.delete(roleId)
        return ResultBean.success()
    }

    @OperationLog("为角色授予菜单")
    @PostMapping("/{roleId}/grant/menu")
    @ResponseBody
    fun grantMenu(
        @PathVariable("roleId") roleId: Int?,
        @RequestParam(value = "menuIds[]", required = false) menuIds: Array<Int?>?
    ): ResultBean {
        roleService.grantMenu(roleId, menuIds)
        return ResultBean.success()
    }

    @OperationLog("为角色授予操作权限")
    @PostMapping("/{roleId}/grant/operator")
    @ResponseBody
    fun grantOperator(
        @PathVariable("roleId") roleId: Int?,
        @RequestParam(value = "operatorIds[]", required = false) operatorIds: Array<Int?>?
    ): ResultBean {
        roleService.grantOperator(roleId, operatorIds)
        return ResultBean.success()
    }

    /**
     * 获取角色拥有的菜单
     */
    @GetMapping("/{roleId}/own/menu")
    @ResponseBody
    fun getRoleOwnMenu(@PathVariable("roleId") roleId: Int?): ResultBean {
        return ResultBean.success(roleService.getMenusByRoleId(roleId))
    }

    /**
     * 获取角色拥有的操作权限
     */
    @GetMapping("/{roleId}/own/operator")
    @ResponseBody
    fun getRoleOwnOperator(@PathVariable("roleId") roleId: Int?): ResultBean {
        val operatorIds = roleService.getOperatorsByRoleId(roleId)
        if (operatorIds != null) {
            for (i in operatorIds.indices) {
                operatorIds[i] = operatorIds[i]?.plus(10000)
            }
        }
        return ResultBean.success(operatorIds)
    }
}