package cn.tsvico.system.controller

import cn.tsvico.common.annotation.OperationLog
import cn.tsvico.common.annotation.RefreshFilterChain
import cn.tsvico.common.util.ResultBean
import cn.tsvico.system.model.Menu
import cn.tsvico.system.service.MenuService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/menu")
class MenuController {
    @Autowired
    private lateinit var menuService: MenuService
    @GetMapping("/index")
    fun index(): String {
        return "menu/menu-list"
    }

    @OperationLog("获取菜单列表")
    @GetMapping("/list")
    @ResponseBody
    fun getList(@RequestParam(required = false) parentId: Int?): ResultBean {
        val menuList = menuService.selectByParentId(parentId)
        return ResultBean.success(menuList)
    }

    @GetMapping
    fun add(model: Model?): String {
        return "menu/menu-add"
    }

    @GetMapping("/tree")
    @ResponseBody
    fun tree(): ResultBean {
        return ResultBean.success(menuService.aLLTree)
    }

    @GetMapping("/tree/root")
    @ResponseBody
    fun treeAndRoot(): ResultBean {
        return ResultBean.success(menuService.aLLMenuTreeAndRoot)
    }

    @GetMapping("/tree/root/operator")
    @ResponseBody
    fun menuAndCountOperatorTreeAndRoot(): ResultBean {
        return ResultBean.success(menuService.aLLMenuAndCountOperatorTreeAndRoot)
    }

    @OperationLog("新增菜单")
    @RefreshFilterChain
    @PostMapping
    @ResponseBody
    fun add(menu: Menu): ResultBean {
        menuService.insert(menu)
        return ResultBean.success()
    }

    @OperationLog("删除菜单")
    @RefreshFilterChain
    @DeleteMapping("/{menuId}")
    @ResponseBody
    fun delete(@PathVariable("menuId") menuId: Int?): ResultBean {
        menuService.deleteByIDAndChildren(menuId)
        return ResultBean.success()
    }

    @GetMapping("/{menuId}")
    fun updateMenu(@PathVariable("menuId") menuId: Int?, model: Model): String {
        val menu = menuService.selectByPrimaryKey(menuId)
        model.addAttribute("menu", menu)
        return "menu/menu-add"
    }

    @OperationLog("修改菜单")
    @RefreshFilterChain
    @PutMapping
    @ResponseBody
    fun update(menu: Menu?): ResultBean {
        menuService.updateByPrimaryKey(menu)
        return ResultBean.success()
    }

    @OperationLog("调整部门排序")
    @PostMapping("/swap")
    @ResponseBody
    fun swapSort(currentId: Int?, swapId: Int?): ResultBean {
        menuService.swapSort(currentId, swapId)
        return ResultBean.success()
    }
}