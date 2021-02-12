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
interface RoleOperatorMapper {
    fun insert(roleOperator: RoleOperator?): Int
    fun getOperatorsByRoleId(roleId: Int?): Array<Int?>?
    fun deleteByRoleId(@Param("roleId") roleId: Int?): Int
    fun insertRoleOperators(@Param("roleId") roleId: Int?, @Param("operatorIds") operatorIds: Array<Int?>?): Int
    fun deleteByOperatorId(@Param("operatorId") operatorId: Int?): Int
}