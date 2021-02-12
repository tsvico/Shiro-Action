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
interface UserRoleMapper {
    fun insert(userRole: UserRole?): Int

    /**
     * 插入多条 用户色-角色 关联关系
     */
    fun insertList(@Param("userId") userId: Int?, @Param("roleIds") roleIds: Array<Int>): Int

    /**
     * 清空用户所拥有的所有角色
     */
    fun deleteUserRoleByUserId(@Param("userId") userId: Int?): Int

    /**
     * 清空此角色与所有角色的关联关系
     */
    fun deleteUserRoleByRoleId(@Param("roleId") roleId: Int?): Int
    fun selectUserIdByRoleId(@Param("roleId") roleId: Int?): List<Int?>?
}