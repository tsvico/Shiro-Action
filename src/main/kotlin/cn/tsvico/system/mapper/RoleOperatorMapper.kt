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
interface RoleOperatorMapper {
    fun insert(roleOperator: RoleOperator?): Int
    fun getOperatorsByRoleId(roleId: Int?): Array<Int?>?
    fun deleteByRoleId(@Param("roleId") roleId: Int?): Int
    fun insertRoleOperators(@Param("roleId") roleId: Int?, @Param("operatorIds") operatorIds: Array<Int?>?): Int
    fun deleteByOperatorId(@Param("operatorId") operatorId: Int?): Int
}