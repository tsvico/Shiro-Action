package im.zhaojun.system.controller

import org.springframework.web.bind.annotation.RequestMapping
import im.zhaojun.system.service.DeptService
import org.springframework.web.bind.annotation.GetMapping
import im.zhaojun.common.annotation.OperationLog
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RequestParam
import im.zhaojun.common.util.ResultBean
import im.zhaojun.system.model.Dept
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import javax.annotation.Resource

@Controller
@RequestMapping("/dept")
class DeptController {
    @Autowired
    private lateinit var deptService: DeptService
    @GetMapping("/index")
    fun index(): String {
        return "dept/dept-list"
    }

    @OperationLog("获取部门列表")
    @GetMapping("/list")
    @ResponseBody
    fun getList(@RequestParam(required = false) parentId: Int?): ResultBean {
        val deptList = deptService.selectByParentId(parentId)
        return ResultBean.success(deptList)
    }

    @GetMapping("/tree/root")
    @ResponseBody
    fun treeAndRoot(): ResultBean {
        return ResultBean.success(deptService.selectAllDeptTreeAndRoot())
    }

    @GetMapping("/tree")
    @ResponseBody
    fun tree(): ResultBean {
        return ResultBean.success(deptService.selectAllDeptTree())
    }

    @GetMapping
    fun add(): String {
        return "dept/dept-add"
    }

    @OperationLog("新增部门")
    @PostMapping
    @ResponseBody
    fun add(dept: Dept): ResultBean {
        return ResultBean.success(deptService.insert(dept))
    }

    @OperationLog("删除部门")
    @DeleteMapping("/{deptId}")
    @ResponseBody
    fun delete(@PathVariable("deptId") deptId: Int?): ResultBean {
        deptService.deleteCascadeByID(deptId)
        return ResultBean.success()
    }

    @OperationLog("修改部门")
    @PutMapping
    @ResponseBody
    fun update(dept: Dept?): ResultBean {
        deptService.updateByPrimaryKey(dept)
        return ResultBean.success()
    }

    @GetMapping("/{deptId}")
    fun update(@PathVariable("deptId") deptId: Int?, model: Model): String {
        val dept = deptService.selectByPrimaryKey(deptId)
        model.addAttribute("dept", dept)
        return "dept/dept-add"
    }

    @OperationLog("调整部门排序")
    @PostMapping("/swap")
    @ResponseBody
    fun swapSort(currentId: Int?, swapId: Int?): ResultBean {
        deptService.swapSort(currentId, swapId)
        return ResultBean.success()
    }
}