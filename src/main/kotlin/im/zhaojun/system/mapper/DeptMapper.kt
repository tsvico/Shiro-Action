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
interface DeptMapper {
    fun deleteByPrimaryKey(deptId: Int?): Int
    fun insert(dept: Dept?): Int
    fun selectByPrimaryKey(deptId: Int?): Dept?
    fun updateByPrimaryKey(dept: Dept?): Int
    fun selectByParentId(@Param("parentId") parentId: Int?): List<Dept?>?
    fun selectAllTree(): List<Dept>?
    fun selectChildrenIDByPrimaryKey(@Param("deptId") deptId: Int?): List<Int?>?
    fun selectMaxOrderNum(): Int
    fun swapSort(@Param("currentId") currentId: Int?, @Param("swapId") swapId: Int?): Int
}