package im.zhaojun.system.mapper

import im.zhaojun.system.model.Dept
import im.zhaojun.system.model.SysLog
import im.zhaojun.system.model.LoginLog
import im.zhaojun.system.model.RoleMenu
import im.zhaojun.system.model.UserRole
import im.zhaojun.system.model.UserAuths
import im.zhaojun.system.model.RoleOperator
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface RoleMenuMapper {
    fun insert(roleMenu: RoleMenu?): Int

    /**
     * 插入多条 角色-菜单 关联关系
     */
    fun insertRoleMenus(@Param("roleId") roleId: Int?, @Param("menuIds") menuIds: Array<Int?>?): Int

    /**
     * 清空角色所拥有的所有菜单
     */
    fun deleteByRoleId(@Param("roleId") roleId: Int?): Int

    /**
     * 取消某个菜单的所有授权用户
     */
    fun deleteByMenuId(@Param("menuId") menuId: Int?): Int
    fun getMenusByRoleId(@Param("roleId") roleId: Int?): Array<Int?>?
}