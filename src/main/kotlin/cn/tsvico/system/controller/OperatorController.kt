package cn.tsvico.system.controller

import cn.tsvico.common.annotation.OperationLog
import cn.tsvico.common.annotation.RefreshFilterChain
import cn.tsvico.common.util.ResultBean
import cn.tsvico.system.model.Operator
import cn.tsvico.system.service.OperatorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import javax.annotation.Resource

@Controller
@RequestMapping("/operator")
class OperatorController {
    @Autowired
    private lateinit var operatorService: OperatorService
    @OperationLog("查看操作日志")
    @GetMapping("/index")
    fun index(): String {
        return "operator/operator-list"
    }

    @GetMapping
    fun add(): String {
        return "operator/operator-add"
    }

    @RefreshFilterChain
    @PostMapping
    @ResponseBody
    fun add(operator: Operator?): ResultBean {
        operatorService.add(operator)
        return ResultBean.success()
    }

    @GetMapping("/{operatorId}")
    fun update(model: Model, @PathVariable("operatorId") operatorId: Int?): String {
        val operator = operatorService.selectByPrimaryKey(operatorId)
        model.addAttribute("operator", operator)
        return "operator/operator-add"
    }

    @RefreshFilterChain
    @PutMapping
    @ResponseBody
    fun update(operator: Operator?): ResultBean {
        operatorService.updateByPrimaryKey(operator)
        return ResultBean.success()
    }

    @GetMapping("/list")
    @ResponseBody
    fun getList(@RequestParam(required = false) menuId: Int?): ResultBean {
        val operatorList = operatorService.selectByMenuId(menuId)
        return ResultBean.success(operatorList)
    }

    @RefreshFilterChain
    @DeleteMapping("/{operatorId}")
    @ResponseBody
    fun delete(@PathVariable("operatorId") operatorId: Int?): ResultBean {
        operatorService.deleteByPrimaryKey(operatorId)
        return ResultBean.success()
    }

    @GetMapping("/tree")
    @ResponseBody
    fun tree(): ResultBean {
        return ResultBean.success(operatorService.aLLMenuAndOperatorTree)
    }
}