package im.zhaojun.system.service

import im.zhaojun.common.util.TreeUtil
import im.zhaojun.system.mapper.MenuMapper
import im.zhaojun.system.mapper.OperatorMapper
import im.zhaojun.system.mapper.RoleOperatorMapper
import im.zhaojun.system.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class OperatorService {
    @Autowired
    private val operatorMapper: OperatorMapper? = null

    @Autowired
    private val menuMapper: MenuMapper? = null

    @Autowired
    private val roleOperatorMapper: RoleOperatorMapper? = null
    fun deleteByPrimaryKey(operatorId: Int?) {
        // 删除分配给用户的操作权限
        roleOperatorMapper!!.deleteByOperatorId(operatorId)
        // 删除自身
        operatorMapper!!.deleteByPrimaryKey(operatorId)
    }

    fun add(operator: Operator?): Int {
        return operatorMapper!!.insert(operator)
    }

    fun selectByPrimaryKey(operatorId: Int?): Operator? {
        return operatorMapper!!.selectByPrimaryKey(operatorId)
    }

    fun updateByPrimaryKey(operator: Operator?): Int {
        return operatorMapper!!.updateByPrimaryKey(operator)
    }

    fun selectByMenuId(menuId: Int?): List<Operator?>? {
        return operatorMapper!!.selectByMenuId(menuId)
    }

    fun selectAll(): List<Operator>? {
        return operatorMapper!!.selectAll()
    }

    // 获取用户拥有的所在操作权限
    val aLLMenuAndOperatorTree: List<Menu?>
    // 获取功能权限树时, 菜单应该没有复选框, 不可选.
        // 将操作权限拼接到页面的树形结构下.
        get() {
            // 获取用户拥有的所在操作权限
            val operators = operatorMapper!!.selectAll()
            val menuList = menuMapper!!.selectAll()

            // 获取功能权限树时, 菜单应该没有复选框, 不可选.
            for (menu in menuList) {
                menu.checkArr = null
            }
            val menuTree = TreeUtil.toTree(
                menuList.toMutableList(),
                "menuId", "parentId", "children", Menu::class.java
            )
            val menuLeafNode = TreeUtil.getAllLeafNode(menuTree)

            // 将操作权限拼接到页面的树形结构下.
            for (menu in menuLeafNode) {
                var children: MutableList<Menu>? = menu.children?.toMutableList()
                if (children == null) {
                    children = ArrayList()
                }
                for (operator in operators!!) {
                    if (menu.menuId == operator.menuId) {

                        // 将操作权限转化为 Menu 结构. 由于操作权限可能与菜单权限的 ID 值冲突, 故将操作权限的 ID + 10000. 使用操作权限的 ID 时再减去这个数
                        val temp = Menu()
                        temp.menuId = operator.operatorId!! + 10000
                        temp.parentId = operator.menuId
                        temp.menuName = operator.operatorName
                        children.add(temp)
                    }
                }
                menu.children = children.toList()
            }
            return menuTree
        }
}