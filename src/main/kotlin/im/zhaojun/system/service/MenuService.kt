package im.zhaojun.system.service

import im.zhaojun.common.shiro.ShiroActionProperties
import im.zhaojun.common.util.ShiroUtil
import im.zhaojun.common.util.TreeUtil
import im.zhaojun.system.mapper.MenuMapper
import im.zhaojun.system.mapper.OperatorMapper
import im.zhaojun.system.mapper.RoleMenuMapper
import im.zhaojun.system.model.Menu
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class MenuService {
    @Autowired
    private val menuMapper: MenuMapper? = null

    @Autowired
    private val roleMenuMapper: RoleMenuMapper? = null

    @Autowired
    private val operatorMapper: OperatorMapper? = null

    @Autowired
    private val shiroActionProperties: ShiroActionProperties? = null
    fun selectByPrimaryKey(id: Int?): Menu? {
        return menuMapper!!.selectByPrimaryKey(id)
    }

    /**
     * 获取所有菜单
     */
    fun selectAll(): List<Menu?>? {
        return menuMapper!!.selectAll()
    }

    /**
     * 根据父 ID 获取所有菜单
     */
    fun selectByParentId(parentId: Int?): List<Menu?>? {
        return menuMapper!!.selectByParentId(parentId)
    }

    /**
     * 获取所有菜单 (树形结构)
     */
    val aLLTree: List<Menu>?
        get() = menuMapper!!.selectAllTree()

    /**
     * 获取所有菜单并添加一个根节点 (树形结构)
     */
    val aLLMenuTreeAndRoot: List<Menu>
        get() {
            val allMenuTree = aLLTree
            return addRootNode("导航目录", 0, allMenuTree)
        }

    /**
     * 获取所有菜单并统计菜单下的操作权限数 (树形结构)
     */
    val aLLMenuAndCountOperatorTree: List<Menu>?
        get() = menuMapper!!.selectAllMenuAndCountOperator()

    /**
     * 获取当前登陆用户拥有的树形菜单 (admin 账户拥有所有权限.)
     */
    fun selectCurrentUserMenuTree(): List<Menu?> {
        val user = ShiroUtil.currentUser
        return selectMenuTreeVOByUsername(user.username)
    }

    /**
     * 获取指定用户拥有的树形菜单 (admin 账户拥有所有权限.)
     */
    fun selectMenuTreeVOByUsername(username: String?): List<Menu> {
        val menus: List<Menu> = if (shiroActionProperties!!.superAdminUsername == username) {
            menuMapper!!.selectAll()
        } else {
            menuMapper!!.selectMenuByUserName(username)
        }
        return toTree(menus)
    }

    /**
     * 获取导航菜单中的所有叶子节点
     */
    val leafNodeMenu: List<Menu>
        get() {
            return TreeUtil.getAllLeafNode<Menu>(this.aLLTree)
        }

    fun insert(menu: Menu) {
        val maxOrderNum = menuMapper!!.selectMaxOrderNum()
        menu.orderNum = maxOrderNum + 1
        menuMapper.insert(menu)
    }

    fun updateByPrimaryKey(menu: Menu?) {
        menuMapper!!.updateByPrimaryKey(menu)
    }

    /**
     * 删除当前菜单以及其子菜单
     */
    @Transactional
    fun deleteByIDAndChildren(menuId: Int?) {
        // 删除子菜单
        val childIDList = menuMapper!!.selectChildrenIDByPrimaryKey(menuId)
        for (childID in childIDList!!) {
            deleteByIDAndChildren(childID)
        }
        // 删除菜单下的操作权限
        operatorMapper!!.deleteByMenuId(menuId)
        // 删除分配给用户的菜单
        roleMenuMapper!!.deleteByMenuId(menuId)
        // 删除自身
        menuMapper.deleteByPrimaryKey(menuId)
    }

    fun count(): Int {
        return menuMapper!!.count()
    }

    fun swapSort(currentId: Int?, swapId: Int?) {
        menuMapper!!.swapSort(currentId, swapId)
    }

    /**
     * 转换为树形结构
     */
    private fun toTree(menuList: List<Menu>): List<Menu> {
        return TreeUtil.toTree(menuList.toMutableList(), "menuId", "parentId", "children", Menu::class.java)
    }

    val aLLMenuAndCountOperatorTreeAndRoot: List<Menu>
        get() {
            val menus = aLLMenuAndCountOperatorTree
            return addRootNode("导航目录", 0, menus)
        }

    /**
     * 将树形结构添加到一个根节点下.
     */
    private fun addRootNode(rootName: String, rootId: Int, children: List<Menu>?): List<Menu> {
        val root = Menu()
        root.menuId = rootId
        root.menuName = rootName
        root.children = children
        val rootList: MutableList<Menu> = ArrayList()
        rootList.add(root)
        return rootList
    }
}