package im.zhaojun.system.service

import com.github.pagehelper.PageHelper
import im.zhaojun.common.shiro.realm.UserNameRealm
import im.zhaojun.system.mapper.RoleMapper
import im.zhaojun.system.mapper.RoleMenuMapper
import im.zhaojun.system.mapper.RoleOperatorMapper
import im.zhaojun.system.mapper.UserRoleMapper
import im.zhaojun.system.model.Role
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RoleService {
    @Autowired
    private val userRoleMapper: UserRoleMapper? = null

    @Autowired
    private val roleMapper: RoleMapper? = null

    @Autowired
    private val roleMenuMapper: RoleMenuMapper? = null

    @Autowired
    private val userNameRealm: UserNameRealm? = null

    @Autowired
    private val roleOperatorMapper: RoleOperatorMapper? = null
    fun selectOne(roleId: Int?): Role? {
        return roleMapper!!.selectByPrimaryKey(roleId)
    }

    fun selectAll(page: Int, limit: Int, roleQuery: Role?): List<Role>? {
        PageHelper.startPage<Any>(page, limit)
        return selectAllByQuery(roleQuery)
    }

    fun selectAll(): List<Role?>? {
        return roleMapper!!.selectAll()
    }

    fun selectAllByQuery(roleQuery: Role?): List<Role>? {
        return roleMapper!!.selectAllByQuery(roleQuery)
    }

    @Transactional
    fun add(role: Role?) {
        try {
            roleMapper!!.insert(role)
        } catch (e: Exception) {
            throw e
        }
    }

    @Transactional
    fun update(role: Role?) {
        roleMapper!!.updateByPrimaryKey(role)
    }

    /**
     * 为角色分配菜单
     * @param roleId    角色 ID
     * @param menuIds   菜单 ID 数组
     */
    @Transactional
    fun grantMenu(roleId: Int?, menuIds: Array<Int?>?) {
        roleMenuMapper!!.deleteByRoleId(roleId)
        if (menuIds != null && menuIds.size != 0) {
            roleMenuMapper.insertRoleMenus(roleId, menuIds)
        }
        clearRoleAuthCache(roleId)
    }

    /**
     * 为角色分配操作权限
     * @param roleId    角色 ID
     * @param operatorIds   操作权限 ID 数组
     */
    @Transactional
    fun grantOperator(roleId: Int?, operatorIds: Array<Int?>?) {
        roleOperatorMapper!!.deleteByRoleId(roleId)
        if (operatorIds != null && operatorIds.size != 0) {
            for (i in operatorIds.indices) {
                operatorIds[i] = operatorIds[i]!! - 10000
            }
            roleOperatorMapper.insertRoleOperators(roleId, operatorIds)
        }
        clearRoleAuthCache(roleId)
    }

    fun count(): Int {
        return roleMapper!!.count()
    }

    @Transactional
    fun delete(roleId: Int?) {
        userRoleMapper!!.deleteUserRoleByRoleId(roleId)
        roleMapper!!.deleteByPrimaryKey(roleId)
        roleMenuMapper!!.deleteByRoleId(roleId)
        roleOperatorMapper!!.deleteByRoleId(roleId)
    }

    fun getMenusByRoleId(roleId: Int?): Array<Int?>? {
        return roleMenuMapper!!.getMenusByRoleId(roleId)
    }

    fun getOperatorsByRoleId(roleId: Int?): Array<Int?>? {
        return roleOperatorMapper!!.getOperatorsByRoleId(roleId)
    }

    private fun clearRoleAuthCache(roleId: Int?) {
        // 获取该角色下的所有用户.
        val userIds = userRoleMapper!!.selectUserIdByRoleId(roleId)

        // 将该角色下所有用户的认证信息缓存清空, 以到达刷新认证信息的目的.
        for (userId in userIds!!) {
            userNameRealm!!.clearAuthCacheByUserId(userId!!)
        }
    }
}