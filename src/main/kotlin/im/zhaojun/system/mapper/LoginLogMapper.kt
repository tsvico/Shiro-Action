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
interface LoginLogMapper {
    fun deleteByPrimaryKey(id: Int?): Int
    fun insert(loginLog: LoginLog?): Int
    fun selectByPrimaryKey(id: Int?): LoginLog?
    fun updateByPrimaryKey(loginLog: LoginLog?): Int
    fun selectAll(): List<LoginLog>
    fun recentlyWeekLoginCount(@Param("username") username: String?): List<Int>
    fun count(): Int
}