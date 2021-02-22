package cn.tsvico.system.mapper

import cn.tsvico.system.model.Dept
import cn.tsvico.system.model.SysLog
import cn.tsvico.system.model.LoginLog
import cn.tsvico.system.model.RoleMenu
import cn.tsvico.system.model.UserRole
import cn.tsvico.system.model.UserAuths
import cn.tsvico.system.model.RoleOperator
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